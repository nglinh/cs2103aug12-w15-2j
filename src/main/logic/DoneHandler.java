package main.logic;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import main.logic.exceptions.EmptyDescriptionException;
import main.shared.LogicToUi;
import main.shared.Task;
import main.storage.WillNotWriteToCorruptFileException;


//@author A0088427U

/**
 * An object of this class handles the done command.
 * 
 * An object of this class create an done parser object and extract index of the
 * task to be changed out of it. All changes are made by commandhandler object.
 * 
 */
public class DoneHandler extends CommandHandler {

	private static final String STRING_SPACE = " ";
	private static final String STRING_MARKED_DONE = "has been marked as done.";
	private static final String MSG_ALR_DONE = "The task has already been marked as done";
	private DoneParser parser;
	private Task toBeDone;
	private Task copy;
	private int toBeDoneSerial;

	public DoneHandler(String arguments) {
		super(arguments);
		parser = new DoneParser(arguments);
	}

	/**
	 * Overrides execute method in commandhandler. This method extracts
	 * information out of the parser and perform actions accordingly
	 */
	@Override
	public LogicToUi execute() {
		try {
			parser.parse();
			toBeDoneSerial = parser.getToBeDoneSerial();
			toBeDone = dataBase.locateATask(toBeDoneSerial);
			copy = new Task(toBeDone);
			copy.done(true);
			if (copy.isEqualTo(toBeDone)) {
				feedback = new LogicToUi(MSG_ALR_DONE, toBeDoneSerial);
			} else {
				updateDatabaseNSendToUndoStack();
				feedback = new LogicToUi(taskToString(copy) + STRING_SPACE
						+ STRING_MARKED_DONE, toBeDoneSerial);
			}
			return feedback;

		} catch (NumberFormatException e) {
			return new LogicToUi(ERROR_INDEX_NUMBER_NOT_VALID);
		} catch (NoSuchElementException e) {
			return new LogicToUi(ERROR_INDEX_NUMBER_NOT_VALID);
		} catch (IOException e) {
			return new LogicToUi(ERROR_IO);
		} catch (WillNotWriteToCorruptFileException e) {
			return new LogicToUi(ERROR_FILE_CORRUPTED);
		} catch (EmptyDescriptionException e) {
			return new LogicToUi(ERROR_INDEX_NUMBER_NOT_VALID);
		}

	}

	/**
	 * Overrides updateDatabaseNSendToUndoStack method in commandHandler. Update
	 * the database and push undo message as well as current database to undo
	 * stack
	 */
	@Override
	protected void updateDatabaseNSendToUndoStack()
			throws NoSuchElementException, IOException,
			WillNotWriteToCorruptFileException {
		List<Task> copyCurrentTaskList = super.getCurrentTaskList();
		dataBase.update(toBeDoneSerial, copy);
		String taskDetails = taskToString(copy);
		String undoMessage = "marking of task \"" + taskDetails + "\" as done";
		super.pushUndoStatMesNTaskList(undoMessage, copyCurrentTaskList);

	}

}
