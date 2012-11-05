package main.logic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import main.shared.LogicToUi;
import main.shared.Task;
import main.storage.Database;

public abstract class CommandHandler {

	protected static final String ERROR_CANNOT_PARSE_DATE = "One or more field(s) expects time component. However,"
			+ "either time component is missing, or DoIt! could not parse it :(."
			+ "Please check your input";
	protected static final String ERROR_IO = "Something is wrong with the file. I cannot write to it. Please check your file permissions.";
	protected static final String ERROR_FILE_CORRUPTED = "File is corrupted. Please rectify the problem or delete the database file and restart DoIT. :(";
	protected static final String ERROR_TASKDES_EMPTY = "Task name cannot be empty. Please check again :(.";
	protected static final String ERROR_INDEX_NUMBER_NOT_VALID = "Sorry you did not provide an index number or the number you provided is not valid. Please try again with a correct number or refresh the list.";
	
	
	private static LinkedList<String> undoMsgHistory = new LinkedList<String>();
	private static LinkedList<List<Task>> undoClones = new LinkedList<List<Task>>();
	
	protected static Database dataBase = Database.getInstance();
	
	protected static List<Task> lastShownToUI = new ArrayList<Task>();
	protected static CommandHandler latestRefreshHandlerForUI = new ListHandler("");
	

	protected static String latestSortArgument = "start";

	protected LogicToUi feedback;
	
	public static final int MAX_UNDO_STEPS = 50;
	
	private Logic logic = Logic.getInstance();

	
	public CommandHandler(String arguments) {
	}
	
	protected LogicToUi sendCommandToLogicAgain(String commandToSend){
		return logic.uiCommunicator(commandToSend);
	}
	
	
	@Deprecated
	public static int getSizeofLastShownToUiList(){
		return lastShownToUI.size();
	}
	
	@Deprecated
	public static Task getTaskFromLastShownToUi(int index){
		return lastShownToUI.get(index);
	}


	public abstract LogicToUi execute();
	
	protected void pushUndoStatusMessageAndTaskList(String undoMsg, List<Task> currentCopy){
		undoMsgHistory.push(undoMsg);
		undoClones.push(currentCopy);

		while(undoMsgHistory.size() > MAX_UNDO_STEPS) {
			undoMsgHistory.removeFirst();
		}
		

		while(undoClones.size() > MAX_UNDO_STEPS) {
			undoClones.removeFirst();
		}
		
	}
	
	
	protected List<Task> getCurrentTaskList(){
		return dataBase.readAll();
	}
	
	
	protected List<Task> peekUndoClones(){
		return undoClones.peek();
	}
	
	protected void popUndoClones(){
		undoClones.pop();
	}
	
	protected int undoStepsRemaining(){
		return undoClones.size();
	}
	
	protected String popAndGetPrevUndoMsg() {
		return undoMsgHistory.pop();
	}



	protected String taskToString(Task toBeConverted) {
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

	protected String dateToString(DateTime inputDate) {
		String LINE_DATE_FORMAT = "EEE dd MMM yyyy h:mma";
		DateTimeFormatter LINE_DATE_FORMATTER = DateTimeFormat
				.forPattern(LINE_DATE_FORMAT);
		return LINE_DATE_FORMATTER.print(inputDate);
	}

}