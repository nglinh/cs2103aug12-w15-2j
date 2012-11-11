//@author A0081007U

package test.logic;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import main.logic.Logic;
import main.shared.LogicToUi;
import main.shared.Task;
import main.storage.Database;
import main.storage.WillNotWriteToCorruptFileException;

import org.joda.time.DateTime;
import org.junit.Test;

public class UndoTest {

	Database db = Database.getInstance();
	
	@Test
	public void undo() {
		try {
			List<Task> result;
			
			

			
			//Undo Add
			db.setAll(new ArrayList<Task>());
			commandTester("add 1 task");
			commandTester("add 2 task");
			
			result = commandTester("list");
			assertEquals(2, result.size());
			
			result = commandTester("undo");
			assertEquals(1, result.size());
			
			
			//Undo delete
			db.setAll(new ArrayList<Task>());
			commandTester("add 1 task");
			commandTester("add 2 task");
			commandTester("list");
			commandTester("delete 1");
			
			result = commandTester("list");
			assertEquals(1, result.size());
			
			result = commandTester("undo");
			assertEquals(2, result.size());
			
			//Undo Done
			db.setAll(new ArrayList<Task>());
			commandTester("add 1 task");
			commandTester("list");
			commandTester("done 1");
			result = commandTester("list");

			assertTrue(result.get(0).isDone());
			
			result = commandTester("undo");
			assertFalse(result.get(0).isDone());
			
			
			//Undo Undone
			db.setAll(new ArrayList<Task>());
			commandTester("add 1 task");
			commandTester("list");
			commandTester("done 1");
			commandTester("list");
			commandTester("undone 1");
			result = commandTester("list");

			assertFalse(result.get(0).isDone());
			
			result = commandTester("undo");
			assertTrue(result.get(0).isDone());
			
			//Undo edit
			db.setAll(new ArrayList<Task>());
			commandTester("add task1");
			commandTester("list");
			commandTester("edit 1 -name task2");
			result = commandTester("list");

			assertEquals("task2", result.get(0).getTaskName());
			
			result = commandTester("undo");
			assertEquals("task1", result.get(0).getTaskName());
			
			//Undo postpone
			db.setAll(new ArrayList<Task>());
			commandTester("add task1 today 12am");
			commandTester("list");
			commandTester("postpone 1 2 hours");
			result = commandTester("list");

			assertEquals(new DateTime().withTimeAtStartOfDay().plusHours(2), result.get(0).getDeadline());
			
			result = commandTester("undo");
			assertEquals(new DateTime().withTimeAtStartOfDay(), result.get(0).getDeadline());
			
			
			
			
		} catch (IOException | WillNotWriteToCorruptFileException e) {
			fail();
		}
		
	}
	
	
	
	
	
	private List<Task> commandTester(String command){
		Logic logic = Logic.getInstance();
		logic.uiCommunicator(command);
		LogicToUi result = logic.uiCommunicator("list");
		return result.getList();
	}

}
