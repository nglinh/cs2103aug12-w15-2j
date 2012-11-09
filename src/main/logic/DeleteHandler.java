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
				undoMessage = deleteAll();

			} else if (parser.isDone) {
				undoMessage = deleteDone(currentTaskList, arrayOfToBeDeleted);

			} else if (parser.isOver) {
				undoMessage = deleteOver(currentTaskList, arrayOfToBeDeleted);

			} else {

				if(parser.onlyOneIndexFound){
					undoMessage = deleteOne();
				} else {
					undoMessage = deleteMultiple();
				}

			}
			super.pushUndoStatMesNTaskList(undoMessage, currentTaskList);
		} catch (IOException e) {
			feedback = new LogicToUi(ERROR_IO);
		} catch (WillNotWriteToCorruptFileException e) {
			feedback = new LogicToUi(ERROR_FILE_CORRUPTED);
		} catch (NumberFormatException | NoSuchElementException e) {
			feedback = new LogicToUi(ERROR_INDEX_NUMBER_NOT_VALID);
		}

		return feedback;

	}

	private String deleteAll() throws IOException,
			WillNotWriteToCorruptFileException {
		String undoMessage;
		dataBase.deleteAll();
		feedback = new LogicToUi("All tasks have been deleted");
		undoMessage = "deletion of all tasks";
		return undoMessage;
	}

	private String deleteMultiple() throws IOException,
			WillNotWriteToCorruptFileException {
		String undoMessage;
		dataBase.delete(parser.listOfToBeDeletedSerials);	
		undoMessage = "deletion of tasks at indexes " + parser.listOfToBeDeletedIndexes.toString();
		feedback = new LogicToUi("Tasks at indexes " + parser.listOfToBeDeletedIndexes.toString() + " have been deleted");
		return undoMessage;
	}

	private String deleteOne() throws IOException,
			WillNotWriteToCorruptFileException {
		String undoMessage;
		toBeDeleted = dataBase.locateATask(parser.getSerialOfTask());
		dataBase.delete(parser.getSerialOfTask());
		String taskDetails = taskToString(toBeDeleted);
		feedback = new LogicToUi(taskDetails + " has been deleted");
		undoMessage = "deletion of task \"" + taskDetails + "\"";
		return undoMessage;
	}

	private String deleteOver(List<Task> currentTaskList,
			LinkedList<Integer> arrayOfToBeDeleted) throws IOException,
			WillNotWriteToCorruptFileException {
		String undoMessage;
		DateTime currentTime = DateTime.now();
		for (Task t : currentTaskList) {
			if (t.isDeadlineTask() && t.getDeadline().isBefore(currentTime)
					|| t.isTimedTask() && t.getEndDate().isBefore(currentTime)) {
				arrayOfToBeDeleted.add(t.getSerial());
			}
		}
		dataBase.delete(arrayOfToBeDeleted);
		feedback = new LogicToUi(
				"All tasks that has ended before this moment have been deleted");
		undoMessage = "deletion of tasks before this moment";
		return undoMessage;
	}

	private String deleteDone(List<Task> currentTaskList,
			LinkedList<Integer> arrayOfToBeDeleted) throws IOException,
			WillNotWriteToCorruptFileException {
		String undoMessage;
		for (Task t : currentTaskList) {
			if (t.isDone()) {
				arrayOfToBeDeleted.add(t.getSerial());
			}
		}
		dataBase.delete(arrayOfToBeDeleted);
		feedback = new LogicToUi(
				"All completed tasks have been deleted");
		undoMessage = "deletion of done tasks";
		return undoMessage;
	}

	@Override
	@Deprecated
	protected void updateDatabaseNSendToUndoStack()
			throws NoSuchElementException, IOException,
			WillNotWriteToCorruptFileException {
		//empty method
		
	}
}
