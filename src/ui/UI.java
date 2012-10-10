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
	private final DateTimeFormatter LINE_DATE_FORMATTER = DateTimeFormat.forPattern(LINE_DATE_FORMAT);
	
	protected static final String COMMAND_CHECK_FILE_PERMISSIONS = "Read File Permissions";

	private Logic logic = new Logic();
	
	protected LogicToUi sendCommandToLogic(String command) {
		return logic.uiCommunicator(command);
	}
	
	
	protected String dateTimeToString(DateTime toBeConverted) {
		return LINE_DATE_FORMATTER.print(toBeConverted);
	}
	
	protected String checkFilePermissions() {
		LogicToUi filePermissions = sendCommandToLogic(COMMAND_CHECK_FILE_PERMISSIONS);
		return filePermissions.getString();
	}

	//This is the first method that will run the UI after it is constructed. DoITstart will run this.
	public abstract void runUI();


}
