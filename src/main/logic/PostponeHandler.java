package main.logic;

import java.io.IOException;

import java.util.List;
import java.util.NoSuchElementException;

import main.logic.exceptions.CannotParseDateException;
import main.logic.exceptions.CannotPostponeFloatingException;
import main.logic.exceptions.EmptyDescriptionException;
import main.shared.LogicToUi;
import main.shared.Task;
import main.storage.WillNotWriteToCorruptFileException;

//@author A0088427U

/**
 * This class handles the postpone command. It creates a
 * new postpone parser object and extract information regarding
 * the postponed task before making changes.
 * 
 * @author mrlinh
 *
 */
public class PostponeHandler extends CommandHandler {
	private static final String ERROR_NONINTEGER_INDEX = "Invalid index number! Index must be an integer.";
	private static final String NOTHING_CHANGED = "The task is still the same!";
	private static final String ERROR_INVALID_INDEX = "Invalid index number. Please check!";
	private static final String ERROR_POSTPONE_FLOATING_TASK = "Cannot postpone a floating task. Please check your index.";
	private PostponeParser parser;
	private Task toBePostponed;
	private int toBePostponedSerial;
	private Task copy;

	public PostponeHandler(String arguments) {
		super(arguments);
		parser = new PostponeParser(arguments);
	}
	/**
	 * Override the execute method in commandhandler class. 
	 * This method extract information out of parser object 
	 * and determine the action accordingly.
	 */
	@Override
	public LogicToUi execute() {
		try {
			parser.parse();
			toBePostponedSerial = parser.getToBePostponedSerial();
			toBePostponed = dataBase.locateATask(toBePostponedSerial);
			copy = new Task(toBePostponed);
			String oldTaskDesc = taskToString(toBePostponed);
			String feedbackString = oldTaskDesc + " has been postponed to ";

			if (!toBePostponed.isFloatingTask()) {
				if (toBePostponed.isDeadlineTask()) {
					feedbackString = postponeDeadline(feedbackString);

				} else {
					feedbackString = postponeTimed(feedbackString);
				}
				if (copy.isEqualTo(toBePostponed)) {
					feedback = new LogicToUi(NOTHING_CHANGED,
							toBePostponedSerial);
				} else {
					updateDatabaseNSendToUndoStack();
				}
				feedback = new LogicToUi(feedbackString, toBePostponedSerial);
			} else {
				feedback = new LogicToUi(ERROR_POSTPONE_FLOATING_TASK,
						toBePostponedSerial);
			}

		} catch (EmptyDescriptionException e) {
			feedback = new LogicToUi(ERROR_TASKDES_EMPTY);
		} catch (CannotParseDateException e) {
			feedback = new LogicToUi(ERROR_CANNOT_PARSE_DATE);
		} catch (CannotPostponeFloatingException e) {
			feedback = new LogicToUi(ERROR_POSTPONE_FLOATING_TASK,
					toBePostponedSerial);
		} catch (NoSuchElementException e) {
			feedback = new LogicToUi(ERROR_INVALID_INDEX);
		} catch (IOException e) {
			feedback = new LogicToUi(ERROR_IO);
		} catch (WillNotWriteToCorruptFileException e) {
			feedback = new LogicToUi(ERROR_FILE_CORRUPTED);
		} catch (NumberFormatException e) {
			feedback = new LogicToUi(ERROR_NONINTEGER_INDEX);
		}

		return feedback;

	}

	/**
	 * Postpone a timed task
	 * 
	 * @param feedbackString
	 *            String to be sent to UI.
	 * @return String to be sent to UI.
	 */
	private String postponeTimed(String feedbackString) {
		copy.changeStartAndEndDate(parser.getNewST(), parser.getNewET());
		feedbackString += dateToString(parser.getNewST()) + " to "
				+ dateToString(parser.getNewET());
		return feedbackString;
	}

	/**
	 * Postpone deadline task
	 * 
	 * @param feedbackString
	 *            String to be sent to UI.
	 * @return string to be sent to UI.
	 */
	private String postponeDeadline(String feedbackString) {
		copy.changeDeadline(parser.getNewDl());
		feedbackString += dateToString(parser.getNewDl());
		return feedbackString;
	}

	/**
	 * Update databse and send to undo stack.
	 */
	@Override
	protected void updateDatabaseNSendToUndoStack()
			throws NoSuchElementException, IOException,
			WillNotWriteToCorruptFileException {
		List<Task> currentTaskList = super.getCurrentTaskList();
		dataBase.update(toBePostponed.getSerial(), copy);
		String taskDetails = taskToString(copy);
		String undoMessage = "postponement of task \"" + taskDetails + "\"";
		super.pushUndoStatMesNTaskList(undoMessage, currentTaskList);
	}
}
