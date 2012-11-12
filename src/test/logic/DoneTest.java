//@author A0081007U

package test.logic;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import main.logic.Logic;
import main.shared.Task;
import main.storage.Database;
import main.storage.WillNotWriteToCorruptFileException;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

public class DoneTest {
	
	private static final String NAME_ONLY = "name only super long super long super long super long super long super long super long super long super long super long super long super long super long super long";
	private static final String NAME_TRUE = "name true";
	private static final String NAME_FALSE = "name false";
	private static final String NAME_1MONTH = "name +1month";
	private static final String NAME_1MONTH_FALSE = "name -1month false";
	
	private static final String NAME_FROM_TOMORROW_3_5 = "name tomorrowfrom 3 - 5pm";
	private static final String NAME_FROM_YESTERDAY_TO_TOMORROW = "name from yesterday to tomorrow";
	private static final String NAME_FROM_NOW_TO_TOMORROW_0000_FALSE = "name from now to tomorrow 0000 false";

	private static final DateTime DEADLINE = new DateTime().plusMonths(1);
	private static final DateTime DEADLINE_FALSE = new DateTime().minusMonths(1);
	
	private static final DateTime TIMED_START_TML_3 = new DateTime().plusDays(1).withTime(15, 00, 00, 00);
	private static final DateTime TIMED_END_TML_5 = new DateTime().plusDays(1).withTime(17, 00, 00, 00);
	
	private static final DateTime TIMED_START = new DateTime().minusDays(1);
	private static final DateTime TIMED_END = new DateTime().plusDays(1);
	private static final DateTime TIMED_DONE_FALSE_START = new DateTime();
	private static final DateTime TIMED_DONE_FALSE_END = TIMED_DONE_FALSE_START.plusDays(1).withTimeAtStartOfDay();


	Task name;
	Task nameTrue;
	Task nameFalse;

	Task nameDeadline;
	Task nameDeadlineTrue;

	Task nameTML3To5;
	Task nameTimed;
	Task nameTimedFalse;


	List<Task> filledListing;
	List<Task> initialClearListing;
	
	Database db = Database.getInstance();
	
	
	@Before
	public void runBeforeEveryTest() {

		name = new Task(NAME_ONLY);
		nameTrue = new Task(NAME_TRUE, true);
		nameFalse = new Task(NAME_FALSE, false);

		nameDeadline = new Task(NAME_1MONTH, DEADLINE);
		nameDeadlineTrue = new Task(NAME_1MONTH_FALSE, DEADLINE_FALSE, false);

		nameTML3To5 = new Task(NAME_FROM_TOMORROW_3_5, TIMED_START_TML_3, TIMED_END_TML_5);
		nameTimed = new Task(NAME_FROM_YESTERDAY_TO_TOMORROW, TIMED_START, TIMED_END);
		nameTimedFalse = new Task(NAME_FROM_NOW_TO_TOMORROW_0000_FALSE, TIMED_DONE_FALSE_START, TIMED_DONE_FALSE_END, false);

		initialClearListing = new ArrayList<Task>();

		filledListing = new ArrayList<Task>();


		filledListing.add(nameDeadlineTrue);
		filledListing.add(nameTimed);
		filledListing.add(nameTimedFalse);
		filledListing.add(nameDeadline);
		filledListing.add(name);
		filledListing.add(nameFalse);
		filledListing.add(nameTrue);
		filledListing.add(nameTML3To5);



		Collections.sort(filledListing);

		try {
			db.setAll(filledListing);
			List<Task> readList = db.getAll();
			assertEquals(filledListing.size(), readList.size());

		} catch (IOException | WillNotWriteToCorruptFileException e) {
			fail();
		}
	}

	@Test
	public void outsideBoundarytest() {
		List<Task> afterResult; 
		
		commandTester("list");
		commandTester("done");
		afterResult = commandTester("list");
		for(int i = 0; i < filledListing.size(); i++){
			assertTrue(afterResult.get(i).isEqualTo(filledListing.get(i)));
		}
		
		
		commandTester("list");
		commandTester("done 0");
		afterResult = commandTester("list");
		for(int i = 0; i < filledListing.size(); i++){
			assertTrue(afterResult.get(i).isEqualTo(filledListing.get(i)));
		}
		
		commandTester("list");
		commandTester("done -1");
		afterResult = commandTester("list");
		for(int i = 0; i < filledListing.size(); i++){
			assertTrue(afterResult.get(i).isEqualTo(filledListing.get(i)));
		}
		
		commandTester("list");
		commandTester("done " + (filledListing.size() + 1 ));
		afterResult = commandTester("list");
		for(int i = 0; i < filledListing.size(); i++){
			assertTrue(afterResult.get(i).isEqualTo(filledListing.get(i)));
		}
		
		commandTester("list");
		commandTester("done a");
		afterResult = commandTester("list");
		for(int i = 0; i < filledListing.size(); i++){
			assertTrue(afterResult.get(i).isEqualTo(filledListing.get(i)));
		}
		
		commandTester("list");
		commandTester("done 1.2");
		afterResult = commandTester("list");
		for(int i = 0; i < filledListing.size(); i++){
			assertTrue(afterResult.get(i).isEqualTo(filledListing.get(i)));
		}
		
	}
	
	@Test
	public void doneUndoneTask(){
		commandTester("delete all");
		
		List<Task> afterResult; 
		
		//Add one task
		
		commandTester("add a test task");
		afterResult = commandTester("list");
		assertFalse(afterResult.get(0).isDone());
		
		//Done 1 task
		commandTester("done 1");
		afterResult = commandTester("list");
		assertTrue(afterResult.get(0).isDone());
		
		//Done 1 task
		commandTester("done 1");
		afterResult = commandTester("list");
		assertTrue(afterResult.get(0).isDone());
	

	}
	
	
	private List<Task> commandTester(String command){
		Logic logic = Logic.getInstance();
		logic.uiCommunicator(command);
		return db.getAll();
	}

}
