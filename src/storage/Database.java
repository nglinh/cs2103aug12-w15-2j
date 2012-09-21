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

	public Database() {
		diskFile = new FileManagement(taskStore);
		fileAttributes = parseFileAttributes(diskFile);
	}

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

	public ArrayList<Task> readAll() {
		return taskStore;
	}

	public ArrayList<Task> readFloatingOnly() {
		ArrayList<Task> floatingOnly = new ArrayList<Task>();
		for(Task temp : taskStore) {
			if(temp.isFloatingTask()) {
				floatingOnly.add(temp);
			}
		}

		return floatingOnly;
	}


	public ArrayList<Task> readAllExceptFloating() {
		ArrayList<Task> allExceptFloating = new ArrayList<Task>();
		for(Task temp : taskStore) {
			if(!temp.isFloatingTask())	{
				allExceptFloating.add(temp);
			}
		}

		return allExceptFloating;
	}


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

	public void delete(int serial) throws IOException {

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



	public void cloneDatabase() {
		ArrayList<Task> newCopy = new ArrayList<Task>();
		for(Task currentTask : taskStore) {
			newCopy.add(new Task(currentTask));
		}

		undoOperations.push(newCopy);
		undoStepsLeft++;

	}

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


	public Status getFileAttributes() {
		return fileAttributes;
	}
	
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
