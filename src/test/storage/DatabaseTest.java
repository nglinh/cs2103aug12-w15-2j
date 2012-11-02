package test.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

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
		filledListing.add(nameTrue);
		filledListing.add(nameFalse);

		Collections.sort(filledListing);

		try {
			//Write Empty database
			db.writeALL(new ArrayList<Task>());
			List<Task> readList = db.readAll();
			assertEquals(0, readList.size());

		} catch (IOException | WillNotWriteToCorruptFileException e) {
			fail();
		}
	}

	@After
	public void clearFileAfterEveryTest(){
		try {
			db.writeALL(new ArrayList<Task>());
		} catch (IOException | WillNotWriteToCorruptFileException e) {
			fail();
		}
	}

	@Test
	public void getAndWriteFunctionalityTest() {


		try {
			//Write filled database
			db.writeALL(filledListing);
			initialClearListing = db.readAll();

			//Check the size read back is equal
			assertEquals(filledListing.size(), initialClearListing.size());

			for(int i = 0; i < filledListing.size(); i++){
				assertEquals(filledListing.get(i).showInfo(), initialClearListing.get(i).showInfo());
			}

		} catch (IOException | WillNotWriteToCorruptFileException e) {
			fail();
		}		




	}


	public void testWriteAll(){
		try {
			db.writeALL(null);
		} catch (IOException | WillNotWriteToCorruptFileException e) {
			fail();
		} catch (AssertionError e){
		}
	}


	@Test
	public void testAdd() {

		try {
			db.add(null);
			fail();
		} catch (IOException | WillNotWriteToCorruptFileException e) {
			fail();
		} catch (AssertionError e){
		}

		try {

			db.writeALL(new ArrayList<Task>()); //Clear the file
			db.add(nameTimedFalse);
			initialClearListing = db.readAll();

			for(Task entry : initialClearListing){
				assertEquals(nameTimedFalse.showInfo(), entry.showInfo());
			}

			db.writeALL(new ArrayList<Task>());
			db.add(name);
			db.add(nameDeadline);
			db.add(nameTimedFalse);

			db.add(nameTrue);
			db.add(nameDeadlineTrue);

			db.add(nameTimed);
			db.add(nameFalse);

			initialClearListing = db.readAll();

			assertEquals(filledListing.size(), initialClearListing.size());

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
			db.writeALL(initialClearListing);
			db.locateATask(Task.SERIAL_NUMBER_START);
			fail();
		} catch (IOException | WillNotWriteToCorruptFileException e1) {
			fail();
		} catch(NoSuchElementException e){
		}



		try {
			db.writeALL(filledListing);
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
			db.update(0, null);
			fail();
		} catch (NoSuchElementException | IOException| WillNotWriteToCorruptFileException e) {
			fail();
		} catch(AssertionError e){
		}
		
		try {
			//Wrong Serial number
			db.update(Task.SERIAL_NUMBER_START - 1, name);
			fail();
		} catch (NoSuchElementException e) {
		} catch (IOException | WillNotWriteToCorruptFileException e) {
			fail();
		}
		
		try {
			db.writeALL(new ArrayList<Task>());
			//Add only one task and update it
			db.add(name);
			db.update(name.getSerial(), nameTimed);
			initialClearListing = db.readAll();
			
			assertEquals(nameTimed.showInfo(), initialClearListing.get(0).showInfo());
			
		} catch (IOException | WillNotWriteToCorruptFileException e) {
			fail();
		}
		
		try {
			db.writeALL(new ArrayList<Task>());
			//Add 2 tasks and update 1
			db.add(nameTimed);
			db.add(nameTrue);
			
			db.update(nameTrue.getSerial(), nameDeadline);
			initialClearListing = db.readAll();
			
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
			db.writeALL(filledListing);
			db.delete(maxSerial);
			fail();
		} catch (IOException | WillNotWriteToCorruptFileException e) {
			fail();
		} catch (NoSuchElementException e) {
		} 
		
		try {
			db.writeALL(filledListing);
			db.delete(nameDeadlineTrue.getSerial());
			
			initialClearListing = db.readAll();
			
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
			
			initialClearListing = db.readAll();
			
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
			db.writeALL(new ArrayList<Task>());
			db.deleteAll();
			initialClearListing = db.readAll();
			assertEquals(0, initialClearListing.size());

			//Delete 1 task
			db.add(nameTimedFalse);
			db.deleteAll();
			initialClearListing = db.readAll();
			assertEquals(0, initialClearListing.size());

			//Delete wany tasks task
			db.writeALL(filledListing);
			db.deleteAll();
			initialClearListing = db.readAll();
			assertEquals(0, initialClearListing.size());			


		} catch (IOException | WillNotWriteToCorruptFileException e) {
			fail();
		}

	}




}
