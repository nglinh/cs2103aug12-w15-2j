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

//@author A0088427U
public class EditTest {
	
	@Test
	public void editName() {
		Task expected = new Task("project meeting at com one", new DateTime().withTime(15, 0, 0, 0));
		Task actual = CommandTester("add \"project meeting at com\" at 3pm", "edit 1 -name project meeting at com one");		
		assertEquals(expected.showInfo(), actual.showInfo());
	}
	
	@Test
	public void editTimedTaskTime() {
		Task expected = new Task("project meeting at com", new DateTime().withTime(16, 0, 0, 0), new DateTime().withTime(17, 0, 0, 0));
		Task actual = CommandTester("add \"project meeting at com\" from 3pm to 4pm", "edit 1 -starttime 4pm -endtime 5pm");		
		assertEquals(expected.showInfo(), actual.showInfo());
	}
	
	@Test
	public void editDeadlineTaskTime() {
		Task expected = new Task("project meeting at com", new DateTime().withTime(16, 0, 0, 0));
		Task actual = CommandTester("add \"project meeting at com\" at 3pm", "edit 1 -deadline 4pm");		
		assertEquals(expected.showInfo(), actual.showInfo());
	}
	
	@Test
	public void editDeadlineTaskTimeOutsideBoundary() {
		Task expected = new Task("project meeting at com", new DateTime().withTime(15, 0, 0, 0));
		Task actual = CommandTester("add \"project meeting at com\" at 3pm", "edit 0 -deadline 4pm");		
		assertEquals(expected.showInfo(), actual.showInfo());
	}
	
	@Test
	public void editDeadlineTaskTimeOutsideBoundary2() {
		Task expected = new Task("project meeting at com", new DateTime().withTime(15, 0, 0, 0));
		Task actual = CommandTester("add \"project meeting at com\" at 3pm", "edit 2 -deadline 4pm");		
		assertEquals(expected.showInfo(), actual.showInfo());
	}
	
	@Test
	public void editDeadlineTaskTimeFloatingPointIndex() {
		Task expected = new Task("project meeting at com", new DateTime().withTime(15, 0, 0, 0));
		Task actual = CommandTester("add \"project meeting at com\" at 3pm", "edit 1.2 -deadline 4pm");		
		assertEquals(expected.showInfo(), actual.showInfo());
	}
	
	@Test
	public void editTimedTaskErrorTime() {
		Task expected = new Task("project meeting at com", new DateTime().withTime(15, 0, 0, 0), new DateTime().withTime(16, 0, 0, 0));
		Task actual = CommandTester("add \"project meeting at com\" from 3pm to 4pm", "edit 1 -starttime 5pm -endtime 4pm");
		// Exceptions should NOT be thrown!
		assertEquals(expected.showInfo(), actual.showInfo());
	}
	
	@Test
	public void editTimedTaskErrorTimeFloatingToTimed() {
		Task expected = new Task("project meeting at com");
		Task actual = CommandTester("add \"project meeting at com\"", "edit 1 -starttime 5pm -endtime 4pm");
		// Exceptions should NOT be thrown!
		assertEquals(expected.showInfo(), actual.showInfo());
	}
	
	@Test
	public void editTimedTaskErrorTimeDeadlineToTimed() {
		Task expected = new Task("project meeting at com", new DateTime().withTime(15, 0, 0, 0));
		Task actual = CommandTester("add \"project meeting at com\" at 3pm", "edit 1 -starttime 5pm -endtime 4pm");
		// Exceptions should NOT be thrown!
		assertEquals(expected.showInfo(), actual.showInfo());
	}
	
	
	
	public Task CommandTester(String addCommand, String editCommand) {		
		LogicToUi returnValue;
		List<Task> backup = Database.getInstance().getAll();
		Logic.getInstance().uiCommunicator("delete all");
		returnValue = Logic.getInstance().uiCommunicator(addCommand);
		System.out.println(returnValue.getString());
		returnValue = Logic.getInstance().uiCommunicator("list");
		returnValue = Logic.getInstance().uiCommunicator(editCommand);
		System.out.println(returnValue.getString());
		returnValue = Logic.getInstance().uiCommunicator("list");
		try {
			Database.getInstance().setAll(backup);
		} catch (IOException | WillNotWriteToCorruptFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnValue.getList().get(0);
	}

}
