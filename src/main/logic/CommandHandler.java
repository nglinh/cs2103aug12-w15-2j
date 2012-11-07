package main.logic;

import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import main.shared.LogicToUi;
import main.shared.Task;
import main.storage.Database;

public abstract class CommandHandler {

	protected static final String ERROR_CANNOT_PARSE_DATE = "One or more field(s)"
			+ " expects time component. However,"
			+ "either time component is missing, or DoIt! could not parse it :(."
			+ "Please check your input";
	protected static final String ERROR_IO = "Something is wrong with the file."
			+ " I cannot write to it. Please check your file permissions.";
	protected static final String ERROR_FILE_CORRUPTED = "File is corrupted. Please"
			+ " rectify the problem or delete the database file and restart DoIT. :(";
	protected static final String ERROR_TASKDES_EMPTY = "Task name cannot be empty."
			+ " Please check again :(.";
	protected static final String ERROR_INDEX_NUMBER_NOT_VALID = "Sorry you did not"
			+ " provide an index number or the number you provided is not valid."
			+ " Please try again with a correct number or refresh the list.";

	private static LinkedList<String> undoMsgHistory = new LinkedList<String>();
	private static LinkedList<List<Task>> undoClones = new LinkedList<List<Task>>();

	protected static Database dataBase = Database.getInstance();
	protected static LastShownToUI lastShownObject = LastShownToUI.getInstance();
	protected static CommandHandler latestListingHandlerForUI = new ListHandler("");
	protected static CommandHandler latestSortHandlerForUI = new SortHandler("");


	protected LogicToUi feedback;


	public CommandHandler(String arguments) {
	}


	public abstract LogicToUi execute();

	protected void pushUndoStatusMessageAndTaskList(String undoMsg,
			List<Task> currentCopy) {
		undoMsgHistory.push(undoMsg);
		undoClones.push(currentCopy);

	}

	protected List<Task> getCurrentTaskList() {
		return dataBase.getAll();
	}

	protected List<Task> peekUndoClones() {
		return undoClones.peek();
	}

	protected void popUndoClones() {
		undoClones.pop();
	}

	protected int undoStepsRemaining() {
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
	protected void setLastShownToUi(List<Task> newList){
		
	}

}