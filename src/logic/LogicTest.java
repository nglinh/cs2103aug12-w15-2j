package logic;

import static org.junit.Assert.*;

import org.junit.Test;

public class LogicTest {

	@Test
	public void testAddTimedTask() {
		String command, expectedResponse;
		Logic l = new Logic();
		
		// Add normal timed task with starting and ending time and date
		command = "add My New Task from 25 Sep 2012 8pm to 26 Sep 2012 9pm";
		expectedResponse = "Timed task \"My New Task\" from 25 Sep 2012 8:00pm to 26 Sep 2012 9:00pm added";
		assertEquals(l.uiCommunicator(command).getString(), expectedResponse);

		// Add normal timed task with starting and ending date but no time.
		// Assume 12:00am for start time and 23:59pm for end time.
		command = "add My New Task from 25 Sep 2012 to 26 Sep 2012";
		expectedResponse = "Timed task \"My New Task\" from 25 Sep 2012 12:00am to 26 Sep 2012 11:59pm added";
		assertEquals(l.uiCommunicator(command).getString(), expectedResponse);
		
		// Add normal timed task with starting and ending time and date
		// but with "from" inside the text
		command = "add Pick up groceries from supermarket from 25 Sep 2012 8pm to 26 Sep 2012 9pm";
		expectedResponse = "Timed task \"Pick up groceries from supermarket\" from 25 Sep 2012 8:00pm to 26 Sep 2012 9:00pm added";
		assertEquals(l.uiCommunicator(command).getString(), expectedResponse);
		
		// Add normal timed task with starting and ending time and date
		// but with "to" inside the text
		command = "add Go to supermarket from 25 Sep 2012 8pm to 26 Sep 2012 9pm";
		expectedResponse = "Timed task \"Go to supermarket\" from 25 Sep 2012 8:00pm to 26 Sep 2012 9:00pm added";
		assertEquals(l.uiCommunicator(command).getString(), expectedResponse);
	}
	
	@Test
	public void testAddDeadlineTask() {
		String command, expectedResponse;
		Logic l = new Logic();
		
		// Add normal deadline task with ending time and date
		command = "add Do homework by 26 Sep 2012 9pm";
		expectedResponse = "Deadline task \"Do homework\" by 26 Sep 2012 9:00pm added";
		assertEquals(l.uiCommunicator(command).getString(), expectedResponse);

		// Add normal deadline task with ending date but no time.
		// Assume 11:59pm for time.
		command = "add Do homework by 26 Sep 2012";
		expectedResponse = "Deadline task \"Do homework\" by 26 Sep 2012 11:59pm added";
		assertEquals(l.uiCommunicator(command).getString(), expectedResponse);
		
		// Add normal deadline task with starting and ending time and date
		// but with "by" inside the text
		command = "add Read book by Steven by 26 Sep 2012 9pm";
		expectedResponse = "Deadline task \"Read book by Steven\" by 26 Sep 2012 9:00pm added";
		assertEquals(l.uiCommunicator(command).getString(), expectedResponse);
	}
	
	@Test
	public void testAddFloatingTask() {
		String command, expectedResponse;
		Logic l = new Logic();
		
		// Add normal floating task
		command = "add Just a normal floating task";
		expectedResponse = "Floating task \"Just a normal floating task\" added";
		assertEquals(l.uiCommunicator(command).getString(), expectedResponse);

		// Add floating task with tricky words "from"
		command = "add Print photos from Alan";
		expectedResponse = "Floating task \"Print photos from Alan\" added";
		assertEquals(l.uiCommunicator(command).getString(), expectedResponse);
		
		// Add floating task with tricky words "by"
		command = "add Print photos taken by Alan";
		expectedResponse = "Floating task \"Print photos taken by Alan\" added";
		assertEquals(l.uiCommunicator(command).getString(), expectedResponse);
		
		// Add floating task with tricky words "to"
		command = "add Bring photos to printing shop";
		expectedResponse = "Floating task \"Bring photos to printing shop\" added";
		assertEquals(l.uiCommunicator(command).getString(), expectedResponse);
	}
	
	@Test
	public void testDelete() {
		// TODO: List of tasks for testing is not confirmed!

		String command, expectedResponse;
		Logic l = new Logic();

		l.uiCommunicator("list");

		// Delete valid task
		command = "delete 1";
		expectedResponse = "Timed task \"My New Task\" from 25 Sep 2012 8:00pm to 26 Sep 2012 9:00pm deleted";
		assertEquals(l.uiCommunicator(command).getString(), expectedResponse);

		// Delete invalid task
		command = "delete 9999";
		expectedResponse = "9999 is an invalid index number";
		assertEquals(l.uiCommunicator(command).getString(), expectedResponse);

		// Delete invalid task
		command = "delete 0";
		expectedResponse = "0 is an invalid index number";
		assertEquals(l.uiCommunicator(command).getString(), expectedResponse);

		// Delete invalid task
		command = "delete -1";
		expectedResponse = "-1 is an invalid index number";
		assertEquals(l.uiCommunicator(command).getString(), expectedResponse);
	}
	
	@Test
	public void testList() {
		String command;
		Logic l = new Logic();

		// Delete valid task
		command = "list";
		assertTrue(l.uiCommunicator(command).getList().size() > 0);
	}


}
