package storage;

/**  
 * Database.java 
 * A class for managing all queries to the database and disk
 * @author  Yeo Kheng Meng
 */ 


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Stack;

import org.joda.time.DateTime;

import shared.SearchTerms;
import shared.Task;




public class Database {

	public static enum Status { 
		FILE_CAN_READ_AND_WRITE, FILE_READ_ONLY, FILE_WRITE_ONLY, FILE_CANNOT_CREATE, FILE_CANNOT_WRITE,
	};


	protected ArrayList<Task> taskStore = new ArrayList<Task>();
	protected ArrayList<Task> latestSearchList = new ArrayList<Task>();

	protected Stack<ArrayList<Task>> undoOperations = new Stack<ArrayList<Task>>();

	protected FileManagement diskFile;
	protected Status fileAttributes;
	
	protected int undoStepsLeft = 0;
	
	/**
	 * To instantiate a database                       
	 * Also instantiates the fileManagement class
	 * 
	 */

	public Database() {
		diskFile = new FileManagement(taskStore);
		fileAttributes = parseFileAttributes(diskFile);
	}
	
	/**
	 * To given the results of a search term                           
	 *
	 * @param terms Input in the form of a search term class
	 * @return an ArrayList<Task> containing all the matched tasks    
	 */

	public ArrayList<Task> search(SearchTerms terms) {
		ArrayList<Task> searchResults = new ArrayList<Task>();
		
		for(Task currentEntry : taskStore)	{
			if(taskMeetsSearchTerms(currentEntry, terms)) {
				searchResults.add(currentEntry);
			}
		}

		latestSearchList = searchResults; 

		return searchResults;
	}



	private boolean taskMeetsSearchTerms(Task currentEntry, SearchTerms terms)	{
		
		boolean taskCompleteness = false;
		boolean taskType = false;
		boolean keywordMatched = false;
		boolean dateRangeMatched = false;
		
		
		if(terms.completeFlag() && currentEntry.isDone()) {
			taskCompleteness = true;
		}
		
		if(terms.incompleteFlag() && !currentEntry.isDone()) {
			taskCompleteness = true;
		}
		
		if(terms.floatingFlag() && currentEntry.isFloatingTask()) {
			taskType = true;
		}
		
		if(terms.deadlineFlag() && currentEntry.isDeadlineTask()) {
			taskType = true;
		}
		
		if(terms.timedFlag() && currentEntry.isTimedTask()) {
			taskType = true;
		}
		
		if(terms.getKeywords() == null || keywordMatching(currentEntry, terms)) {
			keywordMatched = true;
		}
		
		
		if(terms.getStartRange() == null) {
			dateRangeMatched = true;
		} else {
			dateRangeMatched = dateMatching(currentEntry, terms);
		}
		
		
		boolean areAllConditionsSatisfied = taskCompleteness && taskType && keywordMatched && dateRangeMatched;
		
		return areAllConditionsSatisfied;
	}
	
	private boolean dateMatching(Task currentEntry, SearchTerms terms) {
		if(currentEntry.isFloatingTask()) {
			return false;
		}
		
		DateTime timeToCompare;
		
		if(currentEntry.isDeadlineTask()) {
			timeToCompare = currentEntry.getDeadline();
		} else {
			timeToCompare = currentEntry.getStartTime();
		}
		
		if(terms.getStartRange().isEqual(timeToCompare) || 
				terms.getEndRange().isEqual(timeToCompare)) {
			return true;
		}
		
		if(terms.getStartRange().isBefore(timeToCompare) && 
				timeToCompare.isBefore(terms.getEndRange())) {
			return true;
		}
		
		
		
		return false;
		
	}
	
	private boolean keywordMatching(Task currentEntry, SearchTerms terms) {
		boolean match = false;
		
		String[] keywordList = terms.getKeywords();
		
		for(String word : keywordList)	{

			if(currentEntry.searchName(word))	{
				match = true;
			}
		}
		
		return match;
	}
	
	/**
	 * To return all the tasks in database                           
	 *
	 * @return an ArrayList<Task> containing all the tasks in database   
	 */

	public ArrayList<Task> readAll() {
		return taskStore;
	}
	
	/**
	 * To return only floating tasks in database                           
	 *
	 * @return an ArrayList<Task> containing all the floating tasks in database   
	 */

	public ArrayList<Task> readFloatingOnly() {
		ArrayList<Task> floatingOnly = new ArrayList<Task>();
		for(Task temp : taskStore) {
			if(temp.isFloatingTask()) {
				floatingOnly.add(temp);
			}
		}

		return floatingOnly;
	}
	
	/**
	 * To return all tasks except floating database                           
	 *
	 * @return an ArrayList<Task> containing all the floating tasks except floating in database   
	 */

	public ArrayList<Task> readAllExceptFloating() {
		ArrayList<Task> allExceptFloating = new ArrayList<Task>();
		for(Task temp : taskStore) {
			if(!temp.isFloatingTask())	{
				allExceptFloating.add(temp);
			}
		}

		return allExceptFloating;
	}

	/**
	 * To add a new task to database   
	 *                         
	 * @param newTask Task to be added
	 * @throws IOException if cannot commit changes to file, database will not be modified
	 */

