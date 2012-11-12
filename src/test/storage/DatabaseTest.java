//@author A0081007U
package test.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import main.shared.SearchTerms;
import main.shared.Task;
import main.storage.Database;
import main.storage.WillNotWriteToCorruptFileException;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DatabaseTest {



	private static final String NAME_ONLY = "name only super long super long super long super long super long super long super long super long super long super long super long super long super long super long";
	private static final String NAME_TRUE = "name true";
	private static final String NAME_FALSE = "name false";
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


	Task name;
	Task nameTrue;
	Task nameFalse;

	Task nameDeadline;
	Task nameDeadlineTrue;

	Task nameTimed;
	Task nameTimedFalse;


	List<Task> filledListing;
	List<Task> initialClearListing;

	Database db = Database.getInstance();

	@Before
	public void runBeforeEveryTest() {
		assertEquals(Database.DB_File_Status.FILE_ALL_OK, db.getFileAttributes());

		name = new Task(NAME_ONLY);
		nameTrue = new Task(NAME_TRUE, true);
		nameFalse = new Task(NAME_FALSE, false);

		nameDeadline = new Task(NAME_1MONTH, DEADLINE);
		nameDeadlineTrue = new Task(NAME_1MONTH_TRUE, DEADLINE_TRUE, true);

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



		Collections.sort(filledListing);

		try {
			//Write Empty database
			db.setAll(new ArrayList<Task>());
			List<Task> readList = db.getAll();
			assertEquals(0, readList.size());

		} catch (IOException | WillNotWriteToCorruptFileException e) {
			fail();
		}
	}

	@After
	public void clearFileAfterEveryTest(){
		try {
			db.setAll(new ArrayList<Task>());
		} catch (IOException | WillNotWriteToCorruptFileException e) {
			fail();
		}
	}

	@Test
	public void getAndWriteFunctionalityTest() {


		try {
			//Write filled database
			db.setAll(filledListing);
			initialClearListing = db.getAll();

			//Check the size read back is equal
			assertEquals(filledListing.size(), initialClearListing.size());

			Collections.sort(initialClearListing);

			for(int i = 0; i < filledListing.size(); i++){
				assertEquals(filledListing.get(i).showInfo(), initialClearListing.get(i).showInfo());

			}

		} catch (IOException | WillNotWriteToCorruptFileException e) {
			fail();
		}		




	}


	public void testWriteAll(){
		try {
			db.setAll(null);
		} catch (IOException | WillNotWriteToCorruptFileException e) {
			fail();
		} catch (AssertionError e){
		}
	}


	@Test
	public void testAdd() {

		try {

			db.setAll(new ArrayList<Task>()); //Clear the file
			db.add(nameTimedFalse);
			initialClearListing = db.getAll();

			for(Task entry : initialClearListing){
				assertEquals(nameTimedFalse.showInfo(), entry.showInfo());
			}

			db.setAll(new ArrayList<Task>());
			db.add(name);
			db.add(nameDeadline);
			db.add(nameTimedFalse);

			db.add(nameTrue);
			db.add(nameDeadlineTrue);

			db.add(nameTimed);
			db.add(nameFalse);

			initialClearListing = db.getAll();

			assertEquals(filledListing.size(), initialClearListing.size());

			Collections.sort(initialClearListing);

			for (int i = 0; i < initialClearListing.size(); i++) {
				assertEquals(filledListing.get(i).showInfo(), initialClearListing.get(i).showInfo());
			}


		} catch (IOException | WillNotWriteToCorruptFileException e) {
			fail();
		}



	}

	@Test
	public void testLocateATask() {

		try {
			//Nothing to get
			db.setAll(initialClearListing);
			db.locateATask(Task.SERIAL_NUMBER_START);
			fail();
		} catch (IOException | WillNotWriteToCorruptFileException e1) {
			fail();
		} catch(NoSuchElementException e){
		}



		try {
			db.setAll(filledListing);
			assertEquals("Get top of list",nameDeadlineTrue.showInfo(), db.locateATask(nameDeadlineTrue.getSerial()).showInfo());
			assertEquals("Get middle of list", nameDeadline.showInfo(), db.locateATask(nameDeadline.getSerial()).showInfo());
			assertEquals("Get bottom of list", nameFalse.showInfo(), db.locateATask(nameFalse.getSerial()).showInfo());
		} catch (IOException | WillNotWriteToCorruptFileException | NoSuchElementException e) {
			fail();
		}

		//Wrong index
		try{
			db.locateATask(-1);
			fail();
		} catch (NoSuchElementException e) {
		}

		int maxSerial = Task.SERIAL_NUMBER_START - 1;

		for(Task entry : filledListing){
			if(entry.getSerial() > maxSerial){
				maxSerial = entry.getSerial();
			}
		}

		maxSerial++;


		try{
			db.locateATask(maxSerial);
			fail();
		} catch (NoSuchElementException e) {
		}

	}

	@Test
	public void testUpdate() {

		//Since the act of updating will changed the state of the Task object, try to work on different Task objects

		try {
			//Wrong Serial number
			db.update(Task.SERIAL_NUMBER_START - 1, name);
			fail();
		} catch (NoSuchElementException e) {
		} catch (IOException | WillNotWriteToCorruptFileException e) {
			fail();
		}

		try {
			db.setAll(new ArrayList<Task>());
			//Add only one task and update it
			db.add(name);
			db.update(name.getSerial(), nameTimed);
			initialClearListing = db.getAll();

			assertEquals(nameTimed.showInfo(), initialClearListing.get(0).showInfo());

		} catch (IOException | WillNotWriteToCorruptFileException e) {
			fail();
		}

		try {
			db.setAll(new ArrayList<Task>());
			//Add 2 tasks and update 1
			db.add(nameTimed);
			db.add(nameTrue);

			db.update(nameTrue.getSerial(), nameDeadline);
			initialClearListing = db.getAll();

			Collections.sort(initialClearListing);

			assertEquals(nameDeadline.showInfo(), initialClearListing.get(1).showInfo());

		} catch (IOException | WillNotWriteToCorruptFileException e) {
			fail();
		}




	}

	@Test
	public void testDelete() {
		try {
			db.delete(Task.SERIAL_NUMBER_START - 1);
			fail();
		} catch (IOException | WillNotWriteToCorruptFileException e) {
			fail();
		} catch (NoSuchElementException e) {
		}

		int maxSerial = Task.SERIAL_NUMBER_START - 1;

		for(Task entry : filledListing){
			if(entry.getSerial() > maxSerial){
				maxSerial = entry.getSerial();
			}
		}

		maxSerial++;

		try {
			db.setAll(filledListing);
			db.delete(maxSerial);
			fail();
		} catch (IOException | WillNotWriteToCorruptFileException e) {
			fail();
		} catch (NoSuchElementException e) {
		} 

		try {
			db.setAll(filledListing);
			db.delete(nameDeadlineTrue.getSerial());

			initialClearListing = db.getAll();

			//Ensure Task is deleted
			for(Task entry : initialClearListing){
				if(entry.getSerial() == nameDeadlineTrue.getSerial()){
					fail();
				}
			}

		} catch (IOException | WillNotWriteToCorruptFileException e) {
			fail();
		} catch (NoSuchElementException e) {
			fail();
		}

		try{	
			db.delete(nameFalse.getSerial());

			initialClearListing = db.getAll();

			//Ensure Task is deleted
			for(Task entry : initialClearListing){
				if(entry.getSerial() == nameFalse.getSerial()){
					fail();
				}
			}

		} catch (IOException | WillNotWriteToCorruptFileException e) {
			fail();
		} catch (NoSuchElementException e) {
			fail();
		}



	}

	@Test
	public void testDeleteAll() {
		try {
			//Test write empty file
			db.setAll(new ArrayList<Task>());
			db.deleteAll();
			initialClearListing = db.getAll();
			assertEquals(0, initialClearListing.size());

			//Delete 1 task
			db.add(nameTimedFalse);
			db.deleteAll();
			initialClearListing = db.getAll();
			assertEquals(0, initialClearListing.size());

			//Delete many tasks task
			db.setAll(filledListing);
			db.deleteAll();
			initialClearListing = db.getAll();
			assertEquals(0, initialClearListing.size());			


		} catch (IOException | WillNotWriteToCorruptFileException e) {
			fail();
		}

	}

	@Test
	public void testSearch() {
		try {
			db.setAll(filledListing);
		} catch (IOException | WillNotWriteToCorruptFileException e) {
			fail();
		}
		
		List<Task> results;
		SearchTerms terms;
		
		//No keywords
		terms = new SearchTerms(new String[0]);
		results = db.search(terms);
		assertEquals(filledListing.size(), results.size());
		
		
		//No task will meet keywords
		terms = new SearchTerms(new String[]{"No task will have this"});
		results = db.search(terms);
		assertEquals(0, results.size());
		
		//Those containing the keyword false and name
		terms = new SearchTerms(new String[]{"false",  "name"});
		results = db.search(terms);
		assertEquals(2, results.size());
		for(Task resultEntry : results){
			if(!resultEntry.containsTerm("false")){
				fail();
			}
			if(!resultEntry.containsTerm("name")){
				fail();
			}
			
		}
		
		//Fall within this range
		terms = new SearchTerms(new DateTime().minusMonths(2), new DateTime().plusMonths(2));
		results = db.search(terms);
		assertEquals(4, results.size());
		for(Task resultEntry : results){
			if(!resultEntry.clashesWithRange(new DateTime().minusMonths(2), new DateTime().plusMonths(2))){
				fail();
			}
	
		}
		
		
		//Keywords and Date Range
		terms = new SearchTerms(new String[]{"month"}, new DateTime().minusMonths(2), new DateTime().plusMonths(2));
		results = db.search(terms);
		assertEquals(2, results.size());
		for(Task resultEntry : results){
			if(!resultEntry.clashesWithRange(new DateTime().minusMonths(2), new DateTime().plusMonths(2))){
				fail();
			}
			
			if(!resultEntry.containsTerm("month")){
				fail();
			}
	
		}
		
		//No flag
		terms = new SearchTerms(false, false, false, false, false);
		results = db.search(terms);
		assertEquals(filledListing.size(), results.size());
		
		//Complete flag
		terms = new SearchTerms(true, false, false, false, false);
		results = db.search(terms);
		assertEquals(2, results.size());
		for(Task resultEntry : results){
			if(!resultEntry.isDone()){
				fail();
			}
		}
		
		//InComplete flag
		terms = new SearchTerms(false, true, false, false, false);
		results = db.search(terms);
		assertEquals(5, results.size());
		for(Task resultEntry : results){
			if(resultEntry.isDone()){
				fail();
			}
		}

		//Timed flag
		terms = new SearchTerms(false, false, true, false, false);
		results = db.search(terms);
		assertEquals(2, results.size());
		for(Task resultEntry : results){
			if(!resultEntry.isTimedTask()){
				fail();
			}
		}
		
		//Deadline flag
		terms = new SearchTerms(false, false, false, true, false);
		results = db.search(terms);
		assertEquals(2, results.size());
		for(Task resultEntry : results){
			if(!resultEntry.isDeadlineTask()){
				fail();
			}
		}
		
		//Floating flag
		terms = new SearchTerms(false, false, false, false, true);
		results = db.search(terms);
		assertEquals(3, results.size());
		for(Task resultEntry : results){
			if(!resultEntry.isFloatingTask()){
				fail();
			}
		}
		
		
		//For Searchterm class flags and keywords constructor
		//InComplete flag
		terms = new SearchTerms(false, true, false, false, false, new String[]{"tomorrow"});
		results = db.search(terms);
		assertEquals(2, results.size());
		for(Task resultEntry : results){
			if(resultEntry.isDone()){
				fail();
			}
			if(!resultEntry.containsTerm("tomorrow")){
				fail();
			}
			
		}
		
		
		//For Searchterm class flags and date range constructor
		//InComplete flag
		terms = new SearchTerms(false, true, false, false, false, new DateTime().minusDays(2), new DateTime().plusDays(2));
		results = db.search(terms);
		assertEquals(2, results.size());
		for(Task resultEntry : results){
			if(resultEntry.isDone()){
				fail();
			}
			if(!resultEntry.clashesWithRange(new DateTime().minusDays(2), new DateTime().plusDays(2))){
				fail();
			}
			

			
		}
		
		//For Searchterm class flags with keywords and date ranges constructor
		//InComplete flag
		terms = new SearchTerms(false, true, false, false, false, new String[]{"false"}, new DateTime().minusDays(2), new DateTime().plusDays(2));
		results = db.search(terms);
		assertEquals(1, results.size());
		for(Task resultEntry : results){
			if(resultEntry.isDone()){
				fail();
			}
			if(!resultEntry.clashesWithRange(new DateTime().minusDays(2), new DateTime().plusDays(2))){
				fail();
			}
			
			if(!resultEntry.containsTerm("false")){
				fail();
			}
			
		}
		
		
		
	}

	@Test
	public void testDeleteMultiple() {
		List<Integer> deletionList = new LinkedList<Integer>();
		List<Task> obtainedList;

		//Empty serial list
		obtainedList = deleteHelper(new LinkedList<Integer>(), filledListing);
		assertEquals("No change in size", filledListing.size(), obtainedList.size());
		
		//No Task List
		obtainedList = deleteHelper(new LinkedList<Integer>(), new LinkedList<Task>());
		assertEquals("No change in size", 0, obtainedList.size());

		//Delete a task from empty database
		int serial1 = filledListing.get(0).getSerial();
		deletionList.add(serial1);
		
		try{
			obtainedList = deleteHelper(deletionList, new LinkedList<Task>());
			fail();
		} catch(NoSuchElementException e){
		}


		//Delete 1 valid task

		obtainedList = deleteHelper(deletionList, filledListing);
		assertEquals("Size dropped by one", filledListing.size() - 1, obtainedList.size());
		for(Task entry : obtainedList){
			if(entry.getSerial() == serial1){
				fail();
			}
		}
		
		//Delete 2 valid Tasks
		int serial2 = filledListing.get(0).getSerial();
		int serial3 = filledListing.get(filledListing.size() - 1).getSerial();
		deletionList.clear();
		deletionList.add(serial2);
		deletionList.add(serial3);
		
		obtainedList = deleteHelper(deletionList, filledListing);
		
		assertEquals("Size dropped by 2", filledListing.size() - 2, obtainedList.size());
		for(Task entry : obtainedList){
			if(entry.getSerial() == serial2){
				fail();
			}
			
			if(entry.getSerial() == serial3){
				fail();
			}
		}
		
		//Delete 1 invalid Tasks
		int serial4 = Task.SERIAL_NUMBER_START - 1;
		deletionList.clear();
		deletionList.add(serial4);
		
		try{
			deleteHelper(deletionList, filledListing);
			fail();
		} catch(NoSuchElementException e){
		}
		
		obtainedList = db.getAll();
		
		assertEquals("Size no change", filledListing.size(), obtainedList.size());
		
		

		//Delete 1 valid followed by 1 invalid task
		int serial5 = filledListing.get(1).getSerial();
		int serial6 = Task.SERIAL_NUMBER_START - 100;
		deletionList.clear();
		deletionList.add(serial5);
		deletionList.add(serial6);

		try{
			deleteHelper(deletionList, filledListing);
			fail();
		} catch(NoSuchElementException e){
		}
		
		obtainedList = db.getAll();
		
		assertEquals("Size no change", filledListing.size(), obtainedList.size());
		
		
		//Delete 1 invalid followed by 1 valid task
		int serial7 = Task.SERIAL_NUMBER_START - 100;
		int serial8 = filledListing.get(filledListing.size() -1).getSerial();
		deletionList.clear();
		deletionList.add(serial7);
		deletionList.add(serial8);

		try{
			deleteHelper(deletionList, filledListing);
			fail();
		} catch(NoSuchElementException e){
		}
		
		obtainedList = db.getAll();
		
		assertEquals("Size no change", filledListing.size(), obtainedList.size());
	
		//Delete everything
		deletionList.clear();
		for(Task current : filledListing){
			deletionList.add(current.getSerial());
		}
		
		deleteHelper(deletionList, filledListing);
		obtainedList = db.getAll();
		
		assertEquals("Nothing left", 0, obtainedList.size());
	

	}


	private List<Task> deleteHelper(List<Integer> serials, List<Task> filledListing){
		try {
			db.setAll(filledListing);
			db.delete(serials);
			return db.getAll();
		} catch (IOException | WillNotWriteToCorruptFileException e) {
			fail();
			return null;
		}

	}





}
