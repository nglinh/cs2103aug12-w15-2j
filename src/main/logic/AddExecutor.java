package main.logic;

import java.io.IOException;

import main.shared.LogicToUi;
import main.shared.Task;
import main.shared.Task.TaskType;
import main.storage.WillNotWriteToCorruptFileException;

import org.joda.time.DateTime;

public class AddExecutor extends CommandExecutor{
	private AddParser parser;
	private Task newTask;
	
	public AddExecutor(AddParser parsr){
		parser = parsr;
	}
	@Override
	public LogicToUi execute() {
		try{
			if(!parser.isTaskNameNonempty){
				return new LogicToUi("Task description cannot be empty");
			}
			TaskType taskType = parser.getTaskType();
			switch (taskType){
			case FLOATING:
				newTask = new Task(parser.getTaskName());
				break;
			case DEADLINE:
				DateTime dt = parser.getBeginTime();
				String taskName = parser.getTaskName();
				newTask = new Task(taskName,dt);
				break;
			case TIMED:
				DateTime st = parser.getBeginTime();
				DateTime et = parser.getEndTime();
				String newTaskName = parser.getTaskName();
				newTask = new Task(newTaskName, st, et);
				break;
			default:
				return new LogicToUi(
						"I could not determine the type of your event. Can you be more specific?");
			}
			dataBase.add(newTask);
			//pushCommandToUndoHistoryStack();
			return new LogicToUi(  taskToString(newTask)+ " added",newTask.getSerial());
		}
		catch (WillNotWriteToCorruptFileException e){
			return null;
		}
		catch (IOException e) {
			return new LogicToUi(ERROR_IO);
		}
	} 

}
