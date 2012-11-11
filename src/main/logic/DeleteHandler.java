package main.logic;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.joda.time.DateTime;

import main.shared.LogicToUi;
import main.shared.Task;
import main.storage.WillNotWriteToCorruptFileException;

/**
 * An object of this class handles delete command.
 * 
 * Each object of this class associates with a delete parser object.
 * DeleteHandler object extract information out of its parser object and execute
 * 
 * @author mrlinh
 * 
 */
public class DeleteHandler extends CommandHandler {
	private Task toBeDeleted;
	private DeleteParser parser;

	public DeleteHandler(String arguments) {
		super(arguments);
		parser = new DeleteParser(arguments);
	}

	/**
	 * Override the method execute in commandhandler class. This method extract
	 * information out of the parser and perform actions accordingly.
	 */
	@Override
	public LogicToUi execute() {

		try {
			List<Task> currentTaskList = super.getCurrentTaskList();
			List<Integer> listOfToBeDeletedSerials = new LinkedList<Integer>();

			parser.parse();
			String undoMessage;

			if (parser.isAll()) {
				undoMessage = deleteAll();

			} else if (parser.isDone()) {
				undoMessage = deleteDone(currentTaskList,
						listOfToBeDeletedSerials);

			} else if (parser.isOver()) {
				undoMessage = deleteOver(currentTaskList,
						listOfToBeDeletedSerials);

			} else {

				if (parser.isOnlyOneIndexFound()) {
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

	/**
	 * Perform the deleteAll command.
	 * 
	 * @return undo message to be saved to undo stack.
	 * @throws IOException
	 *             : if unable to perfrom read/write to file.
	 * @throws WillNotWriteToCorruptFileException
	 *             : the file is corrupted.
	 */
	private String deleteAll() throws IOException,
			WillNotWriteToCorruptFileException {
		String undoMessage;
		dataBase.deleteAll();
		feedback = new LogicToUi("All tasks have been deleted");
		undoMessage = "deletion of all tasks";
		return undoMessage;
	}

	/**
	 * Perform the delete on multiple items at a time.
	 * 
	 * @return undo message to be put to undo stack.
	 * @throws IOException
	 *             : if cannot read/write to file.
	 * @throws WillNotWriteToCorruptFileException
	 *             : if the database file is corrupted.
	 */
	private String deleteMultiple() throws IOException,
			WillNotWriteToCorruptFileException {
		String undoMessage;
		dataBase.delete(parser.getListOfToBeDeletedSerials());
		undoMessage = "deletion of tasks at indexes "
				+ parser.getListOfToBeDeletedIndexes().toString();
		feedback = new LogicToUi("Tasks at indexes "
				+ parser.getListOfToBeDeletedIndexes().toString()
				+ " have been deleted");
		return undoMessage;
	}

	/**
	 * Perform delete on one element only
	 * 
	 * @return the details of the task to be put to undo stack
	 * @throws IOException
	 *             : if cannot read/write to data file.
	 * @throws WillNotWriteToCorruptFileException
	 *             : if the data file is corrupt.
	 */
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

	/**
	 * Perform delete on overdue tasks.
	 * 
	 * @param currentTaskList
	 *            : current list of tasks inside database.
	 * @param listOfToBeDeleted
	 *            : list of serials to be deleted.
	 * @return: undo message to be put to undo stack.
	 * @throws IOException
	 *             : if cannot write/read to data file.
	 * @throws WillNotWriteToCorruptFileException
	 *             : if the data file is corrupt.
	 */
	private String deleteOver(List<Task> currentTaskList,
			List<Integer> listOfToBeDeletedSerials) throws IOException,
			WillNotWriteToCorruptFileException {
		String undoMessage;
		DateTime currentTime = DateTime.now();
		for (Task t : currentTaskList) {
			if (t.isDeadlineTask() && t.getDeadline().isBefore(currentTime)
					|| t.isTimedTask() && t.getEndDate().isBefore(currentTime)) {
				listOfToBeDeletedSerials.add(t.getSerial());
			}
		}
		dataBase.delete(listOfToBeDeletedSerials);
		feedback = new LogicToUi(
				"All tasks that has ended before this moment have been deleted");
		undoMessage = "deletion of tasks before this moment";
		return undoMessage;
	}

	/**
	 * Delete all finished tasks.
	 * 
	 * @param currentTaskList
	 *            : current list of tasks.
	 * @param listOfToBeDeletedSerials
	 *            : list of serials of tasks to be deleted.
	 * @return: undo message to be put to undo stack
	 * @throws IOException
	 *             : if cannot read/write to data file.
	 * @throws WillNotWriteToCorruptFileException
	 *             : if the data file is corrupt.
	 */
	private String deleteDone(List<Task> currentTaskList,
			List<Integer> listOfToBeDeletedSerials) throws IOException,
			WillNotWriteToCorruptFileException {
		String undoMessage;
		for (Task t : currentTaskList) {
			if (t.isDone()) {
				listOfToBeDeletedSerials.add(t.getSerial());
			}
		}
		dataBase.delete(listOfToBeDeletedSerials);
		feedback = new LogicToUi("All completed tasks have been deleted");
		undoMessage = "deletion of done tasks";
		return undoMessage;
	}

	@Override
	@Deprecated
	protected void updateDatabaseNSendToUndoStack()
			throws NoSuchElementException, IOException,
			WillNotWriteToCorruptFileException {
		// empty method

	}
}
