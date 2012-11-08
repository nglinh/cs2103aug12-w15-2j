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
	Logic logic = Logic.getInstance();
	String command, expectedResponse;
	@Test
	public void timedTask() {
		Task result = CommandTester("add midterm test 30 oct 2pm to 5pm");
		Task expected = new Task("midterm test", new DateTime(2012, 10, 30, 14, 0, 0), new DateTime(2012, 10, 30, 17, 0, 0));
		assertEquals(result.showInfo(), expected.showInfo());
	}

	@Test
	public void timedTask2() {
		Task result = CommandTester("add midterm test 30 oct 2pm to 5pm");
		Task expected = new Task("midterm test", new DateTime(2012, 10, 30, 14, 0, 0), new DateTime(2012, 10, 30, 17, 0, 0));
		assertEquals(result.showInfo(), expected.showInfo());
	}

	@Test
	public void timedTask3(){
		// Add normal timed task with starting and ending time and date
		Task result =  CommandTester("add My New Task from 25 Sep 2012 8pm to 26 Sep 2012 9pm");
		Task expected = new Task("My New Task", new DateTime(2012,9,25,20,00,00),
				new DateTime(2012,9,26,21,00,00));
		assertEquals(expected.showInfo(),result.showInfo());
	}
	
	@Test
	public void timedTask4(){
		// Add normal timed task with starting and ending date but no time.
		// Assume 12:00am for start time and 23:59pm for end time.
		Task result =  CommandTester("add My New Task from 25 Sep 2012 to 26 Sep 2012");
		Task expected = new Task("My New Task", new DateTime(2012,9,25,00,00,00), 
				new DateTime(2012,9,26,23,59,00));
		assertEquals(expected.showInfo(),result.showInfo());
	}
	@Test
	public void timedTask5(){
		// Add normal timed task with starting and ending time and date
		// but with "from" inside the description
		Task result =  CommandTester("add Pick up groceries from supermarket from 25 Sep 2012 9pm to 26 Sep 2012 10pm");
		Task expected = new Task("Pick up groceries from supermarket", new DateTime(2012, 9,25,21,00,00),
				new DateTime(2012,9,26,22,00,00));
		assertEquals(expected.showInfo(),result.showInfo());
	}
	@Test
	public void timedTask6(){
		// Add normal timed task with starting and ending time and date
		// but with "to" inside the description
		Task result = CommandTester("add Go to somewhere from 25 Sep 2012 10pm to 26 Sep 2012 11pm");
		Task expected = new Task("Go to somewhere", new DateTime(2012,9,25,22,00,00),
				new DateTime(2012,9,26,23,00,00));
		assertEquals(expected.showInfo(),result.showInfo());
	}
	
	@Test
	public void timeTask7(){
		//Add normal timed task with starting and ending time and date
		//but have a string that is short form of a month. Ex: go to market, "mar" is
		//short form of March.
		Task result = CommandTester("add go to market from 25 Mar 2013 9pm to 26 Mar 2013 10pm");
		Task expected = new Task("go to market",new DateTime(2013,3,25,21,00,00),
				new DateTime(2013,3,26,22,00,00));
		assertEquals(expected.showInfo(), result.showInfo() );
	}
	@Test
	public void timeTask8(){
		//Add normal timed task with starting and ending date (no time)
		//but have a string that is short form of a month. Ex: go to market, "mar" is
		//short form of March.
		Task result = CommandTester("add go to market from 25 Mar 2012 to 26 Mar 2012");
		Task expected = new Task("go to market", new DateTime(2012,3,25,00,0,0),
				new DateTime(2012,3,26,23,59,0));
		assertEquals(expected.showInfo(),result.showInfo());
	}

	@Test
	public void timeTask9(){
		//Add normal timed task with starting and ending date and time
		//in which start time and end time are in reverse order
		Task result = CommandTester("add My New Task from 26 Sep 2012 9pm to 25 Sep 2012 8pm");
		Task expected = new Task("My New Task", new DateTime(2012,9, 25, 20,0,0),
				new DateTime(2012,9,26,21,0,0)); 
		assertEquals(expected.showInfo(),result.showInfo());
	}
	
	@Test
	public void deadlineTask() {
		Task result = CommandTester("add cs2101 blogpost 29 oct at 23:59");
		Task expected = new Task("cs2101 blogpost", new DateTime(2012, 10, 29, 23, 59, 0));
		assertEquals(result.showInfo(), expected.showInfo());
	}
	
	@Test
	public void deadlineTask2() {
		Task result = CommandTester("add cs2101 blogpost by 29 oct");
		Task expected = new Task("cs2101 blogpost", new DateTime(2012, 10, 29, 23, 59, 0));
		assertEquals(result.showInfo(), expected.showInfo());
	}
	
	@Test
	public void deadlineTaskRelative() {
		Task expected = new Task("cs2101 blogpost", new DateTime().withDayOfWeek(5).withTime(23, 59, 0, 0));
		Task result = CommandTester("add cs2101 blogpost by friday");		
		assertEquals(expected.showInfo(), result.showInfo());
	}
	
	@Test
	public void deadlineTaskRelative2() {
		Task expected = new Task("cs2101 blogpost", new DateTime().withTime(23, 59, 0, 0));
		Task result = CommandTester("add cs2101 blogpost by today");		
		assertEquals(expected.showInfo(), result.showInfo());
	}
	
	@Test
	public void taskWithVenue2() {
		Task expected = new Task("project meeting at com", new DateTime().withTime(15, 0, 0, 0));
		Task result = CommandTester("add project meeting at com at 3pm");		
		assertEquals(expected.showInfo(), result.showInfo());
	}

	@Test
	public void taskWithVenue() {
		Task expected = new Task("project meeting at com", new DateTime().withTime(15, 0, 0, 0));
		Task result = CommandTester("add project meeting at 3pm at com");		
		assertEquals(expected.showInfo(), result.showInfo());
	}

	@Test
	public void floatingTask9() {
		String command, expectedResponse;
	
		// Add normal floating task
		command = "add Just a normal floating task";
		expectedResponse = floatingTaskResponse("Just a normal floating task");
		assertEquals(expectedResponse,logic.uiCommunicator(command).getString());
	
		floatingTask2();
	
		floatingTask3();
	
		floatingTask4();
	
		floatingTask5();
	}

	@Test
	public void floatingTask8() {
		Task result = CommandTester("add hello world");
		Task expected = new Task("hello world");
		assertEquals(result.showInfo(), expected.showInfo());
	}

	@Test
	public void floatingTask7() {
		Task result = CommandTester("add read textbook page 1103");
		Task expected = new Task("read textbook page", new DateTime().withTime(11, 03, 0, 0));
		assertEquals(result.showInfo(), expected.showInfo());
	}
	
	@Test
	public void floatingTask6() {
		Task expected = new Task("read textbook page 1103");
		Task result = CommandTester("add \"read textbook page 1103\"");		
		assertEquals(expected.showInfo(), result.showInfo());
	}
	
	public void floatingTask5() {
		String command;
		String expectedResponse;
		// Add floating task with tricky words "market" (starts with mar = march)
		command = "add Go to market";
		expectedResponse = floatingTaskResponse("Go to market");
		assertEquals(expectedResponse,logic.uiCommunicator(command).getString() );
	}

	public void floatingTask4() {
		String command;
		String expectedResponse;
		// Add floating task with tricky words "to"
		command = "add Bring photos to printing shop";
		expectedResponse = floatingTaskResponse("Bring photos to printing shop");
		assertEquals(expectedResponse,logic.uiCommunicator(command).getString());
	}

	public void floatingTask3() {
		String command;
		String expectedResponse;
		// Add floating task with tricky words "by"
		command = "add Print photos taken by Alan";
		expectedResponse = floatingTaskResponse("Print photos taken by Alan");
		assertEquals(expectedResponse,logic.uiCommunicator(command).getString());
	}

	public void floatingTask2() {
		String command;
		String expectedResponse;
		// Add floating task with tricky words "from"
		command = "add Print photos from Alan";
		expectedResponse = floatingTaskResponse("Print photos from Alan");
		assertEquals(expectedResponse,logic.uiCommunicator(command).getString());
	}
	@Test
	public void taskWithEmptyName() {
		//CommandTester("add ");
		
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
	public String timedTaskResponse(String taskName, String startDate, String endDate){
		return "Timed task \""+taskName+"\" from "+startDate+" to "+endDate+" added";
	}
	public String deadlineTaskResponse(String taskName, String endDate){
		return "Deadline task \""+taskName+"\" by "+endDate+" added";
	}
	public String floatingTaskResponse(String taskName){
		return "Floating task \""+taskName+"\" added";
	}
}
