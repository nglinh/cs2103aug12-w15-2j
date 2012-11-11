//@author A0081007U
package test.logic;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import main.logic.Logic;
import main.shared.LogicToUi;
import main.shared.Task;
import main.storage.Database;
import main.storage.WillNotWriteToCorruptFileException;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

public class SearchTest {
	
	
	private static final String NAME_ONLY = "name only super long super long super long super long super long super long super long super long super long super long super long super long super long super long";
	private static final String NAME_TRUE = "name true";
	private static final String NAME_FALSE = "name false";
	private static final String NAME_1MONTH = "name +1month";
	private static final String NAME_1MONTH_FALSE = "name -1month false";
	
	private static final String NAME_FROM_TOMORROW_3_5 = "name tomorrow from 3 - 5pm";
	private static final String NAME_FROM_YESTERDAY_TO_TOMORROW = "name from yesterday to tomorrow";
	private static final String NAME_FROM_NOW_TO_TOMORROW_0000_FALSE = "name from now to tomorrow 12am false";

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
	public void testNoKeywords() {
		String command = "search ";
		List<Task> results = CommandTester(command);
		assertEquals(null, results);
	}
	
	@Test
	public void testOneKeyword() {
		String command = "search false";
		List<Task> results = CommandTester(command);
		assertEquals(3, results.size());
		
		for(Task entry : results){
			if(!entry.containsTerm("false")){
				fail();
			}
		}
	}
	
	@Test
	public void testManyKeyword() {
		String command = "search false 1";
		List<Task> results = CommandTester(command);
		assertEquals(2, results.size());
		
		for(Task entry : results){
			if(!entry.containsTerm("false")){
				fail();
			}
			
			if(!entry.containsTerm("1")){
				fail();
			}
		}
	}
	
	

	
	private List<Task> CommandTester(String command){
		Logic logic = Logic.getInstance();
		LogicToUi result = logic.uiCommunicator(command);
		return result.getList();
	}
}
