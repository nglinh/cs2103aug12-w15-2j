package main.logic;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import main.shared.LogicToUi;
import main.shared.Task;
import main.shared.Task.TaskType;
import main.storage.WillNotWriteToCorruptFileException;

public class EditHandler extends CommandHandler {
	private static final String ERROR_MUST_CHANGE_BOTH_TIME = "In order to change to timed task, you need to specify"
			+ "both start time and end time.";
	private EditParser parser;
	private Task toBeEdited;
	
	public EditHandler(String arguments) {
		super(arguments);
		parser = new EditParser(arguments);

	}

	@Override
	public LogicToUi execute() {
		
		try {
			parser.parse();
			toBeEdited = parser.getToBeEdited();
			changeDeadline();
			changename();
			changeStartTime();
			changeEndTime();
			changeToFloat();
			List<Task> currentTaskList = super.getCurrentTaskList();
			dataBase.update(toBeEdited.getSerial(), toBeEdited);
			String taskDetails = taskToString(toBeEdited);
			String feedbackString = taskDetails + " updated.";
			
			feedback = new LogicToUi(feedbackString, toBeEdited.getSerial());
			
			String undoMessage = "update to \"" + taskDetails + "\"";
			
			super.pushUndoStatusMessageAndTaskList(undoMessage, currentTaskList);
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
		if (parser.willChangeType) {
			toBeEdited.changetoFloating();
		}
	}

	private void changeEndTime() throws DoNotChangeBothSTimeAndETimeException {
		if (parser.willChangeEndTime) {
			if (toBeEdited.getType() != TaskType.TIMED) {
				if (!parser.willChangeStartTime) {
					throw new DoNotChangeBothSTimeAndETimeException();
				}
				toBeEdited.changetoTimed(parser.getNewStartTime(),
						parser.getNewEndTime());
			} else {
				toBeEdited.changeStartAndEndDate(toBeEdited.getStartDate(),
						parser.getNewEndTime());
			}
		}
	}

	private void changeStartTime() throws DoNotChangeBothSTimeAndETimeException {
		if (parser.willChangeStartTime) {
			if (toBeEdited.getType() != TaskType.TIMED) {
				if (!parser.willChangeEndTime) {
					throw new DoNotChangeBothSTimeAndETimeException();
				}
				toBeEdited.changetoTimed(parser.getNewStartTime(),
						parser.getNewEndTime());
			} else {
				toBeEdited.changeStartAndEndDate(parser.getNewStartTime(),
						toBeEdited.getEndDate());
			}
		}
	}

	private void changename() {
		if (parser.willChangeName) {
			toBeEdited.changeName(parser.getNewName());
		}
	}

	private void changeDeadline() {
		if (parser.willChangeDeadline) {
			if (toBeEdited.getType() != TaskType.DEADLINE) {
				toBeEdited.changetoDeadline(parser.getNewDeadline());
			} else {
				toBeEdited.changeDeadline(parser.getNewDeadline());
			}
		}
	}
}
