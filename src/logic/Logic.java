package logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import shared.LogicToUi;
import shared.Task.TaskType;
import storage.Database;
import storage.NoMoreUndoStepsException;
import storage.WillNotWriteToCorruptFileException;
import shared.Task;
import shared.SearchTerms;
import storage.Database.DB_File_Status;

public class Logic {
	public static enum CommandType {
		ADD, DELETE, LIST, SEARCH, UNDO, FILE_STATUS, REFRESH
	};

	public static Database dataBase = new Database();
	private static ArrayList<Task> lastShownToUI = new ArrayList<Task>();
	public static String latestRefreshCommandForGUI = "list";
	public static String latestCommandFromUI = null;


	public static LogicToUi uiCommunicator(String command) {
		LogicToUi feedback;
		try {
			
			latestCommandFromUI = new String(command);
			CommandType commandType = parseCommand(command);

			
			feedback = executeCommand(commandType, command);
		} catch (NoSuchCommandException e) {
			feedback = new LogicToUi(
					"Sorry but I could not understand you. Can you rephrase the message?");
		} 

		return feedback;
	}

	private static CommandType parseCommand(String command)
			throws NoSuchCommandException {
		String commandSyntax = command.trim().split(" ")[0];
		command = command.replaceFirst(commandSyntax, "").trim();
		CommandType typeOfCommand = determineCommandType(commandSyntax);
		return typeOfCommand;
	}

	private static CommandType determineCommandType(String string)
			throws NoSuchCommandException {
		
		
		switch (string.toLowerCase()) {
		case "add":
			return CommandType.ADD;
		case "delete":
			return CommandType.DELETE;
		case "list":
			return CommandType.LIST;
		case "search":
			return CommandType.SEARCH;
		case "undo":
			return CommandType.UNDO;
		case "filestatus":
			return CommandType.FILE_STATUS;
		case "refresh":
			return CommandType.REFRESH;
		default:
			throw new NoSuchCommandException();
		}
	}

	private static LogicToUi executeCommand(CommandType commandType,
			String arguments) {
		
		
		String[] splitArguments = arguments.split(" ", 2);
		switch (commandType) {
		case ADD:
			return addTask(arguments);
		case DELETE:
			return deleteTask(splitArguments);
		case LIST:
			return list(splitArguments);
		case FILE_STATUS:
			return checkfileStatus();
		case UNDO:
			return undo();
		case SEARCH:
			return search(splitArguments);
		case REFRESH:
			return refresh();
		default:
			return null;
		}
	}
	private static LogicToUi refresh() {
		return uiCommunicator(latestRefreshCommandForGUI);
	}

	//Can only search by keywords for now
	private static LogicToUi search(String[] splitArguments) {
		
		if(splitArguments.length == 0) {
			return new LogicToUi(
					"No search terms specified.");
		}
		String[] keywords = splitArguments[1].split(" ");
		
		SearchTerms terms = new SearchTerms(keywords);
		ArrayList<Task> results = dataBase.search(terms);
		lastShownToUI = results;
		latestRefreshCommandForGUI = new String(latestCommandFromUI);
		
		return new LogicToUi(results);
	}

	private static LogicToUi undo() {
		
		
		try {
			if(dataBase.getUndoStepsLeft() == 0) {
				throw new NoMoreUndoStepsException();
			}
			
			dataBase.undo();
			
			return new LogicToUi("Undo Successful");
		} catch (NoMoreUndoStepsException e) {
			return new LogicToUi("You don't have any more undo steps left");
		} catch (IOException e) {
			return new LogicToUi(
					"In/Out error. Please restart the program.");
		} catch (WillNotWriteToCorruptFileException e) {
			return new LogicToUi("File is corrupted. Please check :(.");
		}
		
	}

	private static LogicToUi checkfileStatus() {
		DB_File_Status status = dataBase.getFileAttributes();
		
		if(status.equals(DB_File_Status.FILE_ALL_OK)) {
			return new LogicToUi("Database file is ready!");
		} else if(status.equals(DB_File_Status.FILE_READ_ONLY)) {
			return new LogicToUi("Database file is read-only. You can only view but not make changes");
		} else if(status.equals(DB_File_Status.FILE_IS_CORRUPT)) {
			return new LogicToUi("The database file is corrupt. DoIt has attempted to read in as much as possible. You will not be able to write to the file until the file is cleared");
		} else {
			return new LogicToUi("Unknown error with database file");
		}
	}

