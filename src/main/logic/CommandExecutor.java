package main.logic;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import main.shared.LogicToUi;
import main.shared.Task;
import main.storage.Database;

public abstract class CommandExecutor {
	protected static final String ERROR_CANNOT_PARSE_DATE = "One or more field(s) expects time component. However," +
			"either time component is missing, or DoIt! could not parse it :(." +
			"Please check your input";
	protected final String ERROR_IO = "Something is wrong with the file. I cannot write to it. Please check your file permissions.";
	protected final String ERROR_FILE_CORRUPTED = "File is corrupted. Please rectify the problem or delete the database file and restart DoIT. :(";

	protected Database dataBase = Database.getInstance();
	public LogicToUi execute(){
		return null;
	}
	protected String taskToString(Task toBeConverted) {
		if(toBeConverted.isTimedTask()) {
			return("Timed task " + "\"" + toBeConverted.getTaskName() + "\"" + " from " + dateToString(toBeConverted.getStartDate()) + " to " + dateToString(toBeConverted.getEndDate()));
		} else if(toBeConverted.isDeadlineTask()) {
			return("Deadline task " + "\"" + toBeConverted.getTaskName() + "\"" + " by " + dateToString(toBeConverted.getDeadline()));
		} else {
			return ("Floating task " + "\"" + toBeConverted.getTaskName() + "\"");
		}
	}
	protected String dateToString(DateTime inputDate){
		String LINE_DATE_FORMAT = "EEE dd MMM yyyy h:mma";
		DateTimeFormatter LINE_DATE_FORMATTER = DateTimeFormat.forPattern(LINE_DATE_FORMAT);
		return LINE_DATE_FORMATTER.print(inputDate);
	}
}