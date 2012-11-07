package main.logic;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.joda.time.DateTime;

import main.shared.LogicToUi;
import main.shared.Task;
import main.storage.WillNotWriteToCorruptFileException;

public class DeleteHandler extends CommandHandler {
	private Task toBeDeleted;
	private DeleteParser parser;

	public DeleteHandler(String arguments) {
		super(arguments);
		parser = new DeleteParser(arguments);
	}

	public LogicToUi execute() {

		try {
			List<Task> currentTaskList = super.getCurrentTaskList();
			LinkedList<Integer> arrayOfToBeDeleted = new LinkedList<Integer>();

			parser.parse();
			String undoMessage;

			if (parser.isAll) {
				dataBase.deleteAll();
				feedback = new LogicToUi("All tasks have been deleted");
				undoMessage = "deletion of all tasks";

			} else if (parser.isDone) {

				List<Task> temp = dataBase.getAll();
				for (Task t : temp) {
					if (t.isDone()) {
						arrayOfToBeDeleted.add(t.getSerial());
					}
				}
				dataBase.delete(arrayOfToBeDeleted);
				feedback = new LogicToUi(
						"All completed tasks have been deleted");
				undoMessage = "deletion of done tasks";

			} else if (parser.isOver) {
				List<Task> temp = dataBase.getAll();
				DateTime currentTime = DateTime.now();
				for (Task t : temp) {
					if (t.isDeadlineTask() && t.getDeadline().isAfter(currentTime)
							|| t.isTimedTask() && t.getEndDate().isAfter(currentTime)) {
						arrayOfToBeDeleted.add(t.getSerial());
					}
				}
				dataBase.delete(arrayOfToBeDeleted);
				feedback = new LogicToUi(
						"All tasks that has ended before this moment have been deleted");
				undoMessage = "deletion of tasks before this moment";

			} else {

				if(parser.onlyOneIndexFound){
					toBeDeleted = parser.getToBeDeleted();
					dataBase.delete(toBeDeleted.getSerial());
					String taskDetails = taskToString(toBeDeleted);
					feedback = new LogicToUi(taskDetails + " has been deleted");
					undoMessage = "deletion of task \"" + taskDetails + "\"";
				} else {
					dataBase.delete(parser.listOfToBeDeletedSerials);
					
					undoMessage = "deletion of tasks at indexes " + parser.listOfToBeDeletedIndexes.toString();
					feedback = new LogicToUi("Tasks at indexes " + parser.listOfToBeDeletedIndexes.toString() + " have been deleted");
				}

			}
			super.pushUndoStatusMessageAndTaskList(undoMessage, currentTaskList);
		} catch (IOException e) {
			feedback = new LogicToUi(ERROR_IO);
		} catch (WillNotWriteToCorruptFileException e) {
			feedback = new LogicToUi(ERROR_FILE_CORRUPTED);
		} catch (NumberFormatException | NoSuchElementException e) {
			feedback = new LogicToUi(ERROR_INDEX_NUMBER_NOT_VALID);
		}

		return feedback;

	}
}
