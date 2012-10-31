package main.logic;

import java.io.IOException;

import main.shared.LogicToUi;
import main.shared.Task;
import main.shared.Task.TaskType;
import main.storage.WillNotWriteToCorruptFileException;

import org.joda.time.DateTime;

public class AddHandler extends CommandHandler {
	private static final String ERROR_TASKDES_EMPTY = "Task name cannot be empty. Please check again :(.";
	private AddParser parser;
	private Task newTask;

	public AddHandler(String str) {
		parser = new AddParser(str);

	}

	@Override
	public LogicToUi execute() {
		LogicToUi feedback;
		try {
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
			String feedbackString = taskToString(newTask) + " added";
			feedback = new LogicToUi(feedbackString, newTask.getSerial());
			undoHistory.push(feedbackString);
		} catch (WillNotWriteToCorruptFileException e) {
			return null;
		} catch (IOException e) {
			feedback = new LogicToUi(ERROR_IO);
		} catch (EmptyDescriptionException e) {
			feedback = new LogicToUi(ERROR_TASKDES_EMPTY);
			e.printStackTrace();
		}
		return feedback;
	}
}
