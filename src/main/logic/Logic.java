package main.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.joestelmach.natty.CalendarSource;
import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import main.shared.LogicToUi;
import main.shared.LogicToUi.SortStatus;
import main.shared.SearchTerms;
import main.shared.Task.SortByEndDate;
import main.shared.Task.SortByStartDate;
import main.storage.Database;
import main.storage.NoMoreUndoStepsException;
import main.storage.WillNotWriteToCorruptFileException;
import main.shared.Task;

import main.storage.Database.DB_File_Status;

public class Logic {
	private final String ERROR_IO = "Something is wrong with the file. I cannot write to it. Please check your file permissions.";
	private final String ERROR_FILE_CORRUPTED = "File is corrupted. Please rectify the problem or delete the database file and restart DoIT. :(";

	public enum CommandType {
		ADD, DELETE, LIST, SEARCH, SEARCH_PARTIAL, UNDO, FILE_STATUS, REFRESH, DONE, UNDONE, SORT, EDIT, POSTPONE, EXIT
	};

	private CommandHandler executor;
	public Database dataBase = null;
	public static ArrayList<Task> lastShownToUI = new ArrayList<Task>();
	private String latestRefreshCommandForUI = "list";

	private String latestCommandFromUI = null;
	private Stack<String> undoHistory = new Stack<String>();

	private Comparator<Task> latestSorter = new SortByStartDate();
	private SortStatus latestSorting = SortStatus.START;
	private String latestSortCommand = "sort start";

	private static Logic theOne = null;

	public static Logic getInstance() {
		if (theOne == null) {
			theOne = new Logic();
			new AddParser("some dummy arguments so I can use DoIt now!");
		}

		return theOne;
	}

	public LogicToUi uiCommunicator(String command) {
		LogicToUi feedback;
		try {

			latestCommandFromUI = new String(command);
			CommandType commandType = parseCommand(command);

			String arguments = command.replaceFirst(
					command.trim().split(" ")[0], "").trim();
			feedback = executeCommand(commandType, arguments);
		} catch (NoSuchCommandException e) {
			feedback = new LogicToUi(
					"Sorry but I could not understand you. Can you rephrase the message?");
		}
		return feedback;
	}

	private CommandType parseCommand(String command)
			throws NoSuchCommandException {
		String commandSyntax = command.trim().split(" ")[0];

		CommandType typeOfCommand = determineCommandType(commandSyntax);
		return typeOfCommand;
	}

	private CommandType determineCommandType(String string)
			throws NoSuchCommandException {

		switch (string.toLowerCase()) {
		case "add":
			// Fallthrough
		case "a":
			return CommandType.ADD;
		case "delete":
			// Fallthrough
		case "de":
			// Fallthrough
		case "del":
			return CommandType.DELETE;

		case "list":
			// Fallthrough
		case "l":
			// Fallthrough
		case "ls":
			return CommandType.LIST;

		case "search":
			return CommandType.SEARCH;
		case "searchpartial":
			return CommandType.SEARCH_PARTIAL;

		case "undo":
			// Fallthrough
		case "u":
			return CommandType.UNDO;
		case "sort":
			return CommandType.SORT;
		case "filestatus":
			return CommandType.FILE_STATUS;
		case "refresh":
			return CommandType.REFRESH;
		case "done":
			return CommandType.DONE;
		case "undone":
			return CommandType.UNDONE;
		case "edit":
			// fall through
		case "edi":
			// fall through
		case "upd":
			// fall through
		case "update":
			return CommandType.EDIT;
		case "postpone":
			return CommandType.POSTPONE;
		case "exit":
			// Fallthrough
		case "quit":
			return CommandType.EXIT;
		default:
			throw new NoSuchCommandException();
		}
	}

	private LogicToUi executeCommand(CommandType commandType, String arguments) {

		switch (commandType) {
		case ADD:
			return addTask(arguments);
		case DELETE:
			return deleteTask(arguments);
		case LIST:
			return list(arguments);
		case FILE_STATUS:
			return checkFileStatus();
		case UNDO:
			return undo();
		case SEARCH:
			return search(arguments);
		case SEARCH_PARTIAL:
			return searchPartial(arguments);
		case REFRESH:
			return refresh();
		case DONE:
			return done(arguments, true);
		case UNDONE:
			return done(arguments, false);
		case SORT:
			return sort(arguments);
		case EDIT:
			return editTask(arguments);
		case POSTPONE:
			return postpone(arguments);
		case EXIT:
			return exit();
		default:
			return null;
		}
	}

	private LogicToUi exit() {
		dataBase.unlockFileToExit();
		return new LogicToUi("Exiting DoIt");
	}

	private LogicToUi postpone(String arguments) {
		executor = new PostponeHandler(arguments);
		return executor.execute();
	}

	private LogicToUi editTask(String argument) {

		executor = new EditHandler(argument);
		return executor.execute();
	}

