package test.logic;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import main.logic.Logic;
import main.shared.LogicToUi;
import main.shared.Task;
import main.storage.Database;
import main.storage.WillNotWriteToCorruptFileException;

import org.joda.time.DateTime;
import org.junit.Test;

public class LogicDeleteTest {
	
	private static final int ERR_TASK_FOUND = -100000;
	String[] addCommandsToTest = {"add \"project meeting at com\" at 3pm"};
	
	@Test
	public void normalDelete() {
		String addCommand = "add \"project meeting at com\" at 3pm";
		String deleteCommand = "delete 1";
		Task notPresentTask = new Task("project meeting at com", new DateTime().withTime(15, 0, 0, 0));		
		int result = CommandTester(addCommand, deleteCommand, notPresentTask);
		assertEquals(1, result);
	}
	
	@Test
	public void outofBoundaryDelete() {
		String addCommand = "add \"project meeting at com\" at 3pm";
		String deleteCommand = "delete 2";
		Task notPresentTask = new Task("project meeting at com", new DateTime().withTime(15, 0, 0, 0));		
		int result = CommandTester(addCommand, deleteCommand, notPresentTask);
		assertEquals(ERR_TASK_FOUND, result); // task should be found, as the index was invalid
		
		notPresentTask = new Task("something else", new DateTime().withTime(15, 0, 0, 0));		
		result = CommandTester(addCommand, deleteCommand, notPresentTask);
		assertEquals(0, result); // zero because no change should be expected
	}
	
	@Test
	public void outofBoundaryDelete2() {
		String addCommand = "add \"project meeting at com\" at 3pm";
		String deleteCommand = "delete 0";
		Task notPresentTask = new Task("project meeting at com", new DateTime().withTime(15, 0, 0, 0));		
		int result = CommandTester(addCommand, deleteCommand, notPresentTask);
		assertEquals(ERR_TASK_FOUND, result); // task should be found, as the index was invalid
		
		notPresentTask = new Task("something else", new DateTime().withTime(15, 0, 0, 0));		
		result = CommandTester(addCommand, deleteCommand, notPresentTask);
		assertEquals(0, result); // zero because no change should be expected
	}
	
	@Test
	public void multipleTasksDelete() {
		String[] addCommands = new String[]{"add \"project meeting at com\" at 3pm", "add abcdefg at 2pm"};
		String deleteCommand = "delete 1 2";
		Task[] notPresentTasks = new Task[]{
				new Task("project meeting at com", new DateTime().withTime(15, 0, 0, 0)),
				new Task("abcdefg", new DateTime().withTime(14, 0, 0, 0))
				};
		int result = CommandTester(addCommands, deleteCommand, notPresentTasks);		
		assertEquals(result, notPresentTasks.length);
	}
	
	public int CommandTester(String addCommand, String deleteCommand, Task shouldNotContainTask) {
		return CommandTester(new String[]{addCommand}, deleteCommand, new Task[]{shouldNotContainTask});		
	}
	
	public int CommandTester(String[] addCommands, String deleteCommand, Task[] shouldNotContainTasks) {		
		LogicToUi returnValue;
		List<Task> backup = Database.getInstance().getAll();
		
		Logic.getInstance().uiCommunicator("delete all");
		
		// Add the tasks
		for(String cmd : addCommands){
			returnValue = Logic.getInstance().uiCommunicator(cmd);
			System.out.println(returnValue.getString());
		}
		
		// List the tasks
		returnValue = Logic.getInstance().uiCommunicator("list");
		for(Task task : returnValue.getList()){;
			System.out.println(task.showInfo());
		}
		int listSizeBefore = returnValue.getList().size();
		
		// Execute the delete command
		returnValue = Logic.getInstance().uiCommunicator(deleteCommand);
		System.out.println(returnValue.getString());
		
		// Check the return value
		returnValue = Logic.getInstance().uiCommunicator("list");
		for(Task shouldNotContainTask : shouldNotContainTasks){
			for(Task task : returnValue.getList()){
				System.out.println(task.showInfo()) ;
				if(task.showInfo().equals(shouldNotContainTask.showInfo())){
					return ERR_TASK_FOUND;
				}
			}
		}
		int listSizeAfter = returnValue.getList().size();
		
		try {
			Database.getInstance().setAll(backup);
		} catch (IOException | WillNotWriteToCorruptFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return listSizeBefore - listSizeAfter;		
		
	}

}
