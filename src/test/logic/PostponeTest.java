package test.logic;

//@author A0088427U
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import main.logic.Logic;
import main.shared.LogicToUi;
import main.shared.Task;
import main.storage.Database;
import main.storage.WillNotWriteToCorruptFileException;

import org.joda.time.DateTime;
import org.junit.Test;

public class PostponeTest {

	@Test
	public void testCase1() {
		Task expected = new Task("project meeting at com",
				new DateTime().withTime(19, 0, 0, 0));
		Task actual = CommandTester("add \"project meeting at com\" at 3pm",
				"postpone 1 4 hours");
		assertEquals(expected.showInfo(), actual.showInfo());
	}

	@Test
	public void testCase2() {
		Task expected = new Task("project meeting at com", new DateTime().withTime(19, 0, 0, 0), 
				new DateTime().withTime(20,0 ,0, 0));
		Task actual = CommandTester("add \"project meeting at com\" from 3 pm to 4 pm", "postpone 1 4 hours");		
		assertEquals(expected.showInfo(), actual.showInfo());
	}

	@Test
	public void testCase3() {
		Task expected = new Task("project meeting at com",
				new DateTime().withTime(18, 0, 0, 0));
		Task actual = CommandTester("add \"project meeting at com\" at 3pm",
				"postpone 1 -3 hours");
		assertEquals(expected.showInfo(), actual.showInfo());
	}
	@Test
	public void testCase4() {
		Task expected = new Task("project meeting at com");
		Task actual = CommandTester("add \"project meeting at com\"", "postpone 1 4 hours");		
		assertEquals(expected.showInfo(), actual.showInfo());
	}
	@Test
	public void testCase5() {
		Task expected = new Task("project meeting at com", new DateTime().withTime(00,0,0,0));
		Task actual = CommandTester("add \"project meeting at com\" at 3pm", "postpone 1 0 hours");		
		assertEquals(expected.showInfo(), actual.showInfo());
	}
	
	
	

	public Task CommandTester(String addCommand, String postponeCommand) {
		LogicToUi returnValue;
		List<Task> backup = Database.getInstance().getAll();
		Logic.getInstance().uiCommunicator("delete all");
		returnValue = Logic.getInstance().uiCommunicator(addCommand);
		System.out.println(returnValue.getString());
		returnValue = Logic.getInstance().uiCommunicator("list");
		returnValue = Logic.getInstance().uiCommunicator(postponeCommand);
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
