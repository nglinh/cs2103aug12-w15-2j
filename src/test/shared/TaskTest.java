//@author A0081007U
package test.shared;

/**  
 * TaskTest.java 
 * A Junit4 test for the Task class
 * @author  Yeo Kheng Meng
 */ 



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import main.shared.Task;
import main.shared.Task.TaskType;

public class TaskTest {

	private static final String NAME_ONLY = "name only";
	private static final String NAME_TRUE = "name true";
	private static final String NAME_1MONTH = "name +1month";
	private static final String NAME_1MONTH_TRUE = "name -1month true";
	private static final String NAME_FROM_YESTERDAY_TO_TOMORROW = "name from yesterday to tomorrow";
	private static final String NAME_FROM_NOW_TO_TOMORROW_0000_FALSE = "name from now to tomorrow 0000 false";
	
	private static final DateTime DEADLINE = new DateTime().plusMonths(1);
	private static final DateTime DEADLINE_TRUE = new DateTime().minusMonths(1);
	private static final DateTime TIMED_START = new DateTime().minusDays(1);
	private static final DateTime TIMED_END = new DateTime().plusDays(1);
	private static final DateTime TIMED_DONE_FALSE_START = new DateTime();
	private static final DateTime TIMED_DONE_FALSE_END = TIMED_DONE_FALSE_START.plusDays(1).withTimeAtStartOfDay();
	
	
	private static final int COMPARETO_SMALLER = -1;
	private static final int COMPARETO_EQUAL = 0;
	private static final int COMPARETO_BIGGER = 1;
	
	Task name;
	Task nameTrue;

	Task nameDeadline;
	Task nameDeadlineTrue;

	Task nameTimed;
	Task nameTimedFalse;

	ArrayList<Task> listing;

	
	@Before
	public void runBeforeEveryTest() {
		name = new Task(NAME_ONLY);
		nameTrue = new Task(NAME_TRUE, true);

		nameDeadline = new Task(NAME_1MONTH, DEADLINE);
		nameDeadlineTrue = new Task(NAME_1MONTH_TRUE, DEADLINE_TRUE, true);

		nameTimed = new Task(NAME_FROM_YESTERDAY_TO_TOMORROW, TIMED_START, TIMED_END);
		nameTimedFalse = new Task(NAME_FROM_NOW_TO_TOMORROW_0000_FALSE, TIMED_DONE_FALSE_START, TIMED_DONE_FALSE_END, false);
	
		listing = new ArrayList<Task>();
		
		listing.add(name);
		listing.add(nameDeadline);
		listing.add(nameTimedFalse);
		
		listing.add(nameTrue);
		listing.add(nameDeadlineTrue);
		
		listing.add(nameTimed);
		listing.add(name);
	}



