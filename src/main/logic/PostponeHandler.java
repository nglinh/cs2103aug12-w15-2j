package main.logic;

import java.io.IOException;

import java.util.NoSuchElementException;

import main.shared.LogicToUi;
import main.shared.Task;
import main.storage.WillNotWriteToCorruptFileException;


public class PostponeHandler extends CommandHandler {
	private static final String ERROR_INVALID_INDEX = "Invalid index number. Please check!";
	private static final String ERROR_POSTPONE_FLOATING_TASK = "Cannot postpone a floating task. Please check your index.";
	private PostponeParser parser;
	private Task toBePostponed;
	private LogicToUi feedback;

	public PostponeHandler(String str) {
		parser = new PostponeParser(str);
	}

	public LogicToUi execute() {

		try {
			parser.parse();
			assert toBePostponed.isFloatingTask(); // should throw exception
			// inside parser.
			toBePostponed = parser.getToBePostponed();

			String oldTaskDesc = taskToString(toBePostponed);

			if (toBePostponed.isDeadlineTask()) {
				toBePostponed.changeDeadline(parser.getNewDeadline());
				dataBase.update(toBePostponed.getSerial(), toBePostponed);
			} else if (toBePostponed.isTimedTask()) {
				toBePostponed.changeStartAndEndDate(parser.getNewStartTime(),
						parser.getNewEndTime());
				dataBase.update(toBePostponed.getSerial(), toBePostponed);
			}
			feedback = new LogicToUi(
					oldTaskDesc
					+ " has been postponed to "
					+ (toBePostponed.isDeadlineTask() ? 
							dateToString(parser.getNewDeadline())
							: (dateToString(parser.getNewStartTime())
									+ " to " + dateToString(parser.getNewEndTime())
									)),toBePostponed.getSerial());
		} catch (EmptyDescriptionException e) {
			feedback = new LogicToUi(ERROR_TASKDES_EMPTY);
		} catch (CannotParseDateException e) {
			feedback = new LogicToUi(ERROR_CANNOT_PARSE_DATE);
		} catch (CannotPostponeFloatingException e) {
			feedback = new LogicToUi(ERROR_POSTPONE_FLOATING_TASK);
		} catch (NoSuchElementException e) {
			feedback = new LogicToUi(ERROR_INVALID_INDEX);
		} catch (IOException e) {
			feedback = new LogicToUi(ERROR_IO);
		} catch (WillNotWriteToCorruptFileException e) {
			feedback = new LogicToUi(ERROR_FILE_CORRUPTED);
		}
		return feedback;

	}
}
