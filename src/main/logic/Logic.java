package main.logic;

import main.logic.exceptions.NoSuchCommandException;
import main.shared.LogicToUi;

public class Logic {
	public enum CommandType {
		ADD, DELETE, LIST, SEARCH, SEARCH_PARTIAL, UNDO, FILE_STATUS, REFRESH, DONE, UNDONE, SORT, EDIT, POSTPONE, EXIT
	};

	private CommandHandler executor;

	private static Logic theOne = null;

	private Logic() {
	}

	public static Logic getInstance() {
		if (theOne == null) {
			theOne = new Logic();
			new AddParser("some dummy arguments so I can use DoIt now!");
		}

		return theOne;
	}

	public LogicToUi uiCommunicator(String command) {
		LogicToUi feedback;
		try {

			CommandType commandType = parseCommand(command);

			String arguments = command.replaceFirst(
					command.trim().split(" ")[0], "").trim();
			feedback = executeCommand(commandType, arguments);
		} catch (NoSuchCommandException e) {
			feedback = new LogicToUi(
					"Sorry but I could not understand you. Can you rephrase the message?");
		}
		return feedback;
	}

	private CommandType parseCommand(String command)
			throws NoSuchCommandException {
		String commandSyntax = command.trim().split(" ")[0];

		CommandType typeOfCommand = determineCommandType(commandSyntax);
		return typeOfCommand;
	}

	private CommandType determineCommandType(String string)
			throws NoSuchCommandException {

		switch (string.toLowerCase()) {
		case "add":
			// Fallthrough
		case "a":
			return CommandType.ADD;
		case "delete":
			// Fallthrough
		case "de":
			// Fallthrough
		case "del":
			return CommandType.DELETE;

		case "list":
			// Fallthrough
		case "l":
			// Fallthrough
		case "ls":
			return CommandType.LIST;

		case "search":
			return CommandType.SEARCH;
		case "searchpartial":
			return CommandType.SEARCH_PARTIAL;

		case "undo":
			// Fallthrough
		case "u":
			return CommandType.UNDO;
		case "sort":
			return CommandType.SORT;
		case "filestatus":
			return CommandType.FILE_STATUS;
		case "refresh":
			return CommandType.REFRESH;
		case "done":
			return CommandType.DONE;
		case "undone":
			return CommandType.UNDONE;
		case "edit":
			// fall through
		case "edi":
			// fall through
		case "upd":
			// fall through
		case "update":
			return CommandType.EDIT;
		case "postpone":
			return CommandType.POSTPONE;
		case "exit":
			// Fallthrough
		case "quit":
			return CommandType.EXIT;
		default:
			throw new NoSuchCommandException();
		}
	}

	private LogicToUi executeCommand(CommandType commandType, String arguments) {

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
		case SEARCH_PARTIAL:
			return searchPartial(arguments);
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
		executor = new UndoneHandler(arguments);
		return executor.execute();
	}

	private LogicToUi done(String arguments) {
		executor = new DoneHandler(arguments);
		return executor.execute();
	}

	private LogicToUi exit(String arguments) {
		executor = new ExitHandler(arguments);
		return executor.execute();
	}

	private LogicToUi sort(String arguments) {
		executor = new SortHandler(arguments);
		return executor.execute();
	}

	private LogicToUi postpone(String arguments) {
		executor = new PostponeHandler(arguments);
		return executor.execute();
	}

	private LogicToUi editTask(String argument) {

		executor = new EditHandler(argument);
		return executor.execute();
	}

	private LogicToUi refresh(String arguments) {

		executor = new RefreshHandler(arguments);
		return executor.execute();
	}

	// Can only search by keywords for now
	private LogicToUi search(String arguments) {
		executor = new SearchHandler(arguments);
		return executor.execute();

	}

	// Can only search by keywords for now, allow GUI to show suggestions
	// without affecting state
	private LogicToUi searchPartial(String arguments) {

		executor = new SearchPartialHandler(arguments);
		return executor.execute();
	}

	private LogicToUi undo(String arguments) {

		executor = new UndoHandler(arguments);
		return executor.execute();
	}

	private LogicToUi checkFileStatus(String arguments) {
		executor = new FileStatusHandler(arguments);
		return executor.execute();
	}

	private LogicToUi deleteTask(String arguments) {
		executor = new DeleteHandler(arguments);
		return executor.execute();

	}

	private LogicToUi addTask(String arguments) {
		executor = new AddHandler(arguments);
		return executor.execute();
	}

	private LogicToUi list(String arguments) {
		executor = new ListHandler(arguments);
		return executor.execute();
	}

}
