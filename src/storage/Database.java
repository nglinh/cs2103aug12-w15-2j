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

import storage.FileManagement.FileStatus;
import shared.SearchTerms;
import shared.Task;




public class Database {

	public static enum DB_File_Status {	FILE_ALL_OK, FILE_READ_ONLY, 
		FILE_PERMISSIONS_UNKNOWN, FILE_IS_CORRUPT, FILE_IS_LOCKED};


		private ArrayList<Task> taskStore = new ArrayList<Task>();

		private Stack<ArrayList<Task>> undoOperations = new Stack<ArrayList<Task>>();

		private FileManagement diskFile;
		private DB_File_Status fileAttributes;

		private int undoStepsLeft = 0;
		
		private static Database theOne = null;
		
		public static Database getInstance(){
			if(theOne == null){
				theOne = new Database();
			}
			
			return theOne;
		}

		/**
		 * To instantiate a database                       
		 * Also instantiates the fileManagement class
		 * 
		 */

		private Database() {
			diskFile = FileManagement.getInstance();
			diskFile.prepareDatabaseFile();
			diskFile.readFileAndDetectCorruption(taskStore);
			fileAttributes = parseFileAttributes(diskFile);
		}
		
		
		

		/**
		 * To give the results based on a search term.
		 * <p>
		 * Returned result is a clone of the tasks in database,
		 *  operations done on the result will not affect the database
		 *
		 * @param terms Input in the form of a search term class
		 * @return an ArrayList<Task> containing all the matched tasks    
		 */

		public ArrayList<Task> search(SearchTerms terms) {
			ArrayList<Task> searchResults = new ArrayList<Task>();

			for(Task currentEntry : taskStore)	{
				if(taskMeetsSearchTerms(currentEntry, terms)) {
					searchResults.add(new Task(currentEntry));
				}
			}

			return searchResults;
		}



		private boolean taskMeetsSearchTerms(Task currentEntry, SearchTerms terms)	{

			boolean taskDone = false;
			boolean taskUndone = false;

			boolean taskFloating = false;
			boolean taskDeadline = false;
			boolean taskTimed = false;

			boolean keywordMatched = false;
			boolean dateRangeMatched = false;


			if(terms.completeFlag() && currentEntry.isDone()) {
				taskDone = true;
			} else if(terms.completeFlag() == false) {
				taskDone = true;
			}

			if(terms.incompleteFlag() && !currentEntry.isDone()) {
				taskUndone = true;
			} else if(terms.incompleteFlag() == false) {
				taskUndone = true;
			}

			if(terms.floatingFlag() && currentEntry.isFloatingTask()) {
				taskFloating = true;
			} else if(terms.floatingFlag() == false) {
				taskFloating = true;
			}

			if(terms.deadlineFlag() && currentEntry.isDeadlineTask()) {
				taskDeadline = true;
			} else if(terms.deadlineFlag() == false) {
				taskDeadline = true;
			}

			if(terms.timedFlag() && currentEntry.isTimedTask()) {
				taskTimed = true;
			} else if(terms.timedFlag() == false) {
				taskTimed = true;
			}

			if((terms.doesSearchContainKeywords() == false) || keywordMatching(currentEntry, terms)) {
				keywordMatched = true;
			}


			if(terms.doesSearchContainDateRange()) {
				dateRangeMatched = dateMatching(currentEntry, terms);
			} else {
				dateRangeMatched = true;
			}


			boolean areAllConditionsSatisfied = taskDone && taskUndone 
					&& taskFloating && taskDeadline && taskTimed
					&& keywordMatched && dateRangeMatched;

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
				timeToCompare = currentEntry.getStartDate();
			}

			if(terms.getStartDate().isEqual(timeToCompare) || 
					terms.getEndDate().isEqual(timeToCompare)) {
				return true;
			}

			if(terms.getStartDate().isBefore(timeToCompare) && 
					timeToCompare.isBefore(terms.getEndDate())) {
				return true;
			}