	private LogicToUi sort(String arguments) {

		if (arguments.length() == 0) {
			arguments = "start";
		}

		boolean needToReverse = false;
		if (arguments.contains("reverse")) {
			needToReverse = true;
		}

		Comparator<Task> sorter = null;

		// Refresh the list first before sorting
		LogicToUi fromListCommand = this
				.uiCommunicator(latestRefreshCommandForUI);
		SearchTerms searchFilters = fromListCommand.getFilters();
		String listStatusMsg = fromListCommand.getString();

		String statusMsg = null;

		if (arguments.contains("type")) {
			sorter = new Task.SortByType();
			statusMsg = "sorted by Type";
			latestSortCommand = "sort " + arguments;
			latestSorting = SortStatus.TYPE;

		} else if (arguments.contains("done")) {
			sorter = new Task.SortByDone();
			statusMsg = "sorted by Done";
			latestSortCommand = "sort " + arguments;
			latestSorting = SortStatus.DONE;

		} else if (arguments.contains("start")) {
			sorter = new SortByStartDate();
			statusMsg = "sorted by Start Date/Deadline";
			latestSortCommand = "sort " + arguments;
			latestSorting = SortStatus.START;

		} else if (arguments.contains("end")) {
			sorter = new SortByEndDate();
			statusMsg = "sorted by End Date/Deadline";
			latestSortCommand = "sort " + arguments;
			latestSorting = SortStatus.END;

		} else if (arguments.contains("name")) {
			sorter = new Task.SortByName();
			statusMsg = "sorted by Name";
			latestSortCommand = "sort " + arguments;
			latestSorting = SortStatus.NAME;

		} else {
			sorter = latestSorter;
			statusMsg = "Incorrect parameter for sort command";
		}

		latestSorter = sorter;
		if (needToReverse) {
			sorter = Collections.reverseOrder(sorter);
		}

		Collections.sort(lastShownToUI, sorter);
		String appendedSortStatus = listStatusMsg + ", " + statusMsg;

		if (searchFilters == null) {
			return new LogicToUi(lastShownToUI, appendedSortStatus,
					latestSorting, needToReverse);
		} else {
			return new LogicToUi(lastShownToUI, appendedSortStatus,
					searchFilters, latestSorting, needToReverse);
		}
	}

	private void pushCommandToUndoHistoryStack() {
		if (latestCommandFromUI == null) {
			return;
		}

		undoHistory.push(latestCommandFromUI);
	}

	private LogicToUi done(String arguments, boolean newDoneStatus) {
		if (arguments.length() == 0) {
			return new LogicToUi(
					"Sorry this index number you provided is not valid. Please try again with a correct number or refresh the list.");
		}

		int index;

		try {
			index = Integer.parseInt(arguments);
			index--; // Since arraylist index starts from 0

			if ((index < 0) || ((index + 1) > lastShownToUI.size())) {
				throw new NoSuchElementException();
			}

			int serial = lastShownToUI.get(index).getSerial();

			Task toBeUpdated = dataBase.locateATask(serial);
			boolean oldDoneStatus = toBeUpdated.isDone();

			toBeUpdated.done(newDoneStatus);
			dataBase.update(serial, toBeUpdated);

			pushCommandToUndoHistoryStack();

			String taskDetails = taskToString(toBeUpdated);

			if (oldDoneStatus == newDoneStatus) {
				return new LogicToUi(taskDetails
						+ " has been already been marked as done/undone.");
			} else if (newDoneStatus == true) {
				return new LogicToUi(taskDetails + " has been marked as done.");
			} else {
				return new LogicToUi(taskDetails
						+ " has been marked as undone.");
			}
		} catch (NoSuchElementException e) {
			return new LogicToUi(
					"Sorry this index number you provided is not valid. Please try again with a correct number or refresh the list.");
		} catch (IOException e) {
			return new LogicToUi(ERROR_IO);
		} catch (WillNotWriteToCorruptFileException e) {
			return new LogicToUi(ERROR_FILE_CORRUPTED);
		}
	}

	private LogicToUi refresh() {

		return uiCommunicator(latestSortCommand);
	}

	// Can only search by keywords for now
	private LogicToUi search(String arguments) {

		LogicToUi toBeSent = searchPartial(arguments);

		lastShownToUI = toBeSent.getList();
		latestRefreshCommandForUI = new String(latestCommandFromUI);

		return toBeSent;
	}

	// Can only search by keywords for now, allow GUI to show suggestions
	// without affecting state
	private LogicToUi searchPartial(String arguments) {

		if (arguments.length() == 0) {
			return new LogicToUi("No search terms specified.");
		}
		String[] keywords = arguments.split(" ");

		SearchTerms terms = new SearchTerms(keywords);
		ArrayList<Task> results = dataBase.search(terms);

		String statusMsg = "You have searched for ";

		for (String keyword : keywords) {
			statusMsg += " \"" + keyword + "\" ";
		}

		return new LogicToUi(results, statusMsg, terms);
	}

	private LogicToUi undo() {

		executor = new UndoHandler();
		return executor.execute();
	}

