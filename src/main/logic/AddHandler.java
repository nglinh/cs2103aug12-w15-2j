package main.logic;

import java.io.IOException;
import java.util.List;

import main.shared.LogicToUi;
import main.shared.Task;
import main.shared.Task.TaskType;
import main.storage.WillNotWriteToCorruptFileException;

import org.joda.time.DateTime;

class AddHandler extends CommandHandler {
	private AddParser parser;
	private Task newTask;


	public AddHandler(String arguments) {
		super(arguments);
		parser = new AddParser(arguments);
	}

	@Override
	public LogicToUi execute() {
		
		try {
			List<Task> currentTaskList = super.getCurrentTaskList();
			parser.parse();
			if (!parser.isTaskNameNonempty) {
				return new LogicToUi("Task description cannot be empty");
			}
			TaskType taskType = parser.getTaskType();
			switch (taskType) {
			case FLOATING:
				newTask = new Task(parser.getTaskName());
				break;
			case DEADLINE:
				DateTime dt = parser.getBeginTime();
				String taskName = parser.getTaskName();
				newTask = new Task(taskName, dt);
				break;
			case TIMED:
				DateTime st = parser.getBeginTime();
				DateTime et = parser.getEndTime();
				String newTaskName = parser.getTaskName();
				newTask = new Task(newTaskName, st, et);
				break;
			default:
				feedback = new LogicToUi(
						"I could not determine the type of your event. Can you be more specific?");
			}
			dataBase.add(newTask);

			String taskDetails = taskToString(newTask);
			String feedbackString = taskDetails + " added";
			feedback = new LogicToUi(feedbackString, newTask.getSerial());
			String undoMessage = "addition of task \"" + taskDetails + "\"";
			super.pushUndoStatusMessageAndTaskList(undoMessage, currentTaskList);
		} catch (WillNotWriteToCorruptFileException e) {
			return null;
		} catch (IOException e) {
			feedback = new LogicToUi(ERROR_IO);
		} catch (EmptyDescriptionException e) {
			feedback = new LogicToUi(ERROR_TASKDES_EMPTY);
		} 

		return feedback;
	}
}
