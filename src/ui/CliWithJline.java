package ui;

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
	
	private static final String MESSAGE_CLI_CUSTOM = "Includes Tab Completion\nRun DoIt with -clisafe if you experience problems";
	protected static final String MESSAGE_INITIAL_HELP_OFFER_JLINE = "Press Tab or type \"help\" for a list of commands.";
	
	
	private static final Completer baseCommandList = new StringsCompleter (new String [] {"help", "add", "list", "sort", "refresh", "delete", "edit", "postpone", "done", "undone", "undo", "search", "exit"});

	private static final Completer helpCommand = new StringsCompleter (new String [] {"help"});
	private static final Completer helpArguments = baseCommandList;
	
	private static final Completer listCommand = new StringsCompleter (new String [] {"list", "ls", "l"});
	private static final Completer listArguments = new StringsCompleter (new String [] {"done", "undone", "timed", "deadline", "floating" , "today", "tomorrow", "overdue", "thisweek", "nextweek"});
	
	private static final Completer delCommand = new StringsCompleter (new String [] {"del", "delete", "de"});
	private static final Completer delArguments = new StringsCompleter (new String [] {"all", "over", "done", "completed"});

	private static final Completer sortCommand = new StringsCompleter (new String [] {"sort"});
	private static final Completer sortArguments = new StringsCompleter (new String [] {"type", "done", "start", "end", "name"});
	
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


			console.println(checkFilePermissions() + "\n");
			console.println(MESSAGE_INITIAL_HELP_OFFER_JLINE);
			
			console.setPrompt(MESSAGE_NEXT_COMMAND);

			String lineFromInput;
			while(true)	{
				lineFromInput = console.readLine();
				String consoleOut = processInput(lineFromInput);

				console.println();
				console.println(consoleOut);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} 



	}




}