	public void add(Task newTask) throws IOException {

		if(newTask == null) {
			throw new IllegalArgumentException();
		}
		
		if(diskFile.canWriteFile() == false) {
			throw new IOException();
		}
		
		cloneDatabase();
		
		taskStore.add(newTask);
		Collections.sort(taskStore);

		diskFile.writeDataBaseToFile(taskStore);


	}
	
	/**
	 * To update existing task in database   
	 *                         
	 * @param originalSerial Serial number of task to be updated
	 * @param updated The new task to replace the old
	 * 
	 * @throws NoSuchElementException if existing Task by serial number cannot be found
	 * @throws IOException if cannot commit changes to file, database will not be modified
	 */

	public void update(int originalSerial, Task updated) throws NoSuchElementException, IOException{
		if(updated == null) {
			throw new IllegalArgumentException();
		}
		
		
		if(diskFile.canWriteFile() == false) {
			throw new IOException();
		}

		cloneDatabase();

		boolean isOriginalTaskFound = false;
		for(Task toBeUpdated : taskStore) {
			if(toBeUpdated.getSerial() == originalSerial)	{		
				toBeUpdated.updateOrClone(updated);
				isOriginalTaskFound = true;

				Collections.sort(taskStore);
				break;
			}
		}

		if(isOriginalTaskFound) {
			diskFile.writeDataBaseToFile(taskStore);
		} else	{
			undoOperations.pop();
			undoStepsLeft--;
			throw new NoSuchElementException();
		}

	}
	
	/**
	 * To delete existing task in database   
	 *                         
	 * @param originalSerial Serial number of task to be deleted
	 * 
	 * @throws NoSuchElementException if existing Task by serial number cannot be found
	 * @throws IOException if cannot commit changes to file, database will not be modified
	 */

	public void delete(int serial) throws NoSuchElementException, IOException {

		if(diskFile.canWriteFile() == false) {
			throw new IOException();
		}

		cloneDatabase();

		boolean isOriginalTaskFound = false;

		Task currentTask;

		for(int i = 0; i < taskStore.size(); i++) {
			currentTask = taskStore.get(i);

			if(currentTask.getSerial() == serial) {
				taskStore.remove(i);
				Collections.sort(taskStore);
				isOriginalTaskFound = true;
				break;
			}

		}

		if(isOriginalTaskFound) {
			diskFile.writeDataBaseToFile(taskStore);
		} else	{
			undoOperations.pop();
			undoStepsLeft--;
			throw new NoSuchElementException();
		}


	}



	private void cloneDatabase() {
		ArrayList<Task> newCopy = new ArrayList<Task>();
		for(Task currentTask : taskStore) {
			newCopy.add(new Task(currentTask));
		}

		undoOperations.push(newCopy);
		undoStepsLeft++;

	}
	
	/**
	 * To undo the last write operation in database   
	 *                        
	 * @throws IOException if cannot commit changes to file, database will not be modified
	 * @throws NoMoreUndoStepsException if no more steps left to undo since program start
	 */

	public void undo() throws IOException, NoMoreUndoStepsException {

		if(diskFile.canWriteFile() == false) {
			throw new IOException();
		}


		if(undoOperations.isEmpty()) {
			throw new NoMoreUndoStepsException();
		} else {
			taskStore = undoOperations.pop();
			undoStepsLeft--;
			diskFile.writeDataBaseToFile(taskStore);
		}



	}

	/**
	 * To get file permissions of database like read-only or full access. Should run this method on startup.
	 * <p>
	 * Statuses available
	 * Database.Status.FILE_CAN_READ_AND_WRITE
	 * Database.Status.FILE_READ_ONLY
	 * <p>
	 * Also supports but may not be necessary FILE_WRITE_ONLY, FILE_CANNOT_CREATE, FILE_CANNOT_WRITE,
	 * 
	 * @return return Status in this format Database.Status.FILE_CAN_READ_AND_WRITE;

	 */
	
	public Status getFileAttributes() {
		return fileAttributes;
	}
	
	/**
	 * Get the number of undo operations remaining
	 * 
	 * @return number of undo steps left

	 */
	
	public int getUndoStepsLeft() {
		return undoStepsLeft;
	}


	private Status parseFileAttributes(FileManagement diskFile) {
		if(diskFile.getFileAttributes().equals(FileManagement.FileStatus.FILE_CAN_READ_AND_WRITE)) {
			fileAttributes = Status.FILE_CAN_READ_AND_WRITE;
		}

		if(diskFile.getFileAttributes().equals(FileManagement.FileStatus. FILE_READ_ONLY))	{
			fileAttributes = Status. FILE_READ_ONLY;
		}

		if(diskFile.getFileAttributes().equals(FileManagement.FileStatus.FILE_WRITE_ONLY))	{
			fileAttributes = Status.FILE_WRITE_ONLY;
		}

		if(diskFile.getFileAttributes().equals(FileManagement.FileStatus.FILE_CANNOT_CREATE)) {
			fileAttributes = Status.FILE_CANNOT_CREATE;
		}

		if(diskFile.getFileAttributes().equals(FileManagement.FileStatus.FILE_CANNOT_WRITE)) {
			fileAttributes = Status.FILE_CANNOT_WRITE;
		}

		return fileAttributes;

	}
	
	
	
















}
