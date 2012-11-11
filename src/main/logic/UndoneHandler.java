package main.logic;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import main.shared.LogicToUi;
import main.shared.Task;
import main.storage.WillNotWriteToCorruptFileException;

public class UndoneHandler extends CommandHandler {

	private String arguments;
	private UndoneParser parser;
	private Task copy;
	private Task toBeUpdated;

	public UndoneHandler(String arguments) {
		super(arguments);
		this.arguments = arguments;
		parser = new UndoneParser(arguments);
	}

	@Override
	public LogicToUi execute() {

		if (arguments.length() == 0) {
			return new LogicToUi(ERROR_INDEX_NUMBER_NOT_VALID);
		}

		try {
			parser.parse();
			int serial = parser.getSerialOfTask();

			toBeUpdated = dataBase.locateATask(serial);
			copy = new Task(toBeUpdated);
			copy.done(false);
			if (copy.isEqualTo(toBeUpdated)) {
				feedback = new LogicToUi(taskToString(toBeUpdated)
						+ " has been already been marked as undone.");
			} else {
				updateDatabaseNSendToUndoStack();
				feedback = new LogicToUi(taskToString(toBeUpdated)
						+ " has been marked as undone.", serial);
			}

		} catch (NumberFormatException e) {
			feedback = new LogicToUi(ERROR_INDEX_NUMBER_NOT_VALID);
		} catch (NoSuchElementException e) {
			return new LogicToUi(ERROR_INDEX_NUMBER_NOT_VALID);
		} catch (IOException e) {
			feedback = new LogicToUi(ERROR_IO);
		} catch (WillNotWriteToCorruptFileException e) {
			feedback = new LogicToUi(ERROR_FILE_CORRUPTED);
		}
		return feedback;
	}

	@Override
	protected void updateDatabaseNSendToUndoStack()
			throws NoSuchElementException, IOException,
			WillNotWriteToCorruptFileException {
		List<Task> copyCurrentTaskList = super.getCurrentTaskList();
		dataBase.update(toBeUpdated.getSerial(), copy);
		String taskDetails = taskToString(copy);
		String undoMessage = "marking of task \"" + taskDetails
				+ "\" as undone";
		super.pushUndoStatMesNTaskList(undoMessage, copyCurrentTaskList);
	}

}
