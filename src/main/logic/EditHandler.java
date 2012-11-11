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

public class EditHandler extends CommandHandler {
	private static final String ERROR_START_BEFORE_END = "Your start time is before end time!";
	private static final String MESSAGE_NOTHING_UPDATED = "The task is still the same!";
	private static final String ERROR_MUST_CHANGE_BOTH_TIME = "In order to change the task to a timed task, you need to specify both the start time and the end time.";
	private EditParser parser;
	private Task toBeEdited;
	private Task copy;
	private int toBeEditedSerial;

	public EditHandler(String arguments) {
		super(arguments);
		parser = new EditParser(arguments);
	}

	@Override
	public LogicToUi execute() {
		try {
			parser.parse();
			toBeEditedSerial = parser.getToBeEditedSerial();
			toBeEdited = dataBase.locateATask(toBeEditedSerial);
			copy = new Task(toBeEdited); // Create a copy to modify.
			if (parser.getWillChangeDeadline()) {
				changeDeadline();
			}
			if (parser.getWillChangeName()) {
				changename();
			}
			if (parser.getWillChangeStartTime()) {
				changeStartTime();
			}
			if (parser.getWillChangeEndTime()) {
				changeEndTime();
			}
			if (parser.getWillChangeToFloat()) {
				changeToFloat();
			}
			if (copy.isEqualTo(toBeEdited)) {
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
		}
		return feedback;
	}

	private void changeToFloat() {
		assert (parser.getWillChangeToFloat());
		copy.changetoFloating();
	}

	private void changeEndTime() throws DoNotChangeBothSTimeAndETimeException,
			STimeBeforeETimeException {
		assert (parser.getWillChangeEndTime());
		if (copy.getType() != TaskType.TIMED) {
			if (!parser.getWillChangeStartTime()) {
				throw new DoNotChangeBothSTimeAndETimeException();
			}
			if (parser.getNewStartTime().isAfter(parser.getNewEndTime())) {
				throw new STimeBeforeETimeException();
			}
			copy.changeToTimed(parser.getNewStartTime(), parser.getNewEndTime());
		} else {
			copy.changeStartAndEndDate(copy.getStartDate(),
					parser.getNewEndTime());
		}
	}

	private void changeStartTime()
			throws DoNotChangeBothSTimeAndETimeException,
			STimeBeforeETimeException {
		assert (parser.getWillChangeStartTime());
		if (copy.getType() != TaskType.TIMED) {
			if (!parser.getWillChangeEndTime()) {
				throw new DoNotChangeBothSTimeAndETimeException();
			}
			if (parser.getNewStartTime().isAfter(parser.getNewEndTime())) {
				throw new STimeBeforeETimeException();
			}
			copy.changeToTimed(parser.getNewStartTime(), parser.getNewEndTime());
		} else {
			copy.changeStartAndEndDate(parser.getNewStartTime(),
					copy.getEndDate());
		}

	}

	private void changename() {
		copy.changeName(parser.getNewName());

	}

	private void changeDeadline() {
		if (copy.getType() != TaskType.DEADLINE) {
			copy.changeToDeadline(parser.getNewDeadline());
		} else {
			copy.changeDeadline(parser.getNewDeadline());
		}

	}

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
