package main.logic;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import main.shared.LogicToUi;
import main.shared.Task;
import main.storage.WillNotWriteToCorruptFileException;

public class DoneHandler extends CommandHandler {

	private String arguments;
	private DoneParser parser;

	public DoneHandler(String arguments) {
		super(arguments);
		this.arguments = arguments;
		parser = new DoneParser(arguments);
	}

	@Override
	public LogicToUi execute() {

		if (arguments.length() == 0) {
			return new LogicToUi(ERROR_INDEX_NUMBER_NOT_VALID);
		}

		try {
			parser.parse();

			List<Task> copyCurrentTaskList = super.getCurrentTaskList();
			Task toBeUpdated = dataBase.locateATask(parser.getSerialOfTask());

			if (toBeUpdated.isDone()) {
				return new LogicToUi(taskToString(toBeUpdated)
						+ " has been already been marked as done.");
			}

			toBeUpdated.done(true);
			int serial = toBeUpdated.getSerial();

			dataBase.update(serial, toBeUpdated);

			String taskDetails = taskToString(toBeUpdated);
			String undoMessage = "marking of task \"" + taskDetails
					+ "\" as done";
			super.pushUndoStatusMessageAndTaskList(undoMessage,
					copyCurrentTaskList);
			return new LogicToUi(taskDetails + " has been marked as done.",
					serial);

		} catch (NumberFormatException e) {
			return new LogicToUi(ERROR_INDEX_NUMBER_NOT_VALID);
		} catch (NoSuchElementException e) {
			return new LogicToUi(ERROR_INDEX_NUMBER_NOT_VALID);
		} catch (IOException e) {
			return new LogicToUi(ERROR_IO);
		} catch (WillNotWriteToCorruptFileException e) {
			return new LogicToUi(ERROR_FILE_CORRUPTED);
		}

	}

}
