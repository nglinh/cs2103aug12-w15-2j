package main.logic;

import java.io.IOException;
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

	public EditHandler(String str) {
		parser = new EditParser(str);

	}

	@Override
	public LogicToUi execute() {
		LogicToUi feedback;
		try {
			parser.parse();
			toBeEdited = parser.getToBeEdited();
			feedback = checkParseResult();
			if (feedback != null) {
				return feedback;
			}
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CannotParseDateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return feedback;
	}

	private void changeToFloat() {
		if (parser.willChangeType) {
			toBeEdited.changetoFloating();
		}
	}

	private void changeEndTime() {
		if (parser.willChangeEndTime) {
			if (toBeEdited.getType() != TaskType.TIMED) {
				toBeEdited.changetoTimed(parser.getNewStartTime(),
						parser.getNewEndTime());
			} else {
				toBeEdited.changeStartAndEndDate(toBeEdited.getStartDate(),
						parser.getNewEndTime());
			}
		}
	}

	private void changeStartTime() {
		if (parser.willChangeStartTime) {
			if (toBeEdited.getType() != TaskType.TIMED) {
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

	private LogicToUi checkParseResult() {
		if (!parser.isIndexValid) {
			return new LogicToUi(
					"Please check your index. It's not in the list.");
		}
		if (!parser.canParseDeadline) {
			return new LogicToUi(
					"Please check the new deadline. I cannot parse it :(.");
		}
		if (!parser.canParseName) {
			return new LogicToUi(
					"Please check the new name. It cannot be empty.");
		}
		if (!parser.canParseEndTime) {
			return new LogicToUi(
					"Please check the new end time. I cannot parse it :(.");
		}
		if (!parser.canParseStartTime) {
			return new LogicToUi(
					"Please check the new start time. I cannot parse it :(.");
		}
		if (toBeEdited.getType() != TaskType.TIMED) {
			if ((!parser.willChangeStartTime && parser.willChangeEndTime)) {
				return new LogicToUi(ERROR_MUST_CHANGE_BOTH_TIME);
			}
		}
		return null;
	}
}
