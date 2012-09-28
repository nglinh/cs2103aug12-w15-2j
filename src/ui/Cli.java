package ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import logic.LineParser; //Linh: change this

import shared.Task;
import shared.LogicToUi;

/**  
 * Cli.java 
 * A class for managing the Cli interface
 * @author  Yeo Kheng Meng
 */ 


public class Cli extends UI{

	private static final String MESSAGE_WELCOME_TO_DO_IT = "Welcome to DoIT! ";
	private static final String MESSAGE_PROGRAM_READY = "Type \"help\" for a list of commands.";
	private static final String MESSAGE_NEXT_COMMAND = "Command: ";



	private static final String COMMAND_HELP = "help";
	private static final String COMMAND_EXIT = "exit";
	private static final String COMMAND_QUIT = "quit";
	private static final String COMMAND_GAPS = " ";


	private static final String TABLE_LINE_PARAM_DELIMITER = "|";

	private static final String TABLE_TOP_AND_BOTTOM           = "+--------------------------------------------------------------------------------+";
	private static final String TABLE_HEADER                   = "|Idx| |  Start/Deadline   |        End        |            What to Do?           |";
	private static final String TABLE_ROW_DEMARCATION		   = "+---+-+-------------------+-------------------+----------------------------------+";
	private static final String TABLE_ENTRY_FORMAT = TABLE_LINE_PARAM_DELIMITER + "%1$3d" + TABLE_LINE_PARAM_DELIMITER + "%2$s" + TABLE_LINE_PARAM_DELIMITER +  " %3$s " + TABLE_LINE_PARAM_DELIMITER +  " %4$s " + TABLE_LINE_PARAM_DELIMITER +  " %5$s " + TABLE_LINE_PARAM_DELIMITER;

	private static final int 	TABLE_DESCRIPTION_ALLOWANCE = 32;
	private static final String TABLE_ENTRY_OVERFLOW_FORMAT = TABLE_LINE_PARAM_DELIMITER + "                                             "  +   TABLE_LINE_PARAM_DELIMITER + " %1$s " + TABLE_LINE_PARAM_DELIMITER;
	private static final String TABLE_DESCRIPTION_PAD = "%-" + TABLE_DESCRIPTION_ALLOWANCE + "s";

	private static final String TABLE_ENTRY_UNDONE = "-";
	private static final String TABLE_ENTRY_DONE = "D";
	private static final String TABLE_EMPTY_DATE_FIELD = "        -        ";



	private static final BufferedReader consoleIn =  new BufferedReader(new InputStreamReader(System.in));

	private static ArrayList<Task> lastShownList = null;

	LineParser toLogic;     //Linh: change this
	CliHelpText cliHelp;

	public Cli(){

		cliHelp = new CliHelpText();
	}

