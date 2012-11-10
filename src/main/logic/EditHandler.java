package main.logic;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import main.logic.exceptions.CannotParseDateException;
import main.logic.exceptions.DoNotChangeBothSTimeAndETimeException;
import main.logic.exceptions.EmptyDescriptionException;
import main.shared.LogicToUi;
import main.shared.Task;
import main.shared.Task.TaskType;
import main.storage.WillNotWriteToCorruptFileException;

public class EditHandler extends CommandHandler {
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
			changeDeadline();
			changename();
			changeStartTime();
			changeEndTime();
			changeToFloat();
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
		}
		return feedback;
	}

	private void changeToFloat() {
		if (parser.willChangeToFloating) {
			copy.changetoFloating();
		}
	}

	private void changeEndTime() throws DoNotChangeBothSTimeAndETimeException {
		if (parser.willChangeEndTime) {
			if (copy.getType() != TaskType.TIMED) {
				if (!parser.willChangeStartTime) {
					throw new DoNotChangeBothSTimeAndETimeException();
				}
				copy.changetoTimed(parser.getNewStartTime(),
						parser.getNewEndTime());
			} else {
				copy.changeStartAndEndDate(copy.getStartDate(),
						parser.getNewEndTime());
			}
		}
	}

	private void changeStartTime() throws DoNotChangeBothSTimeAndETimeException {
		if (parser.willChangeStartTime) {
			if (copy.getType() != TaskType.TIMED) {
				if (!parser.willChangeEndTime) {
					throw new DoNotChangeBothSTimeAndETimeException();
				}
				copy.changetoTimed(parser.getNewStartTime(),
						parser.getNewEndTime());
			} else {
				copy.changeStartAndEndDate(parser.getNewStartTime(),
						copy.getEndDate());
			}
		}
	}

	private void changename() {
		if (parser.willChangeName) {
			copy.changeName(parser.getNewName());
		}
	}

	private void changeDeadline() {
		if (parser.willChangeDeadline) {
			if (copy.getType() != TaskType.DEADLINE) {
				copy.changetoDeadline(parser.getNewDeadline());
			} else {
				copy.changeDeadline(parser.getNewDeadline());
			}
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
