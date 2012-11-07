//@author A0081007U
package main.storage;

/**  
 * Database.java 
 * A class for managing all queries to the database and disk
 * @author  Yeo Kheng Meng
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import org.joda.time.DateTime;

import main.LogHandler;
import main.storage.FileManagement.FileStatus;
import main.shared.SearchTerms;
import main.shared.Task;

public class Database {

	public static enum DB_File_Status {
		FILE_ALL_OK, FILE_READ_ONLY, FILE_PERMISSIONS_UNKNOWN, FILE_IS_CORRUPT, FILE_IS_LOCKED
	};

	private List<Task> taskStore = new ArrayList<Task>();

	private FileManagement diskFile;
	private DB_File_Status fileAttributes;

	private Logger log = LogHandler.getLogInstance();

	private static Database theOne = null;

	public static Database getInstance() {
		if (theOne == null) {
			theOne = new Database();
		}

		return theOne;
	}

	/**
	 * To instantiate a database Also instantiates the fileManagement class
	 * 
	 */

	private Database() {
		log.info("Database instance created, now starting FileMgmt");

		diskFile = FileManagement.getInstance();
		diskFile.prepareDatabaseFile();
		diskFile.readFileAndDetectCorruption(taskStore);
		fileAttributes = parseFileAttributes(diskFile);

		log.info("FileMgmt started");
	}

	/**
	 * To give the results based on a search term.
	 * <p>
	 * Returned result is a clone of the tasks in database, operations done on
	 * the result will not affect the database
	 * 
	 * @param terms
	 *            Input in the form of a search term class
	 * @return an List<Task> containing all the matched tasks
	 */
	public List<Task> search(SearchTerms terms) {
		List<Task> searchResults = new ArrayList<Task>();

		for (Task currentEntry : taskStore) {
			if (taskMeetsSearchTerms(currentEntry, terms)) {
				searchResults.add(new Task(currentEntry));
			}
		}

		return searchResults;
	}

	private boolean taskMeetsSearchTerms(Task currentEntry, SearchTerms terms) {

		boolean taskDone = false;
		boolean taskUndone = false;

		boolean taskFloating = false;
		boolean taskDeadline = false;
		boolean taskTimed = false;

		boolean keywordMatched = false;
		boolean dateRangeMatched = false;

		if (terms.completeFlag() && currentEntry.isDone()) {
			taskDone = true;
		} else if (terms.completeFlag() == false) {
			taskDone = true;
		}

		if (terms.incompleteFlag() && !currentEntry.isDone()) {
			taskUndone = true;
		} else if (terms.incompleteFlag() == false) {
			taskUndone = true;
		}

		if (terms.floatingFlag() && currentEntry.isFloatingTask()) {
			taskFloating = true;
		} else if (terms.floatingFlag() == false) {
			taskFloating = true;
		}

		if (terms.deadlineFlag() && currentEntry.isDeadlineTask()) {
			taskDeadline = true;
		} else if (terms.deadlineFlag() == false) {
			taskDeadline = true;
		}

		if (terms.timedFlag() && currentEntry.isTimedTask()) {
			taskTimed = true;
		} else if (terms.timedFlag() == false) {
			taskTimed = true;
		}

		if ((terms.doesSearchContainKeywords() == false)
				|| keywordMatching(currentEntry, terms)) {
			keywordMatched = true;
		}

		if (terms.doesSearchContainDateRange()) {
			dateRangeMatched = dateMatching(currentEntry, terms);
		} else {
			dateRangeMatched = true;
		}

		boolean areAllConditionsSatisfied = taskDone && taskUndone
				&& taskFloating && taskDeadline && taskTimed && keywordMatched
				&& dateRangeMatched;

		return areAllConditionsSatisfied;
	}

	private boolean dateMatching(Task currentEntry, SearchTerms terms) {
		DateTime startRange = terms.getStartDate();
		DateTime endRange = terms.getEndDate();

		return currentEntry.clashesWithRange(startRange, endRange);

	}

	// Check if task contains all the keywords specified
	private boolean keywordMatching(Task currentEntry, SearchTerms terms) {
		boolean match = true;

		String[] keywordList = terms.getKeywords();

		for (String word : keywordList) {

			if (!currentEntry.containsTerm(word)) {
				match = false;
			}
		}

		return match;
	}

	/**
	 * To return all the tasks in database
	 * <p>
	 * Returned result is a clone of the tasks in database, operations done on
	 * the result will not affect the database
	 * 
	 * @return an List<Task> containing all the tasks in database
	 */

	public List<Task> getAll() {
		log.info("Retrieving entire database");

		List<Task> result = new ArrayList<Task>();

		for (Task currentEntry : taskStore) {
			result.add(new Task(currentEntry));
		}

		log.info("Database of size " + result.size() + " returned");

		return result;
	}

	public void setAll(List<Task> incoming) throws IOException,
			WillNotWriteToCorruptFileException {
		assert (incoming != null);

		log.info("Recieved incoming data of size " + incoming.size());

		verifyFileWritingAbility();

		List<Task> newList = new ArrayList<Task>();

		for (Task currentEntry : incoming) {
			newList.add(new Task(currentEntry));
		}

		log.info("Incoming data saved to temporary copy");

		Collections.sort(newList);

		log.info("Send data to FileMgmt");
		diskFile.writeDataBaseToFile(newList);

		log.info("FileMgmt saved successfully, permanently use new list");
		taskStore = newList;

	}

	/**
	 * To add a new task to database
	 * 
	 * @param newTask
	 *            Task to be added
	 * @throws IOException
	 *             if cannot commit changes to file, database will not be
	 *             modified
	 * @throws WillNotWriteToCorruptFileException
	 */

	public void add(Task newTask) throws IOException,
			WillNotWriteToCorruptFileException {
		assert (newTask != null);

		verifyFileWritingAbility();

		List<Task> newList = new ArrayList<Task>();

		for (Task currentEntry : taskStore) {
			newList.add(new Task(currentEntry));
		}

		newList.add(newTask);

		log.info("Incoming task saved to temporary copy");

		Collections.sort(newList);

		log.info("Send data to FileMgmt");
		diskFile.writeDataBaseToFile(newList);

		log.info("FileMgmt saved successfully, permanently use new list");
		taskStore = newList;

	}

	/**
	 * To locate existing task in database
	 * 
	 * @param serial
	 *            Serial number of task to be located
	 * 
	 * @return the located task with matching serial number
	 * @throws NoSuchElementException
	 *             if existing Task by serial number cannot be found
	 */

	public Task locateATask(int serial) throws NoSuchElementException {
		log.info("Asked to search for this serial " + serial);
		for (Task toFind : taskStore) {
			if (toFind.getSerial() == serial) {
				log.info("Task with this serial " + serial + " found");
				return new Task(toFind);
			}
		}

		log.warning("Task with this serial " + serial + " not found");
		throw new NoSuchElementException();
	}

	/**
	 * To update existing task in database
	 * 
	 * @param originalSerial
	 *            Serial number of task to be updated
	 * @param updated
	 *            The new task to replace the old
	 * 
	 * @throws NoSuchElementException
	 *             if existing Task by serial number cannot be found
	 * @throws IOException
	 *             if cannot commit changes to file, database will not be
	 *             modified
	 * @throws WillNotWriteToCorruptFileException
	 */

	public void update(int originalSerial, Task updated)
			throws NoSuchElementException, IOException,
			WillNotWriteToCorruptFileException {
		assert (updated != null);

		log.info("Received serial " + originalSerial + " and updated task "
				+ updated.showInfo());

		verifyFileWritingAbility();

		List<Task> newList = new ArrayList<Task>();

		for (Task currentEntry : taskStore) {
			newList.add(new Task(currentEntry));
		}

		boolean isOriginalTaskFound = false;
		for (Task toBeUpdated : newList) {
			if (toBeUpdated.getSerial() == originalSerial) {
				toBeUpdated.becomeThis(updated);
				isOriginalTaskFound = true;
				log.info("Old task with this serial " + originalSerial
						+ " found");
				break;
			}
		}

		log.info("Incoming data saved to temporary copy");

		Collections.sort(newList);

		if (isOriginalTaskFound) {
			log.info("Send data to FileMgmt");
			diskFile.writeDataBaseToFile(newList);
			log.info("FileMgmt saved successfully, permanently use new list");
			taskStore = newList;
		} else {
			log.warning("Cannot find old task, exception thrown");
			throw new NoSuchElementException();
		}

	}

	private void verifyFileWritingAbility() throws IOException,
			WillNotWriteToCorruptFileException {
		if (fileAttributes.equals(DB_File_Status.FILE_PERMISSIONS_UNKNOWN)
				|| fileAttributes.equals(DB_File_Status.FILE_READ_ONLY)
				|| fileAttributes.equals(DB_File_Status.FILE_IS_LOCKED)) {
			log.warning("Permissions say no file writing permission");
			throw new IOException();
		}

		if (fileAttributes.equals(DB_File_Status.FILE_IS_CORRUPT)) {
			log.warning("File corrupt status");
			throw new WillNotWriteToCorruptFileException();
		}

		log.info("Has full permissions");
		fileAttributes = DB_File_Status.FILE_ALL_OK;

	}

	/**
	 * To delete existing task in database
	 * 
	 * @param serial
	 *            Serial number of task to be deleted
	 * 
	 * @throws NoSuchElementException
	 *             if existing Task by serial number cannot be found
	 * @throws IOException
	 *             if cannot commit changes to file, database will not be
	 *             modified
	 * @throws WillNotWriteToCorruptFileException
	 */

	public void delete(int serial) throws NoSuchElementException, IOException,
			WillNotWriteToCorruptFileException {
		log.info("Received this serial " + serial);

		verifyFileWritingAbility();

		List<Task> newList = new ArrayList<Task>();

		for (Task currentEntry : taskStore) {
			newList.add(new Task(currentEntry));
		}

		boolean isOriginalTaskFound = false;

		Task currentTask;

		for (int i = 0; i < newList.size(); i++) {
			currentTask = newList.get(i);

			if (currentTask.getSerial() == serial) {
				newList.remove(i);
				isOriginalTaskFound = true;
				log.info("Original Task found");
				break;
			}

		}

		if (isOriginalTaskFound) {

			log.info("Send data to FileMgmt");
			diskFile.writeDataBaseToFile(newList);

			log.info("FileMgmt saved successfully, permanently use new list");
			taskStore = newList;
		} else {
			log.warning("No task with this serial found");
			throw new NoSuchElementException();
		}

	}

	/**
	 * To delete given tasks in database
	 * 
	 * @param serial
	 *            array of serial numbers of tasks to be deleted
	 * 
	 * @throws NoSuchElementException
	 *             if at least one task by serial number cannot be found
	 * @throws IOException
	 *             if cannot commit changes to file, database will not be
	 *             modified
	 * @throws WillNotWriteToCorruptFileException
	 */

	public void delete(List<Integer> serial) throws NoSuchElementException,
			IOException, WillNotWriteToCorruptFileException {
		log.info("received array of serials of size " + serial.size());

		assert (serial != null);

		verifyFileWritingAbility();

		List<Task> newList = new ArrayList<Task>();

		for (Task currentEntry : taskStore) {
			newList.add(new Task(currentEntry));
		}

		for (Integer currentSerial : serial) {
			boolean taskFound = true;

			for(Iterator<Task> iter = newList.iterator(); iter.hasNext();){
				Task current = iter.next();
				
				if(current.getSerial() == currentSerial){
					iter.remove();

					taskFound = true;
					break; // Break iterator, go to next serial number
				}
			}

			if (!taskFound) {
				log.warning("At least one task is not found, exception thrown");
				throw new NoSuchElementException();
			}

		}

		log.info("Send data to FileMgmt");
		diskFile.writeDataBaseToFile(newList);
		taskStore = newList;
		log.info("FileMgmt saved successfully, permanently use new list");

	}

	/**
	 * To delete ALL tasks in database
	 * 
	 * @throws IOException
	 *             if cannot commit changes to file, database will not be
	 *             modified
	 * @throws WillNotWriteToCorruptFileException
	 */

	public void deleteAll() throws IOException,
			WillNotWriteToCorruptFileException {
		verifyFileWritingAbility();

		log.info("Send data to FileMgmt");
		diskFile.writeDataBaseToFile(new ArrayList<Task>());

		log.info("FileMgmt saved successfully, permanently use new list");
		taskStore.clear();
	}

	/**
	 * To get file permissions of database like read-only or full access. Should
	 * run this method on startup.
	 * <p>
	 * List of all Statuses
	 * <p>
	 * DB_File_Status.FILE_ALL_OK DB_File_Status.FILE_READ_ONLY
	 * DB_File_Status.FILE_PERMISSIONS_UNKNOWN DB_File_Status.FILE_IS_CORRUPT
	 * DB_File_Status.FILE_IS_LOCKED
	 * 
	 * @return return Status in this format Database.DB_File_Status.FILE_ALL_OK
	 */

	public DB_File_Status getFileAttributes() {
		return fileAttributes;
	}

	public void unlockFileToExit() {
		log.info("Send command to FileMgmt to unlock the file");
		diskFile.closeFile();
	}

	private DB_File_Status parseFileAttributes(FileManagement diskFile) {

		if (diskFile.getFileAttributes().equals(FileStatus.FILE_ALL_OK)) {
			return DB_File_Status.FILE_ALL_OK;
		}

		if (diskFile.getFileAttributes().equals(FileStatus.FILE_READ_ONLY)) {
			return DB_File_Status.FILE_READ_ONLY;
		}

		if (diskFile.getFileAttributes().equals(FileStatus.FILE_IS_LOCKED)) {
			return DB_File_Status.FILE_IS_LOCKED;
		}

		if (diskFile.getFileAttributes().equals(FileStatus.FILE_IS_CORRUPT)) {
			return DB_File_Status.FILE_IS_CORRUPT;
		}

		if (diskFile.getFileAttributes().equals(
				FileStatus.FILE_PERMISSIONS_UNKNOWN)) {
			return DB_File_Status.FILE_PERMISSIONS_UNKNOWN;
		}

		return DB_File_Status.FILE_PERMISSIONS_UNKNOWN;

	}

}