	public void runUI(){
		
		toLogic = new LineParser(this);    //Linh: change this
		System.out.print(MESSAGE_WELCOME_TO_DO_IT);

		LogicToUi filePermissions = toLogic.executeCommand("Read File Permissions");

		System.out.println(filePermissions.getString());

		System.out.println(MESSAGE_PROGRAM_READY);

		System.out.print(MESSAGE_NEXT_COMMAND);


		String lineFromInput;



		try {
			while(true)	{
				lineFromInput = consoleIn.readLine();
				String consoleOut = processInput(lineFromInput);

				System.out.println();
				System.out.println(consoleOut);
				System.out.println();

				System.out.print(MESSAGE_NEXT_COMMAND);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}


	}


	private String processInput(String lineFromInput) {


		lineFromInput = lineFromInput.trim();
		String outputLine = "";
		String lineFromInputLowerCase = lineFromInput.toLowerCase();
		String[] commandKeyword = lineFromInputLowerCase.split(COMMAND_GAPS);

		switch (commandKeyword[0]) {

		case COMMAND_EXIT :
			//Fallthrough
		case COMMAND_QUIT :
			System.exit(0);
			break;
		case COMMAND_HELP :
			outputLine = parseHelp(commandKeyword);
			break;
		default : 
			outputLine = passMessageToLogic(lineFromInput);

		}

		return outputLine;

	}

	private String passMessageToLogic(String lineFromInput) {
		LogicToUi logicReturn = toLogic.executeCommand(lineFromInput);

		String result;
		if(logicReturn.isReturnValueAString()) {
			result =  logicReturn.getString();
		} else {
			lastShownList = logicReturn.getList();
			result = formatTaskListToString(logicReturn);
		}

		return result;
	}



	private String formatTaskListToString(LogicToUi logicReturn) {
		ArrayList<Task> listResults = logicReturn.getList();

		StringBuffer screenTable = new StringBuffer();

		screenTable.append(TABLE_TOP_AND_BOTTOM + "\n");
		screenTable.append(TABLE_HEADER + "\n");
		screenTable.append(TABLE_ROW_DEMARCATION + "\n");


		for(int index = 0; index < listResults.size(); index++)
		{
			Task entry = listResults.get(index);
			int numberShown = index + 1; //To allow number to start from 1 on the screen

			String entryOutput = formatTaskEntry(entry, numberShown);
			screenTable.append(entryOutput + "\n");

			//If last entry, show the table bottom instead
			if(numberShown == listResults.size()) {
				screenTable.append(TABLE_TOP_AND_BOTTOM + "\n");
			} else {
				screenTable.append(TABLE_ROW_DEMARCATION + "\n");
			}
		}


		return screenTable.toString();

	}

	private String formatTaskEntry(Task entry, int index) {

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


		if(entry.getType().equals(Task.TYPE_TIMED)) {
			start = dateTimeToString(entry.getStartTime());
		} else if(entry.getType().equals(Task.TYPE_DEADLINE)) {
			start = dateTimeToString(entry.getDeadline());
		} else {
			start = TABLE_EMPTY_DATE_FIELD;
		}

		if(entry.getType().equals(Task.TYPE_TIMED)) {
			end = dateTimeToString(entry.getEndTime());
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

	private String multiLineEntry(int index, String done, String start,	String end, String description) {

		StringBuffer returnEntry = new StringBuffer();
		int linesRequired =  (int) Math.ceil(((float) description.length()) / TABLE_DESCRIPTION_ALLOWANCE);

		String firstLine = String.format(TABLE_ENTRY_FORMAT, index, done, start, end, description.substring(0, TABLE_DESCRIPTION_ALLOWANCE));

		returnEntry.append(firstLine);

		//Start processing from second line
		for(int lineNumber = 2; lineNumber < linesRequired; lineNumber++ ) {
			String substringForThisLine = description.substring((lineNumber - 1) * TABLE_DESCRIPTION_ALLOWANCE, TABLE_DESCRIPTION_ALLOWANCE * lineNumber);
			String nextLine = String.format("\n" + TABLE_ENTRY_OVERFLOW_FORMAT, substringForThisLine );
			returnEntry.append(nextLine);
		}

		//Process the last line specially as it has to be padded with spaces
		String subStringForLastLine = description.substring((linesRequired - 1) * TABLE_DESCRIPTION_ALLOWANCE);
		String lastLineDescription = String.format(TABLE_DESCRIPTION_PAD, subStringForLastLine);
		String lastLine = String.format("\n" + TABLE_ENTRY_OVERFLOW_FORMAT, lastLineDescription);
		returnEntry.append(lastLine);

		return returnEntry.toString();
	}

	private String parseHelp(String[] separated) {
		String text = null;
		final int FIRST_PARAMETER = 1;

		if(separated.length == 1) {
			text = cliHelp.help();
		} else {
			text = cliHelp.detailedCommandHelp(separated[FIRST_PARAMETER]);
		}

		return text;
	}

	@Override
	public int indexToSerial(int index) throws NoSuchElementException{
		if(lastShownList == null){
			throw new NoSuchElementException();
		}

		//To account for array index starting from 0
		index--;
		
		int serial;
		try {
			Task matched = lastShownList.get(index);
			serial = matched.getSerial();
		} catch(IndexOutOfBoundsException e) {
			throw new NoSuchElementException();
		}
		
		return serial;
	}



}



