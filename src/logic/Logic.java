package logic;
import java.io.IOException;

import org.joda.time.DateTime;

import ui.UI;
import shared.LogicToUi;
import shared.Task.TaskType;
import storage.Database;
import storage.WillNotWriteToCorruptFileException;
import shared.Task;
import shared.SearchTerms;

public class Logic {
	public static enum CommandType{ADD,DELETE};
	public static UI doItUi;
	public static Database dataBase;

	public LogicToUi uiCommunicator(String command) {
		LogicToUi feedback;
		try{
			command = new String(command);
			CommandType commandType = parseCommand(command); 
			feedback = executeCommand(commandType,command);
		}
		catch(NoSuchCommandException e){
			feedback = new LogicToUi("Sorry but I could not understand you. Can you rephrase the message?");
		}
		return feedback;
	}

	private static CommandType parseCommand(String command) throws NoSuchCommandException{
		String commandSyntax = command.trim().split(" ")[0];
		command = command.replaceFirst(commandSyntax, "").trim();
		CommandType typeOfCommand = determineCommandType(commandSyntax);
		return typeOfCommand;
	}
	private static CommandType determineCommandType(String string) throws NoSuchCommandException{
		switch(string.toLowerCase()){
		case "add":
			return CommandType.ADD;
		case "delete":
			return CommandType.DELETE;
		default:
			throw new NoSuchCommandException();
		}
	}

	private static LogicToUi executeCommand(CommandType commandType,String arguments){
		switch(commandType){
		case ADD:
			return addTask(arguments);
		case DELETE:
			return deleteTask(arguments);
		default:
			return null;
		}
	}
	private static LogicToUi deleteTask(String arguments) {
		// TODO Auto-generated method stub
		return null;
	}

	private static LogicToUi addTask(String arguments) {
		try{
			TaskType taskType = AddParser.getType(arguments);
			switch(taskType){
			case FLOATING:
				try{dataBase.add(new Task(arguments));}
				catch(WillNotWriteToCorruptFileException e){
					return new LogicToUi("File corrupted. Please fix this first :(");
				}
				return new LogicToUi("Event " +arguments +"added");
			case DEADLINE:
				DateTime dt = AddParser.getEndTime(arguments);
				String taskName = AddParser.getTaskName(arguments);
				try{
					dataBase.add(new Task(taskName,dt));
					return new LogicToUi("Event " +taskName +"added");
				}
				catch(IOException e){
					return new LogicToUi("In/Out error. Please restart the program.");
				}
				catch(WillNotWriteToCorruptFileException e){
					return new LogicToUi("File is corrupted. Please check :(.");
				}
			
			
			case TIMED:
				DateTime st = AddParser.getBeginTime(arguments);
				DateTime et = AddParser.getEndTime(arguments);
				String newTaskName = AddParser.getTaskName(arguments);
				try{
					dataBase.add(new Task(newTaskName,st,et));
					return new LogicToUi("Event " +newTaskName +"added");
				}
				catch(IOException e){
					return new LogicToUi("In/Out error.Please restart the program.");
				}
				catch(WillNotWriteToCorruptFileException e){
					return new LogicToUi("File is corrupted. Please check :(.");
				}
			}
		}
		catch(IOException e){
		
			return new LogicToUi("Something is wrong with the file. I cannot write to it. Please check the permission" +
					"for the file");
		}
		return new LogicToUi("I could not determine the type of your event. Can you be more specific?");
		// TODO Auto-generated method stub

	}


	public Logic(){
		dataBase = new Database();

	}

}