	private LogicToUi checkFileStatus() {
		DB_File_Status status = dataBase.getFileAttributes();

		if (status.equals(DB_File_Status.FILE_ALL_OK)) {
			return new LogicToUi("Database file is ready!");
		} else if (status.equals(DB_File_Status.FILE_READ_ONLY)) {
			return new LogicToUi(
					"Database file is read-only or in use by another program. You can only view but not make changes");
		} else if (status.equals(DB_File_Status.FILE_IS_CORRUPT)) {
			return new LogicToUi(
					"The database file is corrupt. DoIt has attempted to read in as much as possible. You will not be able to write to the file until the file is cleared");
		} else if (status.equals(DB_File_Status.FILE_IS_LOCKED)) {
			return new LogicToUi(
					"The database file is locked by another instance of DoIt. Please close all instances and restart the program to use.");
		} else {
			return new LogicToUi("Unknown error with database file");
		}
	}

	private LogicToUi list(String arguments) {

		if (arguments.length() == 0) {

			lastShownToUI = dataBase.readAll();
			latestRefreshCommandForUI = latestCommandFromUI;
			return new LogicToUi(lastShownToUI, "List of all tasks");
		}

		SearchTerms filter;

		String statusMsg = "Listing based on these parameters: ";

		if (arguments.contains("overdue")) {
			DateTime startDate = new DateTime(Long.MIN_VALUE);
			DateTime endDate = new DateTime();

			filter = new SearchTerms(false, true, false, false, false,
					startDate, endDate);

			ArrayList<Task> results = dataBase.search(filter);

			lastShownToUI = results;
			latestRefreshCommandForUI = new String(latestCommandFromUI);

			statusMsg += " \"overdue\" ";
			return new LogicToUi(results, statusMsg, filter);
		}

		String[] parameters = arguments.split(" ");

		boolean complete = false;
		boolean incomplete = false;
		boolean timed = false;
		boolean deadline = false;
		boolean floating = false;
		boolean today = false;
		boolean tomorrow = false;

		for (String eachParam : parameters) {

			if (eachParam.equals("completed") || eachParam.equals("done")) {
				complete = true;
				statusMsg += " \"done\" ";
			}

			if (eachParam.equals("incomplete") || eachParam.equals("undone")) {
				incomplete = true;
				statusMsg += " \"undone\" ";
			}

			if (eachParam.equals("timed")) {
				timed = true;
				statusMsg += " \"timed\" ";
			}

			if (eachParam.equals("deadline")) {
				deadline = true;
				statusMsg += " \"deadline\" ";
			}

			if (eachParam.equals("floating")) {
				floating = true;
				statusMsg += " \"floating\" ";
			}

			if (eachParam.equals("today")) {
				today = true;
				statusMsg += " \"today\" ";
			}

			if (eachParam.equals("tomorrow")) {
				tomorrow = true;
				statusMsg += " \"tomorrow\" ";
			}

		}

		if (today && tomorrow) {
			DateTime startDate = new DateTime().withTimeAtStartOfDay();
			DateTime endDate = startDate.plusDays(1).withTime(23, 59, 59, 999);

			filter = new SearchTerms(complete, incomplete, timed, deadline,
					floating, startDate, endDate);
		} else if (today) {

			DateTime startDate = new DateTime().withTimeAtStartOfDay();
			DateTime endDate = startDate.withTime(23, 59, 59, 999);

			filter = new SearchTerms(complete, incomplete, timed, deadline,
					floating, startDate, endDate);

		} else if (tomorrow) {
			DateTime startDate = new DateTime().plusDays(1)
					.withTimeAtStartOfDay();
			DateTime endDate = startDate.plusDays(1).withTime(23, 59, 59, 999);

			filter = new SearchTerms(complete, incomplete, timed, deadline,
					floating, startDate, endDate);

		} else {
			filter = new SearchTerms(complete, incomplete, timed, deadline,
					floating);
		}

		ArrayList<Task> results = dataBase.search(filter);

		lastShownToUI = results;
		latestRefreshCommandForUI = latestCommandFromUI;
		return new LogicToUi(results, statusMsg, filter);
	}

	private String taskToString(Task toBeConverted) {

		if (toBeConverted.isTimedTask()) {
			return ("Timed task " + "\"" + toBeConverted.getTaskName() + "\""
					+ " from " + dateToString(toBeConverted.getStartDate())
					+ " to " + dateToString(toBeConverted.getEndDate()));
		} else if (toBeConverted.isDeadlineTask()) {
			return ("Deadline task " + "\"" + toBeConverted.getTaskName()
					+ "\"" + " by " + dateToString(toBeConverted.getDeadline()));
		} else {
			return ("Floating task " + "\"" + toBeConverted.getTaskName() + "\"");
		}

	}

	private String dateToString(DateTime inputDate) {
		String LINE_DATE_FORMAT = "EEE dd MMM yyyy h:mma";
		DateTimeFormatter LINE_DATE_FORMATTER = DateTimeFormat
				.forPattern(LINE_DATE_FORMAT);
		return LINE_DATE_FORMATTER.print(inputDate);
	}

	private LogicToUi deleteTask(String arguments) {
		executor = new DeleteHandler(arguments);
		return executor.execute();

	}

	private LogicToUi addTask(String arguments) {
		executor = new AddHandler(arguments);
		return executor.execute();
	}

	private Logic() {
		dataBase = Database.getInstance();
	}

}
