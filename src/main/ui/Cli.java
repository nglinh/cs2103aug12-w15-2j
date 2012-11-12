//@author A0081007U
package main.ui;

/**  
 * Cli.java
 * A class for managing the Cli interface
 * @author  Yeo Kheng Meng
 */

import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import main.LogHandler;
import main.shared.Task;
import main.shared.Task.TaskType;
import main.shared.LogicToUi;

public class Cli extends UI{


	protected static final String MESSAGE_WELCOME_TO_DO_IT = "Welcome to DoIT!";
	private static final String MESSAGE_CLI_CUSTOM = "(Fail-Safe) No Tab-completion";
	protected static final String MESSAGE_INITIAL_HELP_OFFER = "Type \"help\" for a list of commands.";
	protected static final String MESSAGE_NEXT_COMMAND = "Command: ";
	private static final String MESSAGE_NO_SUCH_COMMAND_AVAILABLE = "This command \"%1$s\" is not supported by DoIT.";

	protected static final String COMMAND_HELP = "help";
	protected static final String COMMAND_EXIT = "exit";
	protected static final String COMMAND_QUIT = "quit";
	protected static final String COMMAND_GAPS = " ";

	protected static final String TABLE_LINE_PARAM_DELIMITER = "|";

	protected static	   String TABLE_TOP_AND_BOTTOM           = "+-----------------------------------------------------------------------------+";
	protected static 	   String TABLE_HEADER                   = "|Idx|*|  Start/Deadline   |        End        |      What to Do?              |";
	protected static 	   String TABLE_ROW_DEMARCATION          = "+---+-+-------------------+-------------------+-------------------------------+";
	protected static final String TABLE_ENTRY_FORMAT = TABLE_LINE_PARAM_DELIMITER + "%1$3d" + TABLE_LINE_PARAM_DELIMITER + "%2$s" + TABLE_LINE_PARAM_DELIMITER +  " %3$s " + TABLE_LINE_PARAM_DELIMITER +  " %4$s " + TABLE_LINE_PARAM_DELIMITER +  " %5$s " + TABLE_LINE_PARAM_DELIMITER;

	protected static final String TABLE_ENTRY_OVERFLOW_FORMAT = TABLE_LINE_PARAM_DELIMITER + "   " + TABLE_LINE_PARAM_DELIMITER + " "  + TABLE_LINE_PARAM_DELIMITER + "                   "  +   TABLE_LINE_PARAM_DELIMITER + "                   " +  TABLE_LINE_PARAM_DELIMITER + " %1$s " + TABLE_LINE_PARAM_DELIMITER;

	protected static int consoleWidth = 80;
	protected static final int TABLE_SIZE_OF_PRE_TASK_DESCRIPTION = 51;
	protected static int    TABLE_DESCRIPTION_ALLOWANCE = consoleWidth - TABLE_SIZE_OF_PRE_TASK_DESCRIPTION;
	protected static String TABLE_DESCRIPTION_PAD = "%-" + TABLE_DESCRIPTION_ALLOWANCE + "s";

	protected static final String TABLE_ENTRY_UNDONE = "-";
	protected static final String TABLE_ENTRY_DONE = "*";
	protected static final String TABLE_EMPTY_DATE_FIELD = "        -        ";
	
	protected final String LINE_BREAK = System.getProperty("line.separator");

	Scanner scan = new Scanner(System.in);
	
	protected Logger log = LogHandler.getLogInstance();

	public void runUI(){


		System.out.println(MESSAGE_WELCOME_TO_DO_IT);
		System.out.println(MESSAGE_CLI_CUSTOM);
		System.out.println(super.checkFilePermissions() + LINE_BREAK);
		System.out.println(MESSAGE_INITIAL_HELP_OFFER);
		System.out.print(MESSAGE_NEXT_COMMAND);

		String lineFromInput;

		while(true)     {
			lineFromInput = scan.nextLine();
			log.info("Received this command \"" + lineFromInput + "\"");
			
			String consoleOut = processInput(lineFromInput);
			
			log.info("Output this string \"" + consoleOut + "\"");

			System.out.println();
			
			System.out.println("Current Date/Time is: "+ super.currentTimeInLongerForm() + LINE_BREAK);
			System.out.println(consoleOut);
			System.out.print(MESSAGE_NEXT_COMMAND);
		}

	}


	public String processInput(String lineFromInput) {

		assert(lineFromInput != null);
		lineFromInput = lineFromInput.trim();
		String outputLine = "";
		String lineFromInputLowerCase = lineFromInput.toLowerCase();
		String[] command = lineFromInputLowerCase.split(COMMAND_GAPS, 2);

		switch (command[0]) {

		case COMMAND_EXIT :
			//Fallthrough
		case COMMAND_QUIT :
		{
			log.info("Exit command parsed");
			scan.close();
			super.exit();
		}
			break;
		case COMMAND_HELP :
			log.info("Help command parsed");
			outputLine = parseHelp(command);
			break;
		default :
			log.info("Message suppose to send to logic");
			outputLine = passMessageToLogic(lineFromInput);

		}

		return outputLine;

	}

