package main.ui;

/**  
 * UI.java 
 * An abstract class for all the UIs.
 * @author  Yeo Kheng Meng
 */ 


import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import main.shared.LogicToUi;
import main.logic.Logic;

public abstract class UI {
	private final String LINE_DATE_FORMAT = "dd-MMM-yy hh:mma";
	private final String LINE_DATE_LONGER_FORMAT = "EEE dd-MMM-yyyy hh:mma";
	
	private final DateTimeFormatter LINE_DATE_FORMATTER = DateTimeFormat.forPattern(LINE_DATE_FORMAT);
	private final DateTimeFormatter LINE_DATE_LONGER_FORMATTER = DateTimeFormat.forPattern(LINE_DATE_LONGER_FORMAT);
	
	protected final String COMMAND_CHECK_FILE_PERMISSIONS = "fileStatus";


	private static Logic logic = Logic.getInstance();
	private static Hint hint = Hint.getInstance();
	
	protected static List<String> commandList = hint.getCommands();
	
	//This is the first method that will run the UI after it is constructed. DoITstart will run this.
	public abstract void runUI();
	
	protected LogicToUi sendCommandToLogic(String command) {
		return logic.uiCommunicator(command);
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
	
	
	protected String getHTMLHelp(String command){
		return hint.helpForThisCommandHTML(command);
	}
	
	protected String getNoHTMLHelp(String command){
		return hint.helpForThisCommandNoHTML(command);
	}
	
	protected void exit(){
		System.exit(0);
	}




}
