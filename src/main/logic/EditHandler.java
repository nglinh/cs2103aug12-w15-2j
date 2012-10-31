package main.logic;

import java.io.IOException;
import java.util.NoSuchElementException;

import main.shared.LogicToUi;
import main.shared.Task;
import main.shared.Task.TaskType;
import main.storage.WillNotWriteToCorruptFileException;

public class EditHandler extends CommandHandler {
	private EditParser parser;
	private Task toBeEdited;

	public EditHandler(String str) {
		parser = new EditParser(str);

	}

	@Override
	public LogicToUi execute() {
		LogicToUi feedback;
		try {
			parser.parse();
			toBeEdited = parser.getToBeEdited();
			changeDeadline();
			changename();
			changeStartTime();
			changeEndTime();
			changeToFloat();
			dataBase.update(toBeEdited.getSerial(), toBeEdited);
			String feedbackString = taskToString(toBeEdited) + " updated.";
			feedback = new LogicToUi(feedbackString, toBeEdited.getSerial());
			return feedback;
		} catch (NoSuchElementException e) {
			feedback = new LogicToUi(
					"Sorry this index number or parameter you provided is not valid. "
							+ "Please try again with a correct number or refresh the list.");
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
