package test.logic;

import main.logic.Logic;

import static org.junit.Assert.*;

import org.junit.Test;

public class LogicTest {

	Logic logic = Logic.getInstance();

	@Test
	public void testAddTimedTask() {
		String command, expectedResponse;


		// Add normal timed task with starting and ending time and date
		command = "add My New Task from 25 Sep 2012 8pm to 26 Sep 2012 9pm";
		expectedResponse = timedTaskResponse("My New Task", "Tue 25 Sep 2012 8:00PM", 
				"Wed 26 Sep 2012 9:00PM");
		assertEquals(expectedResponse,logic.uiCommunicator(command).getString());
		
		// Add normal timed task with starting and ending date but no time.
		// Assume 12:00am for start time and 23:59pm for end time.
		command = "add My New Task from 25 Sep 2012 to 26 Sep 2012";
		expectedResponse = timedTaskResponse("My New Task", "Tue 25 Sep 2012 12:00AM", 
				"Wed 26 Sep 2012 11:59PM");
		assertEquals(expectedResponse,logic.uiCommunicator(command).getString());
		
		// Add normal timed task with starting and ending time and date
		// but with "from" inside the description
		command = "add Pick up groceries from supermarket from 25 Sep 2012 9pm to 26 Sep 2012 10pm";
		expectedResponse = timedTaskResponse("Pick up groceries from supermarket", 
				"Tue 25 Sep 2012 9:00PM", "Wed 26 Sep 2012 10:00PM");
		assertEquals(expectedResponse,logic.uiCommunicator(command).getString());

		// Add normal timed task with starting and ending time and date
		// but with "to" inside the description
		command = "add Go to somewhere from 25 Sep 2012 10pm to 26 Sep 2012 11pm";
		expectedResponse = timedTaskResponse("Go to somewhere", "Tue 25 Sep 2012 10:00PM", 
				"Wed 26 Sep 2012 11:00PM");
		assertEquals(expectedResponse,logic.uiCommunicator(command).getString());
		
		//Add normal timed task with starting and ending time and date
		//but have a string that is short form of a month. Ex: go to market, "mar" is
		//short form of March.
		command = "add go to market from 25 Mar 2013 9pm to 26 Mar 2013 10pm";
		expectedResponse = timedTaskResponse("go to market", "Mon 25 Mar 2013 9:00PM", 
				"Tue 26 Mar 2013 10:00PM");
		assertEquals(expectedResponse, logic.uiCommunicator(command).getString());
		
		//Add normal timed task with starting and ending date (no time)
		//but have a string that is short form of a month. Ex: go to market, "mar" is
		//short form of March.
		command = "add go to market from 25 Mar 2012 to 26 Mar 2012";
		expectedResponse = timedTaskResponse("go to market", "Sun 25 Mar 2012 12:00AM", 
				"Mon 26 Mar 2012 11:59PM");
		assertEquals(expectedResponse, logic.uiCommunicator(command).getString());
		
		//Add normal timed task with starting and ending date and time
		//in which start time and end time are in reverse order
		command = "add My New Task from 26 Sep 2012 9pm to 25 Sep 2012 8pm";
		expectedResponse = timedTaskResponse("My New Task", "Tue 25 Sep 2012 8:00PM", 
				"Wed 26 Sep 2012 9:00PM");
		assertEquals(expectedResponse,logic.uiCommunicator(command).getString());
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
		assertEquals(expectedResponse,logic.uiCommunicator(command).getString());

		// Add normal deadline task with ending date but no time.
		// Assume 11:59pm for time.
		command = "add Do homework by 26 Sep 2012";
		expectedResponse = deadlineTaskResponse("Do homework", "Wed 26 Sep 2012 11:59PM");
		assertEquals(expectedResponse,logic.uiCommunicator(command).getString());

		// Add normal deadline task with starting and ending time and date
		// but with "by" inside the text
		command = "add Read book by Steven by 26 Sep 2012 9pm";
		expectedResponse = deadlineTaskResponse("Read book by Steven", "Wed 26 Sep 2012 9:00PM");
		assertEquals(expectedResponse,logic.uiCommunicator(command).getString());

		// Add deadline task with the word "at"
		command = "add go to meeting at 15 Oct 2012 noon";
		expectedResponse = deadlineTaskResponse("go to meeting", "Mon 15 Oct 2012 12:00PM");

		
		// Add deadline task with only year
		command = "add fly to Germany by 2013";
		expectedResponse = deadlineTaskResponse("fly to Germany", "Tue 31 Dec 2013 23:59PM");

		
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
		assertEquals(expectedResponse,logic.uiCommunicator(command).getString());

		// Add floating task with tricky words "from"
		command = "add Print photos from Alan";
		expectedResponse = floatingTaskResponse("Print photos from Alan");
		assertEquals(expectedResponse,logic.uiCommunicator(command).getString());

		// Add floating task with tricky words "by"
		command = "add Print photos taken by Alan";
		expectedResponse = floatingTaskResponse("Print photos taken by Alan");
		assertEquals(expectedResponse,logic.uiCommunicator(command).getString());

		// Add floating task with tricky words "to"
		command = "add Bring photos to printing shop";
		expectedResponse = floatingTaskResponse("Bring photos to printing shop");
		assertEquals(expectedResponse,logic.uiCommunicator(command).getString());

		// Add floating task with tricky words "market" (starts with mar = march)
		command = "add Go to market";
		expectedResponse = floatingTaskResponse("Go to market");
		assertEquals(expectedResponse,logic.uiCommunicator(command).getString() );
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
		expectedResponse = "Timed task \"My New Task\" from Tue 25 Sep 2012 8:00PM to Wed 26 " +
				"Sep 2012 9:00PM has been deleted.";
		assertEquals(expectedResponse,logic.uiCommunicator(command).getString());

		// Delete invalid task
		command = "delete 9999";
		expectedResponse = "Sorry this index number or parameter you provided is not valid. " +
				"Please try again with a correct number or refresh the list.";
		assertEquals(expectedResponse,logic.uiCommunicator(command).getString());

		// Delete invalid task
		command = "delete 0";
		expectedResponse = "Sorry this index number or parameter you provided is not valid. " +
				"Please try again with a correct number or refresh the list.";
		assertEquals(expectedResponse,logic.uiCommunicator(command).getString());

		// Delete invalid task
		command = "delete -1";
		expectedResponse = "Sorry this index number or parameter you provided is not valid. " +
				"Please try again with a correct number or refresh the list.";
		assertEquals(expectedResponse,logic.uiCommunicator(command).getString());
	}

	@Test
	public void testList() {
		String command;


		// Delete valid task
		command = "list";
		assertTrue(logic.uiCommunicator(command).getList().size() > 0);
	}


}