	protected String passMessageToLogic(String lineFromInput) {
		assert(lineFromInput != null);
		LogicToUi logicReturn = super.sendCommandToLogic(lineFromInput);

		String result;
		if(logicReturn.containsList()) {
			result = formatTaskListToString(logicReturn);
			result +=  LINE_BREAK + logicReturn.getString() + LINE_BREAK;
		} else {
			result = logicReturn.getString();
		}

		return result;
	}



	protected String formatTaskListToString(LogicToUi logicReturn) {
		assert(logicReturn != null);

		List<Task> listResults = logicReturn.getList();

		StringBuffer screenTable = new StringBuffer();


		screenTable.append(TABLE_TOP_AND_BOTTOM + LINE_BREAK);
		screenTable.append(TABLE_HEADER + LINE_BREAK);
		screenTable.append(TABLE_ROW_DEMARCATION + LINE_BREAK);


		for(int index = 0; index < listResults.size(); index++)
		{
			Task entry = listResults.get(index);
			int numberShown = index + 1; //To allow number to start from 1 on the screen

			String entryOutput = formatTaskEntry(entry, numberShown);
			screenTable.append(entryOutput + LINE_BREAK);

			//If last entry, show the table bottom instead
			if(numberShown == listResults.size()) {
				screenTable.append(TABLE_TOP_AND_BOTTOM + LINE_BREAK);
			} else {
				screenTable.append(TABLE_ROW_DEMARCATION + LINE_BREAK);
			}
		}


		return screenTable.toString();

	}

	protected String formatTaskEntry(Task entry, int index) {
		assert(entry != null);
		String returnString;

		String done;
		String start;
		String end;
		String description;

		if(entry.isDone()) {
			done = TABLE_ENTRY_DONE;
		} else {
			done = TABLE_ENTRY_UNDONE;
		}


		if(entry.getType().equals(TaskType.TIMED)) {
			start = super.dateTimeToString(entry.getStartDate());
		} else if(entry.getType().equals(TaskType.DEADLINE)) {
			start = super.dateTimeToString(entry.getDeadline());
		} else {
			start = TABLE_EMPTY_DATE_FIELD;
		}

		if(entry.getType().equals(TaskType.TIMED)) {
			end = super.dateTimeToString(entry.getEndDate());
		} else {
			end = TABLE_EMPTY_DATE_FIELD;
		}

		description = entry.getTaskName();

		if(description.length() <= TABLE_DESCRIPTION_ALLOWANCE) {
			description = String.format(TABLE_DESCRIPTION_PAD, description);
			returnString = String.format(TABLE_ENTRY_FORMAT, index, done, start, end, description);
		} else {
			returnString =  multiLineEntry(index, done, start, end, description);
		}

		return returnString;
	}

	protected String multiLineEntry(int index, String done, String start, String end, String description) {

		StringBuffer returnEntry = new StringBuffer();
		int linesRequired =  (int) Math.ceil(((float) description.length()) / TABLE_DESCRIPTION_ALLOWANCE);

		//We have to break the description string into multiple lines to fit into the description column
		
		//The first line contains the date and hence has to be process differently
		String firstLine = String.format(TABLE_ENTRY_FORMAT, index, done, start, end, description.substring(0, TABLE_DESCRIPTION_ALLOWANCE));

		returnEntry.append(firstLine);

		//Start processing from the second line
		for(int lineNumber = 2; lineNumber < linesRequired; lineNumber++ ) {
			String substringForThisLine = description.substring((lineNumber - 1) * TABLE_DESCRIPTION_ALLOWANCE, TABLE_DESCRIPTION_ALLOWANCE * lineNumber);
			String nextLine = String.format(LINE_BREAK + TABLE_ENTRY_OVERFLOW_FORMAT, substringForThisLine );
			returnEntry.append(nextLine);
		}

		//Process the last line specially as it has to be padded with spaces
		String subStringForLastLine = description.substring((linesRequired - 1) * TABLE_DESCRIPTION_ALLOWANCE);
		String lastLineDescription = String.format(TABLE_DESCRIPTION_PAD, subStringForLastLine);
		String lastLine = String.format(LINE_BREAK + TABLE_ENTRY_OVERFLOW_FORMAT, lastLineDescription);
		returnEntry.append(lastLine);

		return returnEntry.toString();
	}

	protected String parseHelp(String[] command) {
		String text;
		
		if(command.length == 1){
			 text = super.getNoHTMLHelp("help");
		} else {
			text = super.getNoHTMLHelp(command[1]);
		}
		if(!text.isEmpty()){
			return text;
		}

		return String.format(MESSAGE_NO_SUCH_COMMAND_AVAILABLE, command[1]);
	}

}
