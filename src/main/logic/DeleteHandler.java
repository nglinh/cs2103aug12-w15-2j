package main.logic;

import java.io.IOException;

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
		boolean commandSuccess = true;
		try {
			
			pushCurrentTaskListToUndoStack();
			parser.parse();
			if (parser.isAll) {

				dataBase.deleteAll();
				feedback = new LogicToUi("All tasks have been deleted");
				String undoMessage = "deletion of all tasks";
				pushUndoStatusMessage(undoMessage);
			} else if (parser.isDone) {

				dataBase.deleteDone();
				feedback = new LogicToUi(
						"All completed tasks have been deleted");
				String undoMessage = "deletion of done tasks";
				pushUndoStatusMessage(undoMessage);
			} else if (parser.isOver) {

				dataBase.deleteOver();
				feedback = new LogicToUi(
						"All tasks that has ended before this moment have been deleted");
				String undoMessage = "deletion of tasks before this moment";
				pushUndoStatusMessage(undoMessage);
			} else {

				toBeDeleted = parser.getToBeDeleted();
				dataBase.delete(toBeDeleted.getSerial());
				String taskDetails = taskToString(toBeDeleted);
				feedback = new LogicToUi(taskDetails + " has been deleted");
				String undoMessage = "deletion of task \"" + taskDetails + "\"";
				pushUndoStatusMessage(undoMessage);
			}

		} catch (IOException e) {
			feedback = new LogicToUi(ERROR_IO);
			commandSuccess = false;
		} catch (WillNotWriteToCorruptFileException e) {
			feedback = new LogicToUi(ERROR_FILE_CORRUPTED);
			commandSuccess = false;
		} catch (NumberFormatException e){
			feedback = new LogicToUi(ERROR_INDEX_NUMBER_NOT_VALID);
			commandSuccess = false;
		} finally {
			if(commandSuccess == false ) {
				popUndoClones();
			}
		}

		return feedback;

	}
}
