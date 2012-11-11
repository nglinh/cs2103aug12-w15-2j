package test.logic;

import main.logic.Logic;

import static org.junit.Assert.*;

import org.junit.Test;

public class LogicTest {

	Logic logic = Logic.getInstance();

	

	public String deadlineTaskResponse(String taskName, String endDate){
		return "Deadline task \""+taskName+"\" by "+endDate+" added";
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
