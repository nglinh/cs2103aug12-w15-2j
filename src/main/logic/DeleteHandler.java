package main.logic;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import main.shared.LogicToUi;
import main.shared.Task;
import main.storage.WillNotWriteToCorruptFileException;

public class DeleteHandler extends CommandHandler {
	private Task toBeDeleted;
	private DeleteParser parser;
	public DeleteHandler(String arguments){
		super(arguments);
		parser = new DeleteParser(arguments);
	}

	public LogicToUi execute() {

		try {
			
			List<Task> currentTaskList = super.getCurrentTaskList();
			parser.parse();
			String undoMessage;
			
			if (parser.isAll) {
//TODO Wait for KM to change Database.
				dataBase.deleteAll();
				feedback = new LogicToUi("All tasks have been deleted");
				undoMessage = "deletion of all tasks";

			} else if (parser.isDone) {
//TODO Wait for KM to change Database.
				dataBase.deleteDone();
				feedback = new LogicToUi(
						"All completed tasks have been deleted");
				undoMessage = "deletion of done tasks";

			} else if (parser.isOver) {
//TODO Wait for KM to change Database
				dataBase.deleteOver();
				feedback = new LogicToUi(
						"All tasks that has ended before this moment have been deleted");
				undoMessage = "deletion of tasks before this moment";

			} else {

				toBeDeleted = parser.getToBeDeleted();
				dataBase.delete(toBeDeleted.getSerial());
				String taskDetails = taskToString(toBeDeleted);
				feedback = new LogicToUi(taskDetails + " has been deleted");
				undoMessage = "deletion of task \"" + taskDetails + "\"";
				
			}
			super.pushUndoStatusMessageAndTaskList(undoMessage, currentTaskList);
		} catch (IOException e) {
			feedback = new LogicToUi(ERROR_IO);
		} catch (WillNotWriteToCorruptFileException e) {
			feedback = new LogicToUi(ERROR_FILE_CORRUPTED);
		} catch (NumberFormatException | NoSuchElementException e){
			feedback = new LogicToUi(ERROR_INDEX_NUMBER_NOT_VALID);
		}

		return feedback;

	}
}
