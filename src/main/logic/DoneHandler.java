package main.logic;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import main.logic.exceptions.EmptyDescriptionException;
import main.shared.LogicToUi;
import main.shared.Task;
import main.storage.WillNotWriteToCorruptFileException;

public class DoneHandler extends CommandHandler {

	private DoneParser parser;
	private Task toBeDone;
	private Task copy;
	private int toBeDoneSerial;

	public DoneHandler(String arguments) {
		super(arguments);
		parser = new DoneParser(arguments);
	}

	@Override
	public LogicToUi execute() {
		try {
			parser.parse();
			toBeDoneSerial = parser.getToBeDoneSerial();
			toBeDone = dataBase.locateATask(toBeDoneSerial);
			copy = new Task(toBeDone);
			copy.done(true);
			if (copy.isEqualTo(toBeDone)) {
				feedback = new LogicToUi(
						"The task has already been marked as done",toBeDoneSerial);
			} else {
				updateDatabaseNSendToUndoStack();
				feedback = new LogicToUi(taskToString(copy)
						+ " has been marked as done.", toBeDoneSerial);
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