			return false;

		}

		//Check if task contains all the keywords specified
		private boolean keywordMatching(Task currentEntry, SearchTerms terms) {
			boolean match = true;

			String[] keywordList = terms.getKeywords();

			for(String word : keywordList)	{

				if(!currentEntry.containsTerm(word))	{
					match = false;
				}
			}

			return match;
		}

		/**
		 * To return all the tasks in database                           
		 * <p>
		 * Returned result is a clone of the tasks in database,
		 *  operations done on the result will not affect the database
		 *  
		 * @return an ArrayList<Task> containing all the tasks in database   
		 */

		public ArrayList<Task> readAll() {
			ArrayList<Task> result = new ArrayList<Task>();

			for(Task currentEntry : taskStore)	{
				result.add(new Task(currentEntry));
			}

			return result;
		}

		/**
		 * To return only floating tasks in database
		 * 
		 * <p>
		 * Returned result is a clone of the tasks in database,
		 *  operations done on the result will not affect the database
		 *
		 * @return an ArrayList<Task> containing all the floating tasks in database   
		 */

		public ArrayList<Task> readFloatingOnly() {
			ArrayList<Task> floatingOnly = new ArrayList<Task>();
			for(Task temp : taskStore) {
				if(temp.isFloatingTask()) {
					floatingOnly.add(new Task(temp));
				}
			}

			return floatingOnly;
		}

		/**
		 * To return all tasks except floating database
		 * 
		 * <p>
		 * Returned result is a clone of the tasks in database,
		 *  operations done on the result will not affect the database
		 *
		 * @return an ArrayList<Task> containing all the floating tasks except floating in database   
		 */

		public ArrayList<Task> readAllExceptFloating() {
			ArrayList<Task> allExceptFloating = new ArrayList<Task>();
			for(Task temp : taskStore) {
				if(!temp.isFloatingTask())	{
					allExceptFloating.add(new Task(temp));
				}
			}

			return allExceptFloating;
		}

		/**
		 * To add a new task to database   
		 *                         
		 * @param newTask Task to be added
		 * @throws IOException if cannot commit changes to file, database will not be modified
		 * @throws WillNotWriteToCorruptFileException 
		 */

		public void add(Task newTask) throws IOException, WillNotWriteToCorruptFileException {

			if(newTask == null){
				throw new IllegalArgumentException();
			}

			verifyFileWritingAbility();

			cloneDatabase();

			taskStore.add(newTask);
			Collections.sort(taskStore);

			diskFile.writeDataBaseToFile(taskStore);


		}

		/**
		 * To locate existing task in database   
		 *                         
		 * @param serial Serial number of task to be located
		 * 
		 * @return the located task with matching serial number 
		 * @throws NoSuchElementException if existing Task by serial number cannot be found
		 */

		public Task locateATask(int serial) throws NoSuchElementException{
			for(Task toFind : taskStore) {
				if(toFind.getSerial() == serial) {
					return new Task(toFind);
				}
			}

			throw new NoSuchElementException();
		}

		/**
		 * To update existing task in database   
		 *                         
		 * @param originalSerial Serial number of task to be updated
		 * @param updated The new task to replace the old
		 * 
		 * @throws NoSuchElementException if existing Task by serial number cannot be found
		 * @throws IOException if cannot commit changes to file, database will not be modified
		 * @throws WillNotWriteToCorruptFileException 
		 */

		public void update(int originalSerial, Task updated) throws NoSuchElementException, IOException, WillNotWriteToCorruptFileException{
			if(updated == null) {
				throw new IllegalArgumentException();
			}

			verifyFileWritingAbility();

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

		private void verifyFileWritingAbility() throws IOException,
		WillNotWriteToCorruptFileException {
			if(fileAttributes.equals(DB_File_Status.FILE_PERMISSIONS_UNKNOWN)
					|| fileAttributes.equals(DB_File_Status.FILE_READ_ONLY)
					|| fileAttributes.equals(DB_File_Status.FILE_IS_LOCKED)) {
				throw new IOException();
			}


			if(fileAttributes.equals(DB_File_Status.FILE_IS_CORRUPT)) {
				throw new WillNotWriteToCorruptFileException();
			}

			fileAttributes = DB_File_Status.FILE_ALL_OK;

		}

		/**
		 * To delete existing task in database   
		 *                         
		 * @param serial Serial number of task to be deleted
		 * 
		 * @throws NoSuchElementException if existing Task by serial number cannot be found
		 * @throws IOException if cannot commit changes to file, database will not be modified
		 * @throws WillNotWriteToCorruptFileException 
		 */

		public void delete(int serial) throws NoSuchElementException, IOException, WillNotWriteToCorruptFileException {

			verifyFileWritingAbility();

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

		/**
		 * To delete ALL tasks in database   
		 *                         
		 * @throws IOException if cannot commit changes to file, database will not be modified
		 * @throws WillNotWriteToCorruptFileException 
		 */


		public void deleteAll() throws IOException, WillNotWriteToCorruptFileException {
			verifyFileWritingAbility();

			cloneDatabase();
			taskStore.clear();
			diskFile.writeDataBaseToFile(taskStore);
		}

		/**
		 * To delete all done tasks in database   
		 *                         
		 * @throws IOException if cannot commit changes to file, database will not be modified
		 * @throws WillNotWriteToCorruptFileException 
		 */

		public void deleteDone() throws IOException, WillNotWriteToCorruptFileException {
			verifyFileWritingAbility();

			cloneDatabase();

			ArrayList<Task> onlyUndoneTasks = new ArrayList<Task>();

			for(Task currentTask : taskStore) {
				if(!currentTask.isDone()) {
					onlyUndoneTasks.add(currentTask);
				}
			}

			taskStore = onlyUndoneTasks;
			diskFile.writeDataBaseToFile(taskStore);
		}

		/**
		 * To delete all tasks that are past their deadlines or endtimes in database
		 * <p>
		 * Timing will be based on end time for timed tasks.
		 *  Floating tasks will not be touched   
		 *                         
		 * @throws IOException if cannot commit changes to file, database will not be modified
		 * @throws WillNotWriteToCorruptFileException 
		 */

		public void deleteOver() throws IOException, WillNotWriteToCorruptFileException {
			verifyFileWritingAbility();

			cloneDatabase();

			DateTime currentTime = DateTime.now();

			ArrayList<Task> onlyPendingTasks = new ArrayList<Task>();
			for(Task currentTask : taskStore) {

				DateTime timeToCompare;
				if(currentTask.isDeadlineTask()) {
					timeToCompare = currentTask.getDeadline();
				} else if (currentTask.isTimedTask()) {
					timeToCompare = currentTask.getEndDate();
				} else { //Floating tasks
					onlyPendingTasks.add(currentTask);
					continue;
				}

				if(timeToCompare.isAfter(currentTime)) {
					onlyPendingTasks.add(currentTask);
				}

			}

			taskStore = onlyPendingTasks;

			diskFile.writeDataBaseToFile(taskStore);

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
		 * @throws WillNotWriteToCorruptFileException 
		 */

		public void undo() throws IOException, NoMoreUndoStepsException, WillNotWriteToCorruptFileException {

			verifyFileWritingAbility();


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
		 * List of all Statuses
		 * <p>
		 * DB_File_Status.FILE_ALL_OK
		 * DB_File_Status.FILE_READ_ONLY
		 * DB_File_Status.FILE_PERMISSIONS_UNKNOWN
		 * DB_File_Status.FILE_IS_CORRUPT
		 * DB_File_Status.FILE_IS_LOCKED
		 * 
		 * @return return Status in this format Database.DB_File_Status.FILE_ALL_OK

		 */

		public DB_File_Status getFileAttributes() {
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


		private DB_File_Status parseFileAttributes(FileManagement diskFile) {

			if(diskFile.getFileAttributes().equals(FileStatus.FILE_ALL_OK)) {
				return DB_File_Status.FILE_ALL_OK;
			}

			if(diskFile.getFileAttributes().equals(FileStatus.FILE_READ_ONLY))	{
				return DB_File_Status.FILE_READ_ONLY;
			}

			if(diskFile.getFileAttributes().equals(FileStatus.FILE_IS_LOCKED)) {
				return DB_File_Status.FILE_IS_LOCKED;
			}

			if(diskFile.getFileAttributes().equals(FileStatus.FILE_IS_CORRUPT)) {
				return DB_File_Status.FILE_IS_CORRUPT;
			}

			if(diskFile.getFileAttributes().equals(FileStatus.FILE_PERMISSIONS_UNKNOWN)) {
				return DB_File_Status.FILE_PERMISSIONS_UNKNOWN;
			}



			return DB_File_Status.FILE_PERMISSIONS_UNKNOWN;

		}



















}
