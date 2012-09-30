package logic;
import java.io.IOException;

import ui.UI;
import shared.LogicToUi;
import shared.Task.TaskType;
import storage.Database;
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
			Task.TaskType taskType = determineTaskType(arguments);
			switch(taskType){
			case DEADLINE:
				
				break;
			case FLOATING:
				dataBase.add(new Task(arguments));
				return new LogicToUi("Event " +arguments +"added");
			case TIMED:
				break;
			}
		}
		catch(IOException e){
			return new LogicToUi("Something is wrong with the file. I cannot write to it. Please check the permission" +
					"for the file");
		}
		return new LogicToUi("I could not determine the type of your event. Can you be more specific?");
		// TODO Auto-generated method stub

	}

	private static TaskType determineTaskType(String arguments) {
		// TODO Auto-generated method stub
		return null;
	}

	public Logic(UI userInterface){
		doItUi = userInterface;
		dataBase = new Database();

	}

}