	private static LogicToUi list(String[] splitArguments) {
		
		if(splitArguments.length == 1) {
			
			lastShownToUI = dataBase.readAll();
			latestRefreshCommandForGUI = new String(latestCommandFromUI);
			return new LogicToUi(lastShownToUI);
		}
		


		String[] parameters = splitArguments[1].split(" ");

		boolean complete = false;
		boolean incomplete = false;
		boolean timed = false;
		boolean deadline = false;
		boolean floating = false;

		for (String eachParam : parameters) {

			if (eachParam.equals("completed") || eachParam.equals("done")) {
				complete = true;
			}

			if (eachParam.equals("incomplete") || eachParam.equals("undone")) {
				incomplete = true;
			}

			if (eachParam.equals("timed")) {
				timed = true;
			}

			if (eachParam.equals("deadline")) {
				deadline = true;
			}

			if (eachParam.equals("floating")) {
				floating = true;
			}

		}

		SearchTerms filter = new SearchTerms(complete, incomplete, timed,
				deadline, floating);
		ArrayList<Task> results = dataBase.search(filter);

		lastShownToUI = results;
		latestRefreshCommandForGUI = new String(latestCommandFromUI);
		return new LogicToUi(results);
	}

	private static LogicToUi deleteTask(String[] splitArguments){
		if(splitArguments.length == 1) {
			return new LogicToUi(
					"Sorry this index number you provided is not valid. Please try again with a correct number or refresh the list.");
		}
		
		int index;

		
		try {
			index = Integer.parseInt(splitArguments[1]);
			index--; //Since arraylist index starts from 0
			
			if((index < 0) || ((index  +  1) > lastShownToUI.size()) ) {
				throw new NoSuchElementException();
			}
			
			int serial = lastShownToUI.get(index).getSerial();
			
			Task toBeDeleted = dataBase.locateATask(serial);
			dataBase.delete(serial);

			String taskDetails = taskToString(toBeDeleted);
			return new LogicToUi(taskDetails + " has been deleted.");
		} catch (NoSuchElementException e) {
			return new LogicToUi(
					"Sorry this index number you provided is not valid. Please try again with a correct number or refresh the list.");
		} catch (IOException e) {
			return new LogicToUi(
					"In/Out error. Please restart the program.");
		} catch (WillNotWriteToCorruptFileException e) {
			return new LogicToUi("File is corrupted. Please check :(.");
		}


	}

	private static String taskToString(Task toBeConverted) {
		
		if(toBeConverted.isTimedTask()) {
			return("Timed task " + "\"" + toBeConverted.getTaskName() + "\"" + " from " + dateToString(toBeConverted.getStartTime()) + " to " + dateToString(toBeConverted.getEndTime()));
		} else if(toBeConverted.isDeadlineTask()) {
			return("Deadline task " + "\"" + toBeConverted.getTaskName() + "\"" + " by " + dateToString(toBeConverted.getDeadline()));
		} else {
			return ("Floating task " + "\"" + toBeConverted.getTaskName() + "\"");
		}

	}
	private static String dateToString(DateTime inputDate){
		String LINE_DATE_FORMAT = "dd MMM yy hh:mma";
		DateTimeFormatter LINE_DATE_FORMATTER = DateTimeFormat.forPattern(LINE_DATE_FORMAT);
		return LINE_DATE_FORMATTER.print(inputDate);
	}

	private static LogicToUi addTask(String arguments) {
		try {
			TaskType taskType = AddParser.getType(arguments);
			switch (taskType) {
			case FLOATING:
				try {
					dataBase.add(new Task(arguments));
				} catch (WillNotWriteToCorruptFileException e) {
					return new LogicToUi(
							"File corrupted. Please fix this first :(");
				}
				return new LogicToUi("Event " + arguments + "added");
			case DEADLINE:
				DateTime dt = AddParser.getEndTime(arguments);
				String taskName = AddParser.getTaskName(arguments);
				try {
					dataBase.add(new Task(taskName, dt));
					return new LogicToUi("Event " + taskName + "added");
				} catch (IOException e) {
					return new LogicToUi(
							"In/Out error. Please restart the program.");
				} catch (WillNotWriteToCorruptFileException e) {
					return new LogicToUi("File is corrupted. Please check :(.");
				}

			case TIMED:
				DateTime st = AddParser.getBeginTime(arguments);
				DateTime et = AddParser.getEndTime(arguments);
				String newTaskName = AddParser.getTaskName(arguments);
				try {
					dataBase.add(new Task(newTaskName, st, et));
					return new LogicToUi("Event " + newTaskName + "added");
				} catch (IOException e) {
					return new LogicToUi(
							"In/Out error.Please restart the program.");
				} catch (WillNotWriteToCorruptFileException e) {
					return new LogicToUi("File is corrupted. Please check :(.");
				}
			}
		} catch (IOException e) {

			return new LogicToUi(
					"Something is wrong with the file. I cannot write to it. Please check the permission"
							+ "for the file");
		}
		return new LogicToUi(
				"I could not determine the type of your event. Can you be more specific?");
		// TODO Auto-generated method stub

	}

	public Logic() {
		dataBase = new Database();

	}

}
