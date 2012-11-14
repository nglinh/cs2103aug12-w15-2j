package main.logic;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import main.logic.exceptions.CannotParseDateException;
import main.logic.exceptions.DoNotChangeBothSTimeAndETimeException;
import main.logic.exceptions.EmptyDescriptionException;
import main.logic.exceptions.STimeBeforeETimeException;
import main.shared.LogicToUi;
import main.shared.Task;
import main.shared.Task.TaskType;
import main.storage.WillNotWriteToCorruptFileException;

//@author A0088427U

/**
 * This class handles the edit command.
 * 
 * It creates a new object of class edit parser and extract information from the
 * parser.
 * 
 */
public class EditHandler extends CommandHandler {
	private static final String ERROR_INDEX_INVALID = "You have entered an invalid index number.";
	private static final String ERROR_START_BEFORE_END = "The start time must be before the end time!";
	private static final String MESSAGE_NOTHING_UPDATED = "The task has not been changed.";
	private static final String ERROR_MUST_CHANGE_BOTH_TIME = "In order to change the task to a timed task, you need to specify both the start time and the end time.";
	private EditParser parser;
	private Task toBeEdited;
	private Task copy;
	private int toBeEditedSerial;

	/**
	 * Constructor of EditHandler class.
	 * 
	 * @param arguments
	 *            : argument string of edit command.
	 */
	public EditHandler(String arguments) {
		super(arguments);
		parser = new EditParser(arguments);
	}

	/**
	 * This method overrides execute method in commandhandler class
	 * 
	 * This method extracts information out of the parser and use it to update
	 * the task accordingly.
	 */
	@Override
	public LogicToUi execute() {
		try {
			parser.parse();
			toBeEditedSerial = parser.getToBeEditedSerial();
			toBeEdited = dataBase.locateATask(toBeEditedSerial);
			copy = new Task(toBeEdited); // Create a copy to modify.
			if (parser.willChangeDeadline()) {
				changeDeadline();
			}
			if (parser.willChangeName()) {
				changename();
			}
			if (parser.willChangeStartTime()) {
				changeStartTime();
			}
			if (parser.willChangeEndTime()) {
				changeEndTime();
			}
			if (parser.willChangeToFloat()) {
				changeToFloat();
			}
			if (copy.isEqualTo(toBeEdited)) // check if the command actually
											// changed anything
			{
				feedback = new LogicToUi(MESSAGE_NOTHING_UPDATED,
						toBeEditedSerial);
			} else {
				updateDatabaseNSendToUndoStack();
				String feedbackString = taskToString(copy) + " updated.";
				feedback = new LogicToUi(feedbackString, toBeEditedSerial);
			}
			return feedback;
		} catch (NoSuchElementException e) {
			feedback = new LogicToUi(ERROR_INDEX_NUMBER_NOT_VALID);
		} catch (IOException e) {
			feedback = new LogicToUi(ERROR_IO);
		} catch (WillNotWriteToCorruptFileException e) {
			feedback = new LogicToUi(ERROR_FILE_CORRUPTED);
		} catch (EmptyDescriptionException e) {
			feedback = new LogicToUi(ERROR_TASKDES_EMPTY);
		} catch (CannotParseDateException e) {
			feedback = new LogicToUi(ERROR_CANNOT_PARSE_DATE);
		} catch (DoNotChangeBothSTimeAndETimeException e) {
			feedback = new LogicToUi(ERROR_MUST_CHANGE_BOTH_TIME);
		} catch (STimeBeforeETimeException e) {
			feedback = new LogicToUi(ERROR_START_BEFORE_END, toBeEditedSerial);
		} catch (NumberFormatException e){
			feedback = new LogicToUi(ERROR_INDEX_INVALID);
		}
		return feedback;
	}

	/**
	 * This method change the task to floating task. assert if the parser does
	 * not detect change to floating syntax
	 */
	private void changeToFloat() {
		assert (parser.willChangeToFloat());
		copy.changetoFloating();
	}

	/**
	 * This method change end time of the task.
	 * 
	 * @throws DoNotChangeBothSTimeAndETimeException
	 *             : if the command does not change both start time and end time
	 *             in the case originally the task is not a timed task.
	 * @throws STimeBeforeETimeException
	 *             : if the start time is before end time
	 */
	private void changeEndTime() throws DoNotChangeBothSTimeAndETimeException,
			STimeBeforeETimeException {
		assert (parser.willChangeEndTime());
		if (copy.getType() != TaskType.TIMED) {
			if (!parser.willChangeStartTime()) {
				throw new DoNotChangeBothSTimeAndETimeException();
			}
			if (parser.getNewStartTime().isAfter(parser.getNewEndTime())) {
				throw new STimeBeforeETimeException();
			}
			copy.changeToTimed(parser.getNewStartTime(), parser.getNewEndTime());
		} else {
			if (copy.getStartDate().isAfter(parser.getNewEndTime())) {
				throw new STimeBeforeETimeException();
			}
			copy.changeStartAndEndDate(copy.getStartDate(),
					parser.getNewEndTime());
		}
	}

	/**
	 * Change Start time of the task.
	 * 
	 * @throws DoNotChangeBothSTimeAndETimeException
	 *             : if the command does not change both start time and end time
	 *             in the case originally the task is not a timed task.
	 * @throws STimeBeforeETimeException
	 *             : if the start time is before end time
	 */
	private void changeStartTime()
			throws DoNotChangeBothSTimeAndETimeException,
			STimeBeforeETimeException {
		assert (parser.willChangeStartTime());
		if (copy.getType() != TaskType.TIMED) {
			if (!parser.willChangeEndTime()) {
				throw new DoNotChangeBothSTimeAndETimeException();
			}
			if (parser.getNewStartTime().isAfter(parser.getNewEndTime())) {
				throw new STimeBeforeETimeException();
			}
			copy.changeToTimed(parser.getNewStartTime(), parser.getNewEndTime());
		} else {
			if (parser.getNewStartTime().isAfter(copy.getEndDate())) {
				throw new STimeBeforeETimeException();
			}
			copy.changeStartAndEndDate(parser.getNewStartTime(),
					copy.getEndDate());
		}

	}

	/**
	 * Change name of the task.
	 */
	private void changename() {
		copy.changeName(parser.getNewName());

	}

	/**
	 * Change deadline of the task.
	 */
	private void changeDeadline() {
		if (copy.getType() != TaskType.DEADLINE) {
			copy.changeToDeadline(parser.getNewDeadline());
		} else {
			copy.changeDeadline(parser.getNewDeadline());
		}

	}

	/**
	 * Update the database with new task and push the message with current
	 * database to undostack.
	 */
	@Override
	protected void updateDatabaseNSendToUndoStack()
			throws NoSuchElementException, IOException,
			WillNotWriteToCorruptFileException {
		List<Task> currentTaskList = super.getCurrentTaskList();
		dataBase.update(toBeEdited.getSerial(), copy);
		String taskDetails = taskToString(copy);
		String undoMessage = "update to \"" + taskDetails + "\"";
		super.pushUndoStatMesNTaskList(undoMessage, currentTaskList);
	}
}
