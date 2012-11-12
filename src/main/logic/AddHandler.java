package main.logic;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import main.logic.exceptions.EmptyDescriptionException;
import main.shared.LogicToUi;
import main.shared.Task;
import main.shared.Task.TaskType;
import main.storage.WillNotWriteToCorruptFileException;

import org.joda.time.DateTime;

//@author A0088427U

/**
 * This class handles the add command. An object of this class extract
 * information from an Addparser object and create a new task accordingly.
 * 
 * The view of database is shared among all handler objects.
 * 
 */

class AddHandler extends CommandHandler {
	private AddParser parser;
	private Task newTask;
	private String feedbackString;
	private LogicToUi feedback;

	/**
	 * Constructor of the class
	 * 
	 * @param arguments
	 *            : string to extract task details from.
	 */
	public AddHandler(String arguments) {
		super(arguments);
		parser = new AddParser(arguments);
	}

	/**
	 * Override execute method in commandhandler. This method extract
	 * information from the parser and use it to create new task.
	 */
	@Override
	public LogicToUi execute() {

		try {

			parser.parse();
			TaskType taskType = parser.getTaskType();
			switch (taskType) {

			case DEADLINE:
				creatNewDeadlineTask();
				break;
			case TIMED:
				createNewTimedTask();
				break;
			default:
				newTask = new Task(parser.getTaskName());
			}
			updateDatabaseNSendToUndoStack();
			String taskDetails = taskToString(newTask);
			feedbackString = taskDetails + " added";
			feedback = new LogicToUi(feedbackString, newTask.getSerial());

		} catch (EmptyDescriptionException e) {
			feedback = new LogicToUi(ERROR_TASKDES_EMPTY);
		} catch (NoSuchElementException e) {
			// empty catch, from updateDatabaseNSendToUndoStack method.
			// there is no cases such an exception is thrown in addhandler.
		} catch (IOException e) {
			feedback = new LogicToUi(ERROR_IO);
		} catch (WillNotWriteToCorruptFileException e) {
			feedback = new LogicToUi(ERROR_FILE_CORRUPTED);
		}

		return feedback;
	}

	/**
	 * This method create new timed task if the execute method detected parser
	 * parse out a timed task
	 */
	private void createNewTimedTask() {
		DateTime st = parser.getBeginTime();
		DateTime et = parser.getEndTime();
		String newTaskName = parser.getTaskName();
		newTask = new Task(newTaskName, st, et);
	}

	/**
	 * This method create new deadline task if the execute method detected
	 * parser parse out a deadline task
	 */
	private void creatNewDeadlineTask() {
		DateTime dt = parser.getBeginTime();
		String taskName = parser.getTaskName();
		newTask = new Task(taskName, dt);
	}

	/**
	 * This method update the database with new task and push current databse to
	 * undo stack.
	 */
	@Override
	protected void updateDatabaseNSendToUndoStack()
			throws NoSuchElementException, IOException,
			WillNotWriteToCorruptFileException {
		List<Task> currentTaskList = super.getCurrentTaskList();
		dataBase.add(newTask);
		String taskDetails = taskToString(newTask);
		String undoMessage = "addition of task \"" + taskDetails + "\"";
		super.pushUndoStatMesNTaskList(undoMessage, currentTaskList);

	}
}
