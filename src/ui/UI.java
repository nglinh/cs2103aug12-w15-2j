package ui;

/**  
 * UI.java 
 * An abstract class for all the UIs.
 * @author  Yeo Kheng Meng
 */ 


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import shared.LogicToUi;
import logic.Logic;

public abstract class UI {
	private final String LINE_DATE_FORMAT = "dd-MMM-yy hh:mma";
	private final String LINE_DATE_LONGER_FORMAT = "EEE dd-MMM-yyyy hh:mma";
	
	private final DateTimeFormatter LINE_DATE_FORMATTER = DateTimeFormat.forPattern(LINE_DATE_FORMAT);
	private final DateTimeFormatter LINE_DATE_LONGER_FORMATTER = DateTimeFormat.forPattern(LINE_DATE_LONGER_FORMAT);
	
	protected static final String COMMAND_CHECK_FILE_PERMISSIONS = "fileStatus";

	
	protected LogicToUi sendCommandToLogic(String command) {
		return Logic.uiCommunicator(command);
	}
	
	
	protected String dateTimeToString(DateTime toBeConverted) {
		return LINE_DATE_FORMATTER.print(toBeConverted);
	}
	
	protected String dateTimeToLongerString(DateTime toBeConverted) {
		return LINE_DATE_LONGER_FORMATTER.print(toBeConverted);
	}
	
	protected String currentTimeInLongerForm() {
		return dateTimeToLongerString((new DateTime()));
	}
	
	protected String checkFilePermissions() {
		LogicToUi filePermissions = sendCommandToLogic(COMMAND_CHECK_FILE_PERMISSIONS);
		return filePermissions.getString();
	}

	//This is the first method that will run the UI after it is constructed. DoITstart will run this.
	public abstract void runUI();


}
