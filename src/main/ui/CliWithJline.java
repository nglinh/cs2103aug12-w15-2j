package main.ui;

/**  
 * CliWithJline.java
 * A class for managing the Cli interface that has features like Tab Completion and Command History
 * @author  Yeo Kheng Meng
 */

import java.util.ArrayList;
import java.util.List;

import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.StringsCompleter;


public class CliWithJline extends Cli{

	private static final int TABLE_MINIMUM_DESCRIPTION_SIZE = 16;
	private static final String TABLE_HEADER_FILLER = " ";
	private static final String TABLE_HEADER_END = " |";
	private static final String TABLE_HEADER_BEGINNING = "|Idx| |  Start/Deadline   |        End        |      What to Do?";
	private static final String TABLE_END_CHARACTER = "-+";
	private static final String TABLE_BORDER_FILLER = "-";
	private static final String TABLE_ROW_DEMARCATION_BEGIN = "+---+-+-------------------+-------------------+-";
	private static final String TABLE_TOP_AND_BOTTOM_BEGIN = "+-----------------------------------------------";
	private static final String MESSAGE_CLI_CUSTOM = "Includes Tab Completion\nRun DoIt with -clisafe if you experience problems";
	protected static final String MESSAGE_INITIAL_HELP_OFFER_JLINE = "Press Tab or type \"help\" for a list of commands.";


	private static final Completer baseCommandList = new StringsCompleter (commandList);

	private static final Completer helpCommand = new StringsCompleter(new String [] {"help"});
	private static final Completer helpArguments = baseCommandList;

	private static final Completer listCommand = new StringsCompleter(new String [] {"list", "ls", "l"});
	private static final Completer listArguments = new StringsCompleter(new String [] {"done", "undone", "timed", "deadline", "floating" , "today", "tomorrow", "overdue"});

	private static final Completer delCommand = new StringsCompleter(new String [] {"del", "delete", "de"});
	private static final Completer delArguments = new StringsCompleter(new String [] {"all", "over", "done"});

	private static final Completer sortCommand = new StringsCompleter(new String [] {"sort"});
	private static final Completer sortArguments = new StringsCompleter(new String [] {"type", "done", "start", "end", "name", "reverse"});

	List<Completer> listSet = new ArrayList<Completer>();
	ArgumentCompleter listArgCmp;

	List<Completer> helpSet = new ArrayList<Completer>();
	ArgumentCompleter helpArgCmp;

	List<Completer> delSet = new ArrayList<Completer>();
	ArgumentCompleter delArgCmp;

	List<Completer> sortSet = new ArrayList<Completer>();
	ArgumentCompleter sortArgCmp;

	ConsoleReader console;

	@Override
	public void runUI(){

		System.out.println(MESSAGE_WELCOME_TO_DO_IT);
		System.out.println(MESSAGE_CLI_CUSTOM);
		System.out.println();

		try {
			console = new ConsoleReader();

			configureArguments();

			console.println(checkFilePermissions() + "\n");
			console.println(MESSAGE_INITIAL_HELP_OFFER_JLINE);

			console.setPrompt(MESSAGE_NEXT_COMMAND);

			String lineFromInput;
			while(true)	{
				lineFromInput = console.readLine();
				getConsoleSizeAndAdjustOutput();
				String consoleOut = processInput(lineFromInput);

				console.println();
				console.println(consoleOut);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void getConsoleSizeAndAdjustOutput(){
		//DoIT is only guaranteed to display properly for the minimum description width, 
		//below that, the header for the description will be too small.
		if(consoleWidth == console.getTerminal().getWidth()){
			return;
		} else {
			consoleWidth = console.getTerminal().getWidth();
		}

		TABLE_DESCRIPTION_ALLOWANCE = consoleWidth - TABLE_SIZE_OF_PRE_TASK_DESCRIPTION;
		if(TABLE_DESCRIPTION_ALLOWANCE < TABLE_MINIMUM_DESCRIPTION_SIZE) {
			TABLE_DESCRIPTION_ALLOWANCE = TABLE_MINIMUM_DESCRIPTION_SIZE;
		}

		TABLE_TOP_AND_BOTTOM = TABLE_TOP_AND_BOTTOM_BEGIN;
		TABLE_ROW_DEMARCATION = TABLE_ROW_DEMARCATION_BEGIN;

		for(int i = 0; i < TABLE_DESCRIPTION_ALLOWANCE; i++){
			TABLE_TOP_AND_BOTTOM += TABLE_BORDER_FILLER;
			TABLE_ROW_DEMARCATION += TABLE_BORDER_FILLER;
		}

		TABLE_TOP_AND_BOTTOM += TABLE_END_CHARACTER;
		TABLE_ROW_DEMARCATION += TABLE_END_CHARACTER;

		TABLE_HEADER = TABLE_HEADER_BEGINNING;
		for(int i = 0; i < TABLE_DESCRIPTION_ALLOWANCE - 16; i++){
			TABLE_HEADER += TABLE_HEADER_FILLER;
		}

		TABLE_HEADER += TABLE_HEADER_END;


		TABLE_DESCRIPTION_PAD = "%-" + TABLE_DESCRIPTION_ALLOWANCE + "s";
	}

	private void configureArguments() {
		listSet.add(listCommand);
		listSet.add(listArguments);
		listArgCmp = new ArgumentCompleter(listSet);

		helpSet.add(helpCommand);
		helpSet.add(helpArguments);
		helpArgCmp = new ArgumentCompleter(helpSet);

		delSet.add(delCommand);
		delSet.add(delArguments);
		delArgCmp = new ArgumentCompleter(delSet);

		sortSet.add(sortCommand);
		sortSet.add(sortArguments);
		sortArgCmp = new ArgumentCompleter(sortSet);			


		console.addCompleter(baseCommandList);
		console.addCompleter(listArgCmp);
		console.addCompleter(helpArgCmp);
		console.addCompleter(delArgCmp);
		console.addCompleter(sortArgCmp);
	}





}
