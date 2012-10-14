package ui;

/**  
 * Cli.java
 * A class holding the help contents for Cli
 * @author  Yeo Kheng Meng
 */

public class CliHelpText {

	private static final String MESSAGE_NO_SUCH_COMMAND_AVAILABLE = "This command \"%1%s\" is not supported by DoIT.";
	
	CliHelpText() {

	}

	public String help() {
		String helpCommands = 
				"Type \"help [command]\" for detailed usage for \"command\"" + "\n" +
						"add" + "\n" + 
						"list" + "\n" + 
						"delete" + "\n" + 
						"edit" + "\n" + 
						"postpone" + "\n" + 
						"done" + "\n" + 
						"undone" + "\n" + 
						"undo" + "\n";

		return helpCommands;
	}

	public String detailedCommandHelp(String commandHelp) {

		commandHelp = commandHelp.trim();
		commandHelp = commandHelp.toLowerCase();
		String helpText = null;

		switch(commandHelp) {
		case "add" :
			helpText = add();
			break;
		case "list" :
			helpText = list();
			break; 
		case "delete" :
			helpText = delete();
			break;
		case "edit" :
			helpText = edit();
			break;
		case "postpone" :
			helpText = postpone();
			break;
		case "done" :
			helpText = done();
			break;
		case "undone" :
			helpText = undone();
			break;
		case "undo" :
			helpText = undo();
			break;
		default :
			helpText =  String.format(MESSAGE_NO_SUCH_COMMAND_AVAILABLE, commandHelp);
		}

		return helpText;
	}

	private String undo() {
		String text = 
				"Undos the last change you made.\n" +
				"Usage: undo\n\n" +
				"Example: undo\n\n" +
				"Advised to call a list operation after every change to database";
		
		return text;
	}

	private String undone() {
		String text = 
				"Marks the task specified by index as undone.\n" +
				"Usage: undone [index]\n\n" +
				"To get \"index\" number, use the list command.\n" +
				"\"index\" will be based on last shown list.\n\n" +
				"Example: undone 5";
				
		return text;
	}

	private String done() {
		String text = 
				"Marks the task specified by index as done.\n" +
				"Usage: done [index]\n\n" +
				"To get \"index\" number, use the list command.\n" +
				"\"index\" will be based on last shown list.\n\n" +
				"Example: done 5";;
				
		return text;
	}

	private String postpone() {
		String text = 
				"";
		return text;
	}

	private String edit() {
		String text = 
				"";
		return text;
	}

	private String delete() {
		String text = 
				"";
		return text;
	}

	private String list() {
		String text = 
				"";
		return text;
	}

	private String add() {
		String text = 
				"";
		return text;
	}

}
