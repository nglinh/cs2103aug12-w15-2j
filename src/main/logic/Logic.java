package main.logic;

import java.util.logging.Logger;

import main.LogHandler;
import main.logic.exceptions.NoSuchCommandException;
import main.shared.LogicToUi;

/**
 * This class defines the Logic component of DoIt! task manager. It takes the
 * command string from UI, process the command and send changes to database to
 * be stored.
 * 
 * @author mrlinh
 * 
 */
public class Logic {
	private static final String MSG_NO_COMMAND = "Sorry but I could not understand you. Can you rephrase the message?";

	public enum CommandType {
		ADD, DELETE, LIST, SEARCH, UNDO, FILE_STATUS, REFRESH, DONE, UNDONE, SORT, EDIT, POSTPONE, EXIT
	};

	private CommandHandler executor;

	private static Logic theOne = null;
	private Logger log;

	private Logic() {
		log = LogHandler.getLogInstance();
		;
	}

	/**
	 * Logic's architecture applies Singleton pattern. User cannot launch
	 * multiple copies of Logic to prevent conflicts.
	 * 
	 * @return: Singleton of Logic.
	 */
	public static Logic getInstance() {
		if (theOne == null) {
			theOne = new Logic();
			new AddParser("some dummy arguments so I can use DoIt now!");
		}

		return theOne;
	}

	/**
	 * Logic's architecture applies the Facades pattern, in which components of
	 * higher hierachy (in this case, UI) cannot directly call methods inside
	 * logic.
	 * 
	 * @param command
	 *            : string of command received from UI.
	 * @return: LogicToUi object, a wrapper of fields and results to be
	 *          displayed to users.
	 */
	public LogicToUi uiCommunicator(String command) {
		LogicToUi feedback;
		try {
			log.info("Logic received command.");
			String commandSyntax = getFirstWord(command);
			String arguments = removeFirstWord(command);
			CommandType commandType = determineCommandType(commandSyntax);
			feedback = executeCommand(commandType, arguments);
			log.info("Command executed, return to UI.");
		} catch (NoSuchCommandException e) {
			feedback = new LogicToUi(MSG_NO_COMMAND);
			log.warning("Cannot understand command");
		}
		return feedback;
	}

	private CommandType determineCommandType(String string)
			throws NoSuchCommandException {
		log.info("determineCommandType received syntax word.");
		switch (string.toLowerCase()) {
		case "add":
			// Fallthrough
		case "a":
			log.info("add command detected.");
			return CommandType.ADD;
		case "delete":
			// Fallthrough
		case "de":
			// Fallthrough
		case "del":
			log.info("delete command detected.");
			return CommandType.DELETE;

		case "list":
			// Fallthrough
		case "l":
			// Fallthrough
		case "ls":
			log.info("list command detected.");
			return CommandType.LIST;

		case "search":
			log.info("search command detected.");
			return CommandType.SEARCH;

		case "undo":
			// Fallthrough
		case "u":
			log.info("undo command detected.");
			return CommandType.UNDO;
		case "sort":
			log.info("sort command detected.");
			return CommandType.SORT;
		case "filestatus":
			log.info("check file status command detected.");
			return CommandType.FILE_STATUS;
		case "refresh":
			log.info("refresh command detected.");
			return CommandType.REFRESH;
		case "done":
			log.info("done command detected.");
			return CommandType.DONE;
		case "undone":
			log.info("undone command detected.");
			return CommandType.UNDONE;
		case "edit":
			// fall through
		case "edi":
			// fall through
		case "upd":
			// fall through
		case "update":
			log.info("edit command detected.");
			return CommandType.EDIT;
		case "postpone":
			log.info("postpone command detected.");
			return CommandType.POSTPONE;
		case "exit":
			// Fallthrough
		case "quit":
			log.info("exit command detected.");
			return CommandType.EXIT;
		default:
			log.warning("could not identify command");
			throw new NoSuchCommandException();
		}
	}

	private LogicToUi executeCommand(CommandType commandType, String arguments) {
		assert (commandType != null);
		log.info("executeCommand received information");
		switch (commandType) {
		case ADD:
			return addTask(arguments);
		case DELETE:
			return deleteTask(arguments);
		case LIST:
			return list(arguments);
		case FILE_STATUS:
			return checkFileStatus(arguments);
		case UNDO:
			return undo(arguments);
		case SEARCH:
			return search(arguments);
		case REFRESH:
			return refresh(arguments);
		case DONE:
			return done(arguments);
		case UNDONE:
			return undone(arguments);
		case SORT:
			return sort(arguments);
		case EDIT:
			return editTask(arguments);
		case POSTPONE:
			return postpone(arguments);
		case EXIT:
			return exit(arguments);
		default:
			return null;
		}
	}

	private LogicToUi undone(String arguments) {
		log.info("undone method entered.");
		executor = new UndoneHandler(arguments);
		return executor.execute();
	}

	private LogicToUi done(String arguments) {
		log.info("done method entered.");
		executor = new DoneHandler(arguments);
		return executor.execute();
	}

	private LogicToUi exit(String arguments) {
		log.info("exit method entered.");
		executor = new ExitHandler(arguments);
		return executor.execute();
	}

	private LogicToUi sort(String arguments) {
		log.info("sort method entered.");
		executor = new SortHandler(arguments);
		return executor.execute();
	}

	private LogicToUi postpone(String arguments) {
		log.info("postpone method entered.");
		executor = new PostponeHandler(arguments);
		return executor.execute();
	}

	private LogicToUi editTask(String argument) {
		log.info("edit method entered.");
		executor = new EditHandler(argument);
		return executor.execute();
	}

	private LogicToUi refresh(String arguments) {
		log.info("refresh method entered.");
		executor = new RefreshHandler(arguments);
		return executor.execute();
	}

	// Can only search by keywords for now
	private LogicToUi search(String arguments) {
		log.info("search method entered.");
		executor = new SearchHandler(arguments);
		return executor.execute();

	}

	private LogicToUi undo(String arguments) {
		log.info("undo method entered.");
		executor = new UndoHandler(arguments);
		return executor.execute();
	}

	private LogicToUi checkFileStatus(String arguments) {
		log.info("filestatus method entered.");
		executor = new FileStatusHandler(arguments);
		return executor.execute();
	}

	private LogicToUi deleteTask(String arguments) {
		log.info("delete method entered.");
		executor = new DeleteHandler(arguments);
		return executor.execute();

	}

	private LogicToUi addTask(String arguments) {
		log.info("add method entered.");
		executor = new AddHandler(arguments);
		return executor.execute();
	}

	private LogicToUi list(String arguments) {
		log.info("list method entered.");
		executor = new ListHandler(arguments);
		return executor.execute();
	}

	private String removeFirstWord(String string) {
		return string.replaceFirst(getFirstWord(string), "").trim();
	}

	private String getFirstWord(String string) {
		return string.split(" ")[0];
	}
}
