package test.logic;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import main.logic.Logic;
import main.shared.LogicToUi;
import main.shared.Task;
import main.shared.Task.TaskType;
import main.storage.Database;
import main.storage.WillNotWriteToCorruptFileException;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

public class SortTest {
	
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
	public void defaultSort() {
		List<Task> results;
		
		commandTester("list");
		results = commandTester("sort");
		
		DateTime previous = new DateTime(Long.MIN_VALUE);
		boolean fail = false;
		
		for(Task entry : results){
			DateTime currentComp;
			
			if(entry.isDeadlineTask()) {
				currentComp = entry.getDeadline();
			} else if( entry.isTimedTask()) {
				currentComp = entry.getStartDate();
			} else {
				currentComp = Task.INVALID_DATE_FIELD;
			}
			
			if(previous.isAfter(currentComp)) {
				fail = true;
			}
			
			previous = currentComp;
		}
		
		assertFalse(fail);
		
	}
	
	@Test
	public void startSort() {
		List<Task> results;
		
		commandTester("list");
		results = commandTester("sort start");
		
		DateTime previous = new DateTime(Long.MIN_VALUE);
		boolean fail = false;
		
		for(Task comp : results){
			DateTime currentComp;
			
			if(comp.isDeadlineTask()) {
				currentComp = comp.getDeadline();
			} else if( comp.isTimedTask()) {
				currentComp = comp.getStartDate();
			} else {
				currentComp = Task.INVALID_DATE_FIELD;
			}
			
			if(previous.isAfter(currentComp)) {
				fail = true;
			}
			
			previous = currentComp;
		}
		
		assertFalse(fail);
		
	}
	
	@Test
	public void endSort() {
		List<Task> results;
		
		commandTester("list");
		results = commandTester("sort end");
		
		boolean fail = false;
		
		DateTime previous = new DateTime(Long.MIN_VALUE);
		for(Task comp : results) {
			DateTime currentComp;
			
			if(comp.isDeadlineTask()) {
				currentComp = comp.getDeadline();
			} else if( comp.isTimedTask()) {
				currentComp = comp.getEndDate();
			} else {
				currentComp = Task.INVALID_DATE_FIELD;
			}
			
			if(previous.isAfter(currentComp)) {
				fail = true;
			}
			
			previous = currentComp;
			
		}
		assertFalse(fail);
		
	}
	
	@Test
	public void typeSort() {
		List<Task> results;
		
		commandTester("list");
		results = commandTester("sort type");
		
		boolean fail = false;
		
		//In order of deadline, timed and floating
		TaskType previous = TaskType.DEADLINE;
		for(Task comp : results) {
			if(previous.equals(TaskType.TIMED) && comp.getType().equals(TaskType.DEADLINE)) {
				fail = true;
			}
			
			if(previous.equals(TaskType.FLOATING) && comp.getType().equals(TaskType.TIMED)) {
				fail = true;
			}
			
			if(previous.equals(TaskType.FLOATING) && comp.getType().equals(TaskType.DEADLINE)) {
				fail = true;
			}
			
			previous = comp.getType();
		}
		assertFalse(fail);
	}
	
	
	@Test
	public void doneSort() {
		List<Task> results;
		
		commandTester("list");
		results = commandTester("sort done");

		boolean fail = false;
		
		//Undone tasks come first
		boolean previous = false;
		for(Task comp : results) {
			if(previous && (comp.isDone() == false)) {
				fail = true;
			}
			
			previous = comp.isDone();
		}
		assertFalse(fail);

	}
	
	@Test
	public void nameSort(){
		
		final int COMPARETO_BIGGER = 1;
		List<Task> results;
		
		commandTester("list");
		results = commandTester("sort name");
		boolean fail = false;
		
		//Undone tasks come first
		String previous = " ";
		for(Task comp : results) {
			String compName = comp.getTaskName();
			if(previous.compareTo(compName) == COMPARETO_BIGGER) {
				fail = true;
			}
			
			previous = compName;
		}
		assertFalse(fail);
	}
	
	
	
	@Test
	public void reverseDoneSort() {
		List<Task> results;
		
		commandTester("list");
		results = commandTester("sort done reverse");

		boolean fail = false;
		
		//done tasks come first
		boolean previous = true;
		for(Task comp : results) {
			if(previous == false && comp.isDone()) {
				fail = true;
			}
			
			previous = comp.isDone();
		}
		assertFalse(fail);

	}
	
	
	
	
	private List<Task> commandTester(String command){
		Logic logic = Logic.getInstance();
		LogicToUi result = logic.uiCommunicator(command);
		return result.getList();
	}

}
