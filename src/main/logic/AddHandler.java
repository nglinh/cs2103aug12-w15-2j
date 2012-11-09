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

class AddHandler extends CommandHandler {
	private AddParser parser;
	private Task newTask;
	private String feedbackString;
	private LogicToUi feedback;

	public AddHandler(String arguments) {
		super(arguments);
		parser = new AddParser(arguments);
	}

	@Override
	public LogicToUi execute() {

		try {

			parser.parse();
			TaskType taskType = parser.getTaskType();
			switch (taskType) {
			case FLOATING:
				newTask = new Task(parser.getTaskName());
				break;
			case DEADLINE:
				creatNewDeadlineTask();
				break;
			case TIMED:
				createNewTimedTask();
				break;
			default:
				feedback = new LogicToUi(
						"I could not determine the type of your event. Can you be more specific?");
			}
			updateDatabaseNSendToUndoStack();
			String taskDetails = taskToString(newTask);
			feedbackString = taskDetails + " added";
			feedback = new LogicToUi(feedbackString, newTask.getSerial());

		} catch (EmptyDescriptionException e) {
			feedback = new LogicToUi(ERROR_TASKDES_EMPTY);
		} catch (NoSuchElementException e) {
			//empty catch
		} catch (IOException e) {
			feedback = new LogicToUi(ERROR_IO);
		} catch (WillNotWriteToCorruptFileException e) {
			feedback = new LogicToUi(ERROR_FILE_CORRUPTED);
		}

		return feedback;
	}

	private void createNewTimedTask() {
		DateTime st = parser.getBeginTime();
		DateTime et = parser.getEndTime();
		String newTaskName = parser.getTaskName();
		newTask = new Task(newTaskName, st, et);
	}

	private void creatNewDeadlineTask() {
		DateTime dt = parser.getBeginTime();
		String taskName = parser.getTaskName();
		newTask = new Task(taskName, dt);
	}

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
