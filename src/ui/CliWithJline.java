package ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jline.console.ConsoleReader;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;


public class CliWithJline extends Cli{
	
	private static final String MESSAGE_CLI_CUSTOM = "Includes command history and Tab Completion\nRun DoIt with -clisafe if you experience problems";
	protected static final String MESSAGE_INITIAL_HELP_OFFER_JLINE = "Press Tab or type \"help\" for a list of commands.";
	
	
	private static final Completer baseCommandList = new StringsCompleter (new String [] {"help", "add", "list", "delete", "edit", "postpone", "done", "undone", "undo", "exit"});

	private static final Completer listCommand = new StringsCompleter (new String [] {"list"});
	private static final Completer listArguments = new StringsCompleter (new String [] {"done", "undone", "timed", "deadline", "floating" , "today", "tomorrow"});

	List<Completer> listSet = new ArrayList<Completer>();
	ArgumentCompleter listArgCmp;
	
	
	@Override
	public void runUI(){
		
		System.out.println(MESSAGE_WELCOME_TO_DO_IT);
		System.out.println(MESSAGE_CLI_CUSTOM);
		System.out.println();
		
		try {
			ConsoleReader console = new ConsoleReader();

			listSet.add(listCommand);
			listSet.add(listArguments);
			listArgCmp = new ArgumentCompleter(listSet);

			console.addCompleter(baseCommandList);
			console.addCompleter(listArgCmp);


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

		} catch (IOException e) {
			e.printStackTrace();
		} 



	}




}
