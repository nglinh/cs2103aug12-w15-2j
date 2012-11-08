package test.logic;

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

public class LogicAddTest{

	@Test
	public void floatingTask() {
		Task a = CommandTester("add hello world");
		Task b = new Task("hello world");
		assertEquals(a.showInfo(), b.showInfo());
	}
	
	@Test
	public void deadlineTask() {
		Task a = CommandTester("add cs2101 blogpost 29 oct at 23:59");
		Task b = new Task("cs2101 blogpost", new DateTime(2012, 10, 29, 23, 59, 0));
		assertEquals(a.showInfo(), b.showInfo());
	}
	
	@Test
	public void deadlineTask2() {
		Task a = CommandTester("add cs2101 blogpost by 29 oct");
		Task b = new Task("cs2101 blogpost", new DateTime(2012, 10, 29, 23, 59, 0));
		assertEquals(a.showInfo(), b.showInfo());
	}
	
	@Test
	public void deadlineTaskRelative() {
		Task expected = new Task("cs2101 blogpost", new DateTime().withDayOfWeek(5).withTime(23, 59, 0, 0));
		Task actual = CommandTester("add cs2101 blogpost by friday");		
		assertEquals(expected.showInfo(), actual.showInfo());
	}
	
	@Test
	public void deadlineTaskRelative2() {
		Task expected = new Task("cs2101 blogpost", new DateTime().withTime(23, 59, 0, 0));
		Task actual = CommandTester("add cs2101 blogpost by today");		
		assertEquals(expected.showInfo(), actual.showInfo());
	}
	
	@Test
	public void timedTask() {
		Task a = CommandTester("add midterm test 30 oct 2pm to 5pm");
		Task b = new Task("midterm test", new DateTime(2012, 10, 30, 14, 0, 0), new DateTime(2012, 10, 30, 17, 0, 0));
		assertEquals(a.showInfo(), b.showInfo());
	}
	
	@Test
	public void timedTask2() {
		Task a = CommandTester("add midterm test 30 oct 2pm to 5pm");
		Task b = new Task("midterm test", new DateTime(2012, 10, 30, 14, 0, 0), new DateTime(2012, 10, 30, 17, 0, 0));
		assertEquals(a.showInfo(), b.showInfo());
	}
	
	@Test
	public void floatingTask1a() {
		Task a = CommandTester("add read textbook page 1103");
		Task b = new Task("read textbook page", new DateTime().withTime(11, 03, 0, 0));
		assertEquals(a.showInfo(), b.showInfo());
	}
	
	@Test
	public void floatingTask1b() {
		Task expected = new Task("read textbook page 1103");
		Task actual = CommandTester("add \"read textbook page 1103\"");		
		assertEquals(expected.showInfo(), actual.showInfo());
	}
	
	@Test
	public void taskWithVenue() {
		Task expected = new Task("project meeting at com", new DateTime().withTime(15, 0, 0, 0));
		Task actual = CommandTester("add project meeting at 3pm at com");		
		assertEquals(expected.showInfo(), actual.showInfo());
	}
	
	@Test
	public void taskWithVenue2() {
		Task expected = new Task("project meeting at com", new DateTime().withTime(15, 0, 0, 0));
		Task actual = CommandTester("add project meeting at com at 3pm");		
		assertEquals(expected.showInfo(), actual.showInfo());
	}
	
	@Test
	public void taskWithEmptyName() {
	//	CommandTester("add ");
	
	}
	//Todo: return logicToUi after each add command. Its better to test the return message to see if the correct error has been found. 
	public Task CommandTester(String command) {
		List<Task> backup = Database.getInstance().getAll();
		Logic.getInstance().uiCommunicator("delete all");
		LogicToUi returnValue = Logic.getInstance().uiCommunicator(command);
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
