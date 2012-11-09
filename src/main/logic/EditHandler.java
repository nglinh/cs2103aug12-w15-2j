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

//TODO use isEqualTo

public class EditHandler extends CommandHandler {
	private static final String MESSAGE_NOTHING_UPDATED = "Nothing updated!";
	private static final String ERROR_MUST_CHANGE_BOTH_TIME = "In order to change to timed task, you need to specify"
			+ "both start time and end time.";
	private EditParser parser;
	private Task toBeEdited;
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
			Task copy = new Task(toBeEdited);
			changeDeadline(copy);
			changename(copy);
			changeStartTime(copy);
			changeEndTime(copy);
			changeToFloat(copy);
			if (copy.isEqualTo(toBeEdited)) {
				feedback = new LogicToUi(MESSAGE_NOTHING_UPDATED);
			} else {
				List<Task> currentTaskList = super.getCurrentTaskList();
				dataBase.update(toBeEdited.getSerial(), copy);
				String taskDetails = taskToString(copy);
				String feedbackString = taskDetails + " updated.";
				feedback = new LogicToUi(feedbackString, copy.getSerial());
				String undoMessage = "update to \"" + taskDetails + "\"";
				super.pushUndoStatMesNTaskList(undoMessage, currentTaskList);
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

	private void changeToFloat(Task task) {
		if (parser.willChangeToFloating) {
			task.changetoFloating();
		}
	}

	private void changeEndTime(Task task)
			throws DoNotChangeBothSTimeAndETimeException {
		if (parser.willChangeEndTime) {
			if (task.getType() != TaskType.TIMED) {
				if (!parser.willChangeStartTime) {
					throw new DoNotChangeBothSTimeAndETimeException();
				}
				task.changetoTimed(parser.getNewStartTime(),
						parser.getNewEndTime());
			} else {
				task.changeStartAndEndDate(toBeEdited.getStartDate(),
						parser.getNewEndTime());
			}
		}
	}

	private void changeStartTime(Task task)
			throws DoNotChangeBothSTimeAndETimeException {
		if (parser.willChangeStartTime) {
			if (task.getType() != TaskType.TIMED) {
				if (!parser.willChangeEndTime) {
					throw new DoNotChangeBothSTimeAndETimeException();
				}
				task.changetoTimed(parser.getNewStartTime(),
						parser.getNewEndTime());
			} else {
				task.changeStartAndEndDate(parser.getNewStartTime(),
						task.getEndDate());
			}
		}
	}

	private void changename(Task task) {
		if (parser.willChangeName) {
			task.changeName(parser.getNewName());
		}
	}

	private void changeDeadline(Task task) {
		if (parser.willChangeDeadline) {
			if (task.getType() != TaskType.DEADLINE) {
				task.changetoDeadline(parser.getNewDeadline());
			} else {
				task.changeDeadline(parser.getNewDeadline());
			}
		}
	}
}
