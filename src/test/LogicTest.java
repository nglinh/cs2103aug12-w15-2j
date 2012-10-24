package test;

import logic.Logic;

import static org.junit.Assert.*;

import org.junit.Test;

public class LogicTest {
	
	Logic logic = Logic.getInstance();

	@Test
	public void testAddTimedTask() {
		String command, expectedResponse;

		
		// Add normal timed task with starting and ending time and date
		command = "add My New Task from 25 Sep 2012 8pm to 26 Sep 2012 9pm";
		expectedResponse = timedTaskResponse("My New Task", "Tue 25 Sep 2012 8:00PM", "Wed 26 Sep 2012 9:00PM");
		assertEquals(logic.uiCommunicator(command).getString(), expectedResponse);

		// Add normal timed task with starting and ending date but no time.
		// Assume 12:00am for start time and 23:59pm for end time.
		command = "add My New Task from 25 Sep 2012 to 26 Sep 2012";
		expectedResponse = timedTaskResponse("My New Task", "Tue 25 Sep 2012 12:00AM", "Wed 26 Sep 2012 11:59PM");
		assertEquals(logic.uiCommunicator(command).getString(), expectedResponse);
		
		// Add normal timed task with starting and ending time and date
		// but with "from" inside the text
		command = "add Pick up groceries from supermarket from 25 Sep 2012 8pm to 26 Sep 2012 9pm";
		expectedResponse = timedTaskResponse("Pick up groceries from supermarket", "Tue 25 Sep 2012 8:00PM", "Wed 26 Sep 2012 9:00PM");
		assertEquals(logic.uiCommunicator(command).getString(), expectedResponse);
		
		// Add normal timed task with starting and ending time and date
		// but with "to" inside the text
		command = "add Go to supermarket from 25 Sep 2012 8pm to 26 Sep 2012 9pm";
		expectedResponse = timedTaskResponse("Go to supermarket", "Tue 25 Sep 2012 8:00PM", "Wed 26 Sep 2012 9:00PM");
		assertEquals(logic.uiCommunicator(command).getString(), expectedResponse);
	}
	
	public String timedTaskResponse(String taskName, String startDate, String endDate){
		return "Timed task \""+taskName+"\" from "+startDate+" to "+endDate+" added";
	}
	
	@Test
	public void testAddDeadlineTask() {
		String command, expectedResponse;
		
		// Add normal deadline task with ending time and date
		command = "add Do homework by 26 Sep 2012 9pm";
		expectedResponse = deadlineTaskResponse("Do homework", "Wed 26 Sep 2012 9:00PM");
		assertEquals(logic.uiCommunicator(command).getString(), expectedResponse);

		// Add normal deadline task with ending date but no time.
		// Assume 11:59pm for time.
		command = "add Do homework by 26 Sep 2012";
		expectedResponse = deadlineTaskResponse("Do homework", "Wed 26 Sep 2012 11:59PM");
		assertEquals(logic.uiCommunicator(command).getString(), expectedResponse);
		
		// Add normal deadline task with starting and ending time and date
		// but with "by" inside the text
		command = "add Read book by Steven by 26 Sep 2012 9pm";
		expectedResponse = deadlineTaskResponse("Read book by Steven", "Wed 26 Sep 2012 9:00PM");
		assertEquals(logic.uiCommunicator(command).getString(), expectedResponse);
		
		// Add deadline task with the word "at"
		command = "add go to meeting at 15 Oct 2012 noon";
		expectedResponse = deadlineTaskResponse("go to meeting", "Mon 15 Oct 2012 12:00PM");
		
		/* Commented out as this the interpretation can be correct in different ways
		
		// Add deadline task with only year
		command = "add fly to Germany by 2013";
		expectedResponse = deadlineTaskResponse("fly to Germany", "Tue 31 Dec 2013 23:59PM");
		
		*/
	}
	
	public String deadlineTaskResponse(String taskName, String endDate){
		return "Deadline task \""+taskName+"\" by "+endDate+" added";
	}
	
	@Test
	public void testAddFloatingTask() {
		String command, expectedResponse;
		
		// Add normal floating task
		command = "add Just a normal floating task";
		expectedResponse = floatingTaskResponse("Just a normal floating task");
		assertEquals(logic.uiCommunicator(command).getString(), expectedResponse);

		// Add floating task with tricky words "from"
		command = "add Print photos from Alan";
		expectedResponse = floatingTaskResponse("Print photos from Alan");
		assertEquals(logic.uiCommunicator(command).getString(), expectedResponse);
		
		// Add floating task with tricky words "by"
		command = "add Print photos taken by Alan";
		expectedResponse = floatingTaskResponse("Print photos taken by Alan");
		assertEquals(logic.uiCommunicator(command).getString(), expectedResponse);
		
		// Add floating task with tricky words "to"
		command = "add Bring photos to printing shop";
		expectedResponse = floatingTaskResponse("Bring photos to printing shop");
		assertEquals(logic.uiCommunicator(command).getString(), expectedResponse);
		
		// Add floating task with tricky words "market" (starts with mar = march)
		command = "add Go to market";
		expectedResponse = floatingTaskResponse("Go to market");
		assertEquals(logic.uiCommunicator(command).getString(), expectedResponse);
	}
	
	public String floatingTaskResponse(String taskName){
		return "Floating task \""+taskName+"\" added";
	}
	
	@Test
	public void testDelete() {
		// TODO: List of tasks for testing is not confirmed!

		String command, expectedResponse;

		logic.uiCommunicator("list");

		// Delete valid task
		command = "delete 1";
		expectedResponse = "Timed task \"My New Task\" from 25 Sep 2012 8:00pm to 26 Sep 2012 9:00pm deleted";
		assertEquals(logic.uiCommunicator(command).getString(), expectedResponse);

		// Delete invalid task
		command = "delete 9999";
		expectedResponse = "9999 is an invalid index number";
		assertEquals(logic.uiCommunicator(command).getString(), expectedResponse);

		// Delete invalid task
		command = "delete 0";
		expectedResponse = "0 is an invalid index number";
		assertEquals(logic.uiCommunicator(command).getString(), expectedResponse);

		// Delete invalid task
		command = "delete -1";
		expectedResponse = "-1 is an invalid index number";
		assertEquals(logic.uiCommunicator(command).getString(), expectedResponse);
	}
	
	@Test
	public void testList() {
		String command;


		// Delete valid task
		command = "list";
		assertTrue(logic.uiCommunicator(command).getList().size() > 0);
	}


}