	public void testTaskString() {
		try{
			new Task("");
			fail();
		} catch (IllegalArgumentException e) {
		}
		
		new Task("1");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testTaskStringBooleanString() {
		new Task(null, true);
	}

	@Test
	public void testTaskStringDateTime() {

		try{
			new Task("test", null);
			fail();
		} catch (IllegalArgumentException e) {
		}


		try{
			new Task("test", Task.INVALID_DATE_FIELD);
			fail();
		} catch (IllegalArgumentException e) {
		}

	}
	
	@Test
	public void testTaskStringDateTimeBoolean() {

		try{
			new Task("test", TIMED_DONE_FALSE_START, true);
		} catch (AssertionError e) {
			fail();
		}


		try{
			new Task("test", TIMED_DONE_FALSE_START, false);
		} catch (AssertionError e) {
			fail();
		}

	}


	@Test
	public void testTaskStringDateTimeDateTime() {
		try{
			new Task("test", new DateTime(), null);
		} catch (IllegalArgumentException e) {
		}

		try{
			new Task("test", null, new DateTime());
			fail();
		} catch (IllegalArgumentException e) {
		}

		try{
			new Task("test", new DateTime(), Task.INVALID_DATE_FIELD);
			fail();
		} catch (IllegalArgumentException e) {
		}
		
		try{
			new Task("test", Task.INVALID_DATE_FIELD, new DateTime());
			fail();
		} catch (IllegalArgumentException e) {
		}


		try{
			new Task("test", null, null);
			fail();
		} catch (IllegalArgumentException e) {
		}

		
		try{
			new Task("test", Task.INVALID_DATE_FIELD, Task.INVALID_DATE_FIELD);
			fail();
		} catch (IllegalArgumentException e) {
		}


		try{
			new Task("test", TIMED_DONE_FALSE_START.plusHours(1), TIMED_DONE_FALSE_START.plusHours(1).minusSeconds(1));
			fail();
		} catch (IllegalArgumentException e) {
		}


		try{
			new Task("test", new DateTime(1000), new DateTime(1000));
		} catch (IllegalArgumentException e) {
			fail();
		}

	}
	
	@Test
	public void testTaskStringDateTimeDateTimeBoolean() {
		try{
			new Task("test", TIMED_DONE_FALSE_START, TIMED_DONE_FALSE_START, true);
		} catch (AssertionError e) {
			fail();
		}


		try{
			new Task("test", TIMED_DONE_FALSE_START, TIMED_DONE_FALSE_START, false);
		} catch (AssertionError e) {
			fail();
		}
	
	}


	@Test
	public void testTaskTask() {		
		Task toBeTakenOver;
		
		toBeTakenOver = new Task(name);
		assertTrue(name.isEqualTo(toBeTakenOver));
		
		toBeTakenOver = new Task(nameTrue);
		assertTrue(nameTrue.isEqualTo(toBeTakenOver));
		
		toBeTakenOver = new Task(nameDeadline);
		assertTrue(nameDeadline.isEqualTo(toBeTakenOver));
		
		toBeTakenOver = new Task(nameDeadlineTrue);
		assertTrue(nameDeadlineTrue.isEqualTo(toBeTakenOver));
		
		toBeTakenOver = new Task(nameTimed);
		assertTrue(nameTimed.isEqualTo(toBeTakenOver));
		
		toBeTakenOver = new Task(nameTimedFalse);
		assertTrue(nameTimedFalse.isEqualTo(toBeTakenOver));
		


	}


	@Test
	public void testIsFloatingTask() {
		
		assertTrue(nameTrue.isFloatingTask());
		assertFalse(nameDeadlineTrue.isFloatingTask());
		assertFalse(nameTimedFalse.isFloatingTask());
	}

	@Test
	public void testIsTimedTask() {
		assertFalse(nameTrue.isTimedTask());
		assertFalse(nameDeadlineTrue.isTimedTask());
		assertTrue(nameTimedFalse.isTimedTask());
	}

	@Test
	public void testIsDeadlineTask() {
		assertFalse(nameTrue.isDeadlineTask());
		assertTrue(nameDeadlineTrue.isDeadlineTask());
		assertFalse(nameTimedFalse.isDeadlineTask());
	}

	@Test
	public void testGetSerial() {
		
		int start = name.getSerial();
		assertEquals(start + 0, name.getSerial());
		assertEquals(start + 1, nameTrue.getSerial());
		
		assertEquals(start + 2, nameDeadline.getSerial());
		assertEquals(start + 3, nameDeadlineTrue.getSerial());
		
		assertEquals(start + 4, nameTimed.getSerial());
		assertEquals(start + 5, nameTimedFalse.getSerial());
	}

	@Test
	public void testGetType() {

		assertEquals(Task.TaskType.FLOATING, nameTrue.getType());
		assertEquals(Task.TaskType.DEADLINE, nameDeadlineTrue.getType());
		assertEquals(Task.TaskType.TIMED, nameTimedFalse.getType());
	}

	@Test
	public void testGetTaskName() {
		assertEquals(NAME_TRUE, nameTrue.getTaskName());
		assertEquals(NAME_1MONTH_TRUE, nameDeadlineTrue.getTaskName());
		assertEquals(NAME_FROM_NOW_TO_TOMORROW_0000_FALSE, nameTimedFalse.getTaskName());
	}

	@Test
	public void testGetStartDate() {
		assertEquals(Task.INVALID_DATE_FIELD, name.getStartDate());
		assertEquals(Task.INVALID_DATE_FIELD, nameTrue.getStartDate());
		
		assertEquals(Task.INVALID_DATE_FIELD, nameDeadline.getStartDate());
		assertEquals(Task.INVALID_DATE_FIELD, nameDeadlineTrue.getStartDate());
		
		assertEquals(TIMED_START, nameTimed.getStartDate());
		assertEquals(TIMED_DONE_FALSE_START, nameTimedFalse.getStartDate());
	}

	@Test
	public void testGetEndDate() {
		assertEquals(Task.INVALID_DATE_FIELD, name.getEndDate());
		assertEquals(Task.INVALID_DATE_FIELD, nameTrue.getEndDate());
		
		assertEquals(Task.INVALID_DATE_FIELD, nameDeadline.getEndDate());
		assertEquals(Task.INVALID_DATE_FIELD, nameDeadlineTrue.getEndDate());
		
		assertEquals(TIMED_END, nameTimed.getEndDate());
		assertEquals(TIMED_DONE_FALSE_END, nameTimedFalse.getEndDate());
	}

	@Test
	public void testGetDeadline() {
		assertEquals(Task.INVALID_DATE_FIELD, name.getDeadline());
		assertEquals(Task.INVALID_DATE_FIELD, nameTrue.getDeadline());
		
		assertEquals(DEADLINE, nameDeadline.getDeadline());
		assertEquals(DEADLINE_TRUE, nameDeadlineTrue.getDeadline());
		
		assertEquals(Task.INVALID_DATE_FIELD, nameTimed.getDeadline());
		assertEquals(Task.INVALID_DATE_FIELD, nameTimedFalse.getDeadline());
	}
	
	@Test
	public void testChangeName() {
		try{
			name.changeName(null);
			fail();
		} catch (IllegalArgumentException e) {
		}

		
		try{
			name.changeName("");
			fail();
		} catch (IllegalArgumentException e) {
		}

		
		nameTimedFalse.changeName(NAME_ONLY);
		assertEquals(NAME_ONLY, nameTimedFalse.getTaskName());
		
	}


	@Test
	public void testChangetoFloating() {

		nameDeadline.changetoFloating();
		
		assertTrue(nameDeadline.isFloatingTask());

		
	}



	@Test
	public void testChangeStartAndEndTime() {
		try{
			nameTrue.changeStartAndEndDate(TIMED_DONE_FALSE_START, TIMED_DONE_FALSE_END);
			fail();
		} catch (IllegalArgumentException e) {
		}

		
		try{
			nameDeadlineTrue.changeStartAndEndDate(TIMED_DONE_FALSE_START, TIMED_DONE_FALSE_END);
			fail();
		} catch (IllegalArgumentException e) {
		}

		
		try{
			nameTimed.changeStartAndEndDate( TIMED_DONE_FALSE_END, TIMED_DONE_FALSE_START);
			fail();
		} catch (IllegalArgumentException e) {
		}

		
		try{
			nameTimed.changeStartAndEndDate(TIMED_DONE_FALSE_START, Task.INVALID_DATE_FIELD);
			fail();
		} catch (IllegalArgumentException e) {
		}

		
		try{
			nameTimed.changeStartAndEndDate(Task.INVALID_DATE_FIELD, Task.INVALID_DATE_FIELD);
			fail();
		} catch (IllegalArgumentException e) {
		}
		
		
		
		try{
			nameTimed.changeStartAndEndDate( TIMED_DONE_FALSE_END, TIMED_DONE_FALSE_END);
		} catch (IllegalArgumentException e) {
			fail();
		}
		
		
		
		nameTimed.changeStartAndEndDate(TIMED_DONE_FALSE_START, TIMED_DONE_FALSE_END);
		assertEquals(TIMED_DONE_FALSE_START, nameTimed.getStartDate());
		assertEquals(TIMED_DONE_FALSE_END, nameTimed.getEndDate());
		
	}
	@Test
	public void testchangetoDeadline() {
		nameDeadline.changetoDeadline(DEADLINE_TRUE);
		assertEquals(DEADLINE_TRUE, nameDeadline.getDeadline());
		
		nameTrue.changetoDeadline(DEADLINE_TRUE);
		assertEquals(DEADLINE_TRUE, nameTrue.getDeadline());
		
		nameTimed.changetoDeadline(DEADLINE_TRUE);
		assertEquals(DEADLINE_TRUE, nameTimed.getDeadline());
	}
	
	@Test
	public void testChangeStartDateAndEndDate() {
		try{
			nameTimed.changeStartAndEndDate(null, TIMED_DONE_FALSE_END);
			fail();
		} catch (IllegalArgumentException e) {
		}
		
		try{
			nameTimed.changeStartAndEndDate(TIMED_DONE_FALSE_END, null);
			fail();
		} catch (IllegalArgumentException e) {
		}	
	}

	@Test
	public void testChangeDeadline() {
		try{
			nameTrue.changeDeadline(DEADLINE);
			fail();
		} catch (IllegalArgumentException e) {
		}
		
		try{
			nameTimedFalse.changeDeadline(DEADLINE);
			fail();
		} catch (IllegalArgumentException e) {
		}
		
		try{
			nameDeadline.changeDeadline(null);
			fail();
		} catch (IllegalArgumentException e) {
		}
		
		try{
			nameDeadline.changeDeadline(Task.INVALID_DATE_FIELD);
			fail();
		} catch (IllegalArgumentException e) {
		}
		
		nameDeadline.changeDeadline(DEADLINE_TRUE);
		assertEquals(DEADLINE_TRUE, nameDeadline.getDeadline());
		
		
	}

	@Test
	public void testCompareTo() {
		try{
			name.compareTo(null);
			fail();
		} catch (IllegalArgumentException e) {
		}
		
		
		
		assertEquals(COMPARETO_EQUAL, name.compareTo(nameTrue));
		
		assertEquals(COMPARETO_BIGGER, nameTrue.compareTo(nameDeadline));
		
		assertEquals(COMPARETO_SMALLER, nameTimedFalse.compareTo(name));
		
		assertEquals(nameDeadlineTrue.getDeadline().compareTo(nameDeadline.getDeadline()), nameDeadlineTrue.compareTo(nameDeadline));
		
		assertEquals(nameTimed.getStartDate().compareTo(nameDeadlineTrue.getDeadline()), nameTimed.compareTo(nameDeadlineTrue));
		
		assertEquals(nameDeadlineTrue.getDeadline().compareTo(nameTimed.getEndDate()), nameDeadlineTrue.compareTo(nameTimed));
		
	}

	@Test
	public void testSearchName() {
		try{
			name.containsTerm(null);
			fail();
		} catch (IllegalArgumentException e) {
		}

		
		try{
			name.containsTerm("");
			fail();
		} catch (IllegalArgumentException e) {
		}
		
		assertTrue(nameDeadline.containsTerm("name"));
		assertFalse(nameDeadline.containsTerm("rubbish"));
	}

	@Test
	public void testIsWithinDateRange() {
		try{
			nameTimed.isWithinDateRange(null, null);
			fail();
		} catch (IllegalArgumentException e) {
		}


		try{
			nameTimedFalse.isWithinDateRange(Task.INVALID_DATE_FIELD, null);
		} catch (IllegalArgumentException e) {
		}

		try{
			nameTimed.isWithinDateRange(null, Task.INVALID_DATE_FIELD);
			fail();
		} catch (IllegalArgumentException e) {
		}

		try{
			nameTimedFalse.isWithinDateRange(Task.INVALID_DATE_FIELD, Task.INVALID_DATE_FIELD);
			fail();
		} catch (IllegalArgumentException e) {
		}
		
		try{
			nameTimed.isWithinDateRange(TIMED_START, null);
			fail();
		} catch (IllegalArgumentException e) {
		}
		
		try{
			nameTimed.isWithinDateRange(null, TIMED_END);
			fail();
		} catch (IllegalArgumentException e) {
		}
		
		try{
			nameTimed.isWithinDateRange(TIMED_START, Task.INVALID_DATE_FIELD);
			fail();
		} catch (IllegalArgumentException e) {
		}
		
		try{
			nameTimed.isWithinDateRange(TIMED_END, TIMED_START);
			fail();
		} catch (IllegalArgumentException e) {
		}

		
		
		assertFalse(nameTrue.isWithinDateRange(TIMED_START, TIMED_END));
		assertTrue(nameDeadline.isWithinDateRange(DEADLINE, DEADLINE.plus(1)));
		assertTrue(nameDeadlineTrue.isWithinDateRange(DEADLINE_TRUE.minus(1), DEADLINE_TRUE));
		
		assertTrue(nameTimed.isWithinDateRange(TIMED_START.minus(1), TIMED_END.plus(1)));
		assertFalse(nameTimed.isWithinDateRange(TIMED_START.minus(100), TIMED_START.minus(90)));
		assertFalse(nameTimedFalse.isWithinDateRange(TIMED_DONE_FALSE_START.plus(1), TIMED_DONE_FALSE_END.minus(1)));
	
	}
	
	@Test
	public void testBecomeThis(){
		try{
			name.becomeThis(null);
			fail();
		} catch (IllegalArgumentException e){
		}
		
		
		nameTrue.becomeThis(nameTimedFalse);
		
		assertTrue(nameTrue.isEqualTo(nameTimedFalse));
	}
	
	@Test
	public void testAmIEqualToThis(){
		assertTrue(nameTimedFalse.isEqualTo(nameTimedFalse));
		
		Task newName = new Task(name);
		
		newName.changeName(name.getTaskName() + "extra");
		assertFalse("Different name", newName.isEqualTo(name));
		
		newName = new Task(name);
		
		newName.changetoDeadline(new DateTime());
		assertFalse("Different type", newName.isEqualTo(name));
		
		Task newStart = new Task(nameTimed);
		newStart.changeStartAndEndDate(newStart.getStartDate().plus(1), newStart.getEndDate());
		assertFalse("Different start", newStart.isEqualTo(nameTimed));
		
		Task newEnd = new Task(nameTimedFalse);
		newEnd.changeStartAndEndDate(newEnd.getStartDate(), newEnd.getEndDate().plus(1));
		assertFalse("Different End", newEnd.isEqualTo(nameTimedFalse));
		
		Task newDeadline = new Task(nameDeadline);
		newDeadline.changeDeadline(newDeadline.getDeadline().plus(1));
		assertFalse("Different deadline", newDeadline.isEqualTo(nameDeadline));
		
		Task newNameTrue = new Task(nameTimedFalse);
		newNameTrue.done(true);
		assertFalse("Different type", newNameTrue.isEqualTo(nameTimedFalse));
		
		Task newNameTask = new Task(name.getTaskName());
		assertFalse("Different Serial", newNameTask.isEqualTo(name));
		
		
	}
	
	

	
	@Test
	public void testClashesWithRange(){

		try{
			nameDeadlineTrue.clashesWithRange(null, TIMED_DONE_FALSE_END);
			fail();
		} catch (IllegalArgumentException e) {
		}
		
		try{
			nameDeadlineTrue.clashesWithRange(TIMED_DONE_FALSE_END, null);
			fail();
		} catch (IllegalArgumentException e) {
		}
		
		try{
			nameDeadlineTrue.clashesWithRange(Task.INVALID_DATE_FIELD, TIMED_DONE_FALSE_END);
			fail();
		} catch (IllegalArgumentException e) {
		}
		
		try{
			nameDeadlineTrue.clashesWithRange(TIMED_DONE_FALSE_END, Task.INVALID_DATE_FIELD);
			fail();
		} catch (IllegalArgumentException e) {
		}	
		
		try{
			nameDeadlineTrue.clashesWithRange(TIMED_DONE_FALSE_END, TIMED_DONE_FALSE_START);
			fail();
		} catch (IllegalArgumentException e) {
		}	
		
		
		DateTime deadlineLow = DEADLINE.minus(1);
		DateTime deadline = DEADLINE;
		DateTime deadlineHigh = DEADLINE.plus(1);
	
		//Floating task will never clash
		assertFalse(name.clashesWithRange(deadlineLow, deadlineHigh));
		
		//Test Deadline Task
		assertTrue(nameDeadline.clashesWithRange(deadlineLow, deadlineHigh));
		assertTrue(nameDeadline.clashesWithRange(deadlineLow, deadline));
		assertTrue(nameDeadline.clashesWithRange(deadline, deadlineHigh));
		
		assertFalse(nameDeadline.clashesWithRange(deadlineHigh, deadlineHigh.plus(1)));
		assertFalse(nameDeadline.clashesWithRange(deadlineLow.minus(1), deadlineLow));
		
		//Test Timed Task
		DateTime timedStartLow = TIMED_START.minus(1);
		DateTime timedStart = TIMED_START;
		DateTime timedStartHigh = TIMED_START.plus(1);
		
		DateTime timedEndLow = TIMED_END.minus(1);
		DateTime timedEnd = TIMED_END;
		DateTime timedEndHigh = TIMED_END.plus(1);
		
		
		//Test equality
		assertTrue(nameTimed.clashesWithRange(timedStart, timedEndHigh));
		assertTrue(nameTimed.clashesWithRange(timedStartLow, timedEnd));
		assertTrue(nameTimed.clashesWithRange(timedEnd, timedEndHigh));
		assertTrue(nameTimed.clashesWithRange(timedStartLow, timedStart));
		
		assertTrue(nameTimed.clashesWithRange(timedStartHigh, timedEndLow));
		assertFalse(nameTimed.clashesWithRange(timedStartLow.minus(1), timedStartLow));
		assertFalse(nameTimed.clashesWithRange(timedEndHigh, timedEndHigh.plus(1)));
	
		assertTrue(nameTimed.clashesWithRange(timedStartLow, timedEndHigh));
		
		
	}
	
	@Test
	public void testShowInfo(){
		assertEquals(name.showInfo(), name.showInfo());
		assertEquals(nameTrue.showInfo(), nameTrue.showInfo());
		
		assertEquals(nameDeadline.showInfo(), nameDeadline.showInfo());
		assertEquals(nameDeadlineTrue.showInfo(), nameDeadlineTrue.showInfo());
		
		assertEquals(nameTimed.showInfo(), nameTimed.showInfo());
		assertEquals(nameTimedFalse.showInfo(), nameTimedFalse.showInfo());
		
	}
	
	@Test
	public void testComparatorType() {
		Collections.sort(listing, new Task.SortByType());

		boolean fail = false;
		
		//In order of deadline, timed and floating
		TaskType previous = TaskType.DEADLINE;
		for(Task comp : listing) {
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
	public void testComparatorDone() {
		Collections.sort(listing, new Task.SortByDone());

		boolean fail = false;
		
		//Undone tasks come first
		boolean previous = false;
		for(Task comp : listing) {
			if(previous && (comp.isDone() == false)) {
				fail = true;
			}
			
			previous = comp.isDone();
		}
		assertFalse(fail);

	}
	
	@Test
	public void testComparatorStart() {
		Collections.sort(listing, new Task.SortByStartDate());

		boolean fail = false;
		
		
		DateTime previous = new DateTime(Long.MIN_VALUE);
		for(Task comp : listing) {
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
	public void testComparatorEnd() {
		Collections.sort(listing, new Task.SortByEndDate());

		boolean fail = false;
		
		
		DateTime previous = new DateTime(Long.MIN_VALUE);
		for(Task comp : listing) {
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
	public void testComparatorName() {
		Collections.sort(listing, new Task.SortByName());

		boolean fail = false;
		
		//Undone tasks come first
		String previous = " ";
		for(Task comp : listing) {
			String compName = comp.getTaskName();
			if(previous.compareTo(compName) == COMPARETO_BIGGER) {
				fail = true;
			}
			
			previous = compName;
		}
		assertFalse(fail);

	}


}
