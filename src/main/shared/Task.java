//@author A0081007U
package main.shared;
/**  
 * Task.java 
 * A class for holding all the information for each Task
 * @author  Yeo Kheng Meng
 */ 



import java.util.Comparator;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class Task implements Comparable<Task> {
	public static enum TaskType{FLOATING,DEADLINE,TIMED};

	public static final DateTime INVALID_DATE_FIELD = new DateTime(Long.MAX_VALUE);

	public static final int SERIAL_NUMBER_START = 0;

	private static final int COMPARETO_SMALLER = -1;
	private static final int COMPARETO_EQUAL = 0;
	private static final int COMPARETO_BIGGER = 1;


	private static int nextSerial = SERIAL_NUMBER_START; 


	private int serial; //A unique identifier for each task. Will reset on each new program launch

	private TaskType type = null;
	private String taskName = null;

	private DateTime startDate = INVALID_DATE_FIELD;
	private DateTime endDate = INVALID_DATE_FIELD;
	private DateTime deadline = INVALID_DATE_FIELD;


	private boolean isCompleted = false;

	/**
	 * Instantiate an undone floating task                        
	 *
	 * @param name the task description
	 * @throws IllegalArgumentException if name is null or empty
	 */

	public Task(String name) {

		if((name == null) || (name.isEmpty())) {
			throw new IllegalArgumentException();
		}

		this.type = TaskType.FLOATING;
		this.taskName = name;

		serial = nextSerial;
		nextSerial++;
	}

	/**
	 * Instantiate a floating task with known done value.                     
	 *
	 * @param name the task description
	 * @param done the done value.
	 */

	public Task(String name, boolean done)	{

		Task newTask = new Task(name);
		newTask.done(done);
		this.becomeThis(newTask);
	}

	/**
	 * Instantiate an undone deadline task.                     
	 *
	 * @param name the task description
	 * @param deadline the deadline in Joda DateTime form
	 */

	public Task(String name, DateTime deadline) {

		Task newTask = new Task(name);
		newTask.changeToDeadline(deadline);
		this.becomeThis(newTask);

	}

	/**
	 * Instantiate a deadline task with known done value.                     
	 *
	 * @param name the task description
	 * @param deadline the deadline in Joda DateTime form
	 * @param done the done value.
	 */

	public Task(String name, DateTime deadline, boolean done) {

		Task newTask = new Task(name, deadline);
		newTask.done(done);
		this.becomeThis(newTask);

	}

	/**
	 * Instantiate an undone timed task.
	 * <p>
	 * Accepts case where start and end time are the same                     
	 *
	 * @param name the task description
	 * @param startDate the start time and date in Joda DateTime form
	 * @param endDate the end time and date in Joda DateTime form
	 */

	public Task(String name, DateTime startDate, DateTime endDate) {

		Task newTask = new Task(name);
		newTask.changeToTimed(startDate, endDate);
		this.becomeThis(newTask);
	}

	/**
	 * Instantiate a timed task with known done value.
	 * <p>
	 * Accepts case where start and end time are the same                      
	 *
	 * @param name the task description
	 * @param startDate the start time and date in Joda DateTime form
	 * @param endDate the end time and date in Joda DateTime form
	 * @param done the done value.
	 */

	public Task(String name, DateTime startDate, DateTime endDate, boolean done) {
		Task newTask = new Task(name, startDate, endDate);
		newTask.done(done);
		this.becomeThis(newTask);
	}


	/**
	 * Clone the incoming task of all details including serial number      
	 *
	 * @param toBeCloned the new task to be cloned
	 * @throws IllegalArgumentException if given task is null
	 */
	public Task(Task toBeCloned) {
		if(toBeCloned == null) {
			throw new IllegalArgumentException();
		}

		this.becomeThis(toBeCloned);
	}

	/**
	 * Clone and become the current task given to this object.                           
	 *
	 * All the fields including serial number 
	 * of the parameter task will be copied to this object
	 *
	 * @param updated Task to be cloned
	 * @throws IllegalArgumentException if updated task is null           
	 * 
	 */

	public void becomeThis(Task updated) {

		if(updated == null) {
			throw new IllegalArgumentException();
		}

		this.type = updated.getType();
		this.taskName = updated.getTaskName();
		this.isCompleted = updated.isDone();

		this.serial = updated.getSerial();

		if(!updated.getStartDate().equals(INVALID_DATE_FIELD)) {
			this.startDate = new DateTime(updated.getStartDate());
		}

		if(!updated.getEndDate().equals(INVALID_DATE_FIELD))	{
			this.endDate = new DateTime(updated.getEndDate());
		}

		if(!updated.getDeadline().equals(INVALID_DATE_FIELD))	{
			this.deadline = new DateTime(updated.getDeadline());
		}
	}

	/**
	 * Check if the Task is completed                        
	 *
	 * @return true if task is completed
	 */

	public boolean isDone()	{
		return isCompleted;

	}

	public boolean isFloatingTask()	{
		if (this.type.equals(TaskType.FLOATING)) {
			return true;
		}

		return false;
	}

	public boolean isTimedTask() {
		if (this.type.equals(TaskType.TIMED)) {
			return true;
		}

		return false;
	}

	public boolean isDeadlineTask()	{
		if (this.type.equals(TaskType.DEADLINE)) {
			return true;
		}

		return false;
	}

	public int getSerial() {
		return serial;
	}


	/**
	 * Returns the type of task                    
	 *
	 *@return type of task. Use Task.TYPE_FLOATING, Task.TYPE_DEADLINE or Task.TYPE_TIMED
	 */
	public TaskType getType()	{
		return type;
	}

	public String getTaskName()	{
		return taskName;
	}

	/**
	 * Get start time of timed task                           
	 *
	 * @return start time if valid or Task.INVALID_DATE_FIELD if not a timed task.           
	 *  
	 */

	public DateTime getStartDate()	{
		return startDate;
	}

	/**
	 * Get end time of timed task                           
	 *
	 * @return end time if valid or Task.INVALID_DATE_FIELD if not a timed task.           
	 *  
	 */


	public DateTime getEndDate(){
		return endDate;
	}

	/**
	 * Get deadline of deadline task                           
	 *
	 * @return deadline or Task.INVALID_DATE_FIELD if not a deadline task.           
	 *  
	 */

	public DateTime getDeadline(){
		return deadline;
	}

	/**
	 * Sets done status for the task                           
	 *
	 * @param newDoneStatus to set whether the task is done or undone     
	 *  
	 */

	public void done(boolean newDoneStatus)	{
		this.isCompleted = newDoneStatus;
	}
	
	
	
	/**
	 * Converts the task to a floating task. No effect if the task is already a floating task.                
	 */


	public void changetoFloating() {

		this.type = TaskType.FLOATING;

		this.deadline = INVALID_DATE_FIELD;
		this.startDate = INVALID_DATE_FIELD;
		this.endDate = INVALID_DATE_FIELD;
	}
	
	/**
	 * Converts the task to a deadline task. 
	 * If the task is already a deadline task, the time given will overwrite the current deadline                           
	 *
	 * @param newDeadline deadline to set for this task
	 * @throws IllegalArgumentException if given date is null or of the form Task.INVALID_DATE_FIELD    
	 *  
	 */

	public void changeToDeadline(DateTime newDeadline)	{
		if((newDeadline == null) 
				|| (newDeadline.equals(INVALID_DATE_FIELD))) {
			throw new IllegalArgumentException("Illegal date to change to deadline: " + newDeadline);
		}


		this.type = TaskType.DEADLINE;

		this.deadline = newDeadline;

		this.startDate = INVALID_DATE_FIELD;
		this.endDate = INVALID_DATE_FIELD;

	}
	
	/**
	 * Converts the task to a timed task. 
	 * If the task is already a timed task, the times given will overwrite the current times                           
	 *
	 * @param newStartDate start date to set for this task
	 * @param newEndDate end date to set for this task
	 * 
	 * @throws IllegalArgumentException if given date is null or of the form Task.INVALID_DATE_FIELD. 
	 * Also throw if start date is after end date
	 *  
	 */

	public void changeToTimed(DateTime newStartDate, DateTime newEndDate)	{

		if((newStartDate == null) 
				|| (newStartDate.equals(INVALID_DATE_FIELD))
				|| (newEndDate == null) 
				|| (newEndDate.equals(INVALID_DATE_FIELD))
				|| (newStartDate.isAfter(newEndDate))) {
			throw new IllegalArgumentException("Invalid start: "+newStartDate+", end: " + newEndDate);
		}

		this.type = TaskType.TIMED;

		this.startDate = newStartDate;
		this.endDate = newEndDate;

		this.deadline = INVALID_DATE_FIELD;

	}

	/**
 	* Changes the name of the task
 	* 
 	* @param newName the new name of this task
 	* @throws IllegalArgumentException if name is null or empty
 	*/


	public void changeName(String newName)	{
		if((newName == null) || (newName.isEmpty())) {
			throw new IllegalArgumentException();
		}

		this.taskName = newName;
	}
	
	/**
	 * Changes the start and end date for a timed task only                         
	 *
	 * @param newStartDate start date to set for this task
	 * @param newEndDate end date to set for this task
	 * 
	 * @throws IllegalArgumentException if given date is null or of the form Task.INVALID_DATE_FIELD. 
	 * Also throw if start date is after end date, if task is not a timed task.
	 *  
	 */

	public void changeStartAndEndDate(DateTime newStartDate, DateTime newEndDate)	{

		if((!this.type.equals(TaskType.TIMED))
				|| (newStartDate == null) 
				|| (newStartDate.equals(INVALID_DATE_FIELD))
				|| (newEndDate == null) 
				|| (newEndDate.equals(INVALID_DATE_FIELD))
				|| (newStartDate.isAfter(newEndDate))) {
			throw new IllegalArgumentException();
		}

		this.startDate = newStartDate;
		this.endDate = newEndDate;
	}
	
	/**
	 * Changes the start and end date for a timed task only                         
	 *
	 * @param newDeadline deadline to set for this task
	 * 
	 * @throws IllegalArgumentException if given date is null or of the form Task.INVALID_DATE_FIELD. 
	 * Also throw if task is not a deadline task
	 *  
	 */


	public void changeDeadline(DateTime newDeadline) {
		if(!this.type.equals(TaskType.DEADLINE)
			|| (newDeadline == null)
			|| (newDeadline.equals(INVALID_DATE_FIELD))){
				throw new IllegalArgumentException();
			}

		this.deadline = newDeadline;
	}
	
	/**
	 * Check if this task is exactly the same as the given task
	 * @param toCompare the task to compare with
	 * @return true if all fields including serial number are the same, false otherwise.
	 *
	 * @throws IllegalArgumentException if given task is null
	 */
	
	public boolean isEqualTo(Task toCompare){
		if(toCompare == null){
			throw new IllegalArgumentException();
		}
		
		boolean name = false;
		boolean type = false;
		boolean start = false;
		boolean end = false;
		boolean deadline = false;
		boolean done = false;
		boolean serial = false;
		
		if(this.taskName.equals(toCompare.getTaskName())){
			name = true;
		}
		
		if(this.type.equals(toCompare.getType())){
			type = true;
		}
		if(this.startDate.equals(toCompare.getStartDate())){
			start = true;
		}
		
		if(this.endDate.equals(toCompare.getEndDate())){
			end = true;
		}
		
		if(this.deadline.equals(toCompare.getDeadline())){
			deadline = true;
		}
		
		if(this.isCompleted == toCompare.isDone()){
			done = true;
		}
		
		if(this.serial == toCompare.getSerial()){
			serial = true;
		}
		
		boolean isEqual = name && type && start && end && deadline && done && serial;
		return isEqual;
	}



	/**
	 * To compare this task with the incoming task based on dates     
	 * <p>
	 * Floating Task will always appear at the bottom.
	 *  The time to use for timed tasks will be start dates.
	 *
	 * @param input the new task to be cloned
	 * @return -1 if receiving object is smaller, 0 if they are equal and 1 if the recieving object is bigger
	 *
	 * @throws IllegalArgumentException if given task is null
	 *
	 */

	@Override
	public int compareTo(Task input) {

		if(input == null) {
			throw new IllegalArgumentException();
		}

		if(this.isFloatingTask() && input.isFloatingTask()) {
			return this.getTaskName().compareToIgnoreCase(input.getTaskName());
		}

		//Floating Tasks are bigger than every other tasks to appear at the bottom
		if(this.isFloatingTask()) {
			return COMPARETO_BIGGER;
		}


		if(input.isFloatingTask())	{
			return COMPARETO_SMALLER;
		}

		DateTime currentTaskDate = comparisonDate(this);
		DateTime inputTaskDate = comparisonDate(input);

		return currentTaskDate.compareTo(inputTaskDate);

	}
	
	/**
	 * Check if the name of this task has the given term. Case-insensitive
	 * @param term keyword to compare with
	 * @return true if name of task has this keyword
	 * 
	 * @throws IllegalArgumentException if given String is null or empty
	 */

	public boolean containsTerm(String term)	{
		if((term == null) || (term.length() == 0)) {
			throw new IllegalArgumentException();
		}

		String taskNameLowerCase = taskName.toLowerCase();
		String termLowerCase = term.toLowerCase();

		if(taskNameLowerCase.contains(termLowerCase))	{
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * See if time of the task is within the given range inclusive. 
	 * For timed tasks, the start date is used.
	 * 
	 * @param startRange start of time range
	 * @param endRange end of time range
	 * @return true if the task is within this range
	 * 
	 * @throws IllegalArgumentException if given date is null or of the form Task.INVALID_DATE_FIELD. 
	 * Also throw if start date is after end date
	 */


	public boolean isWithinDateRange(DateTime startRange, DateTime endRange) {
		
		if((startRange == null) 
				|| (startRange.equals(INVALID_DATE_FIELD))
				|| (endRange == null) 
				|| (endRange.equals(INVALID_DATE_FIELD))
				|| (startRange.isAfter(endRange))) {
			throw new IllegalArgumentException();
		}

		if(this.isFloatingTask()) {
			return false;
		}
		DateTime currentTaskDate = comparisonDate(this);

		if(currentTaskDate.isEqual(startRange)) {
			return true;
		}
		if(currentTaskDate.isEqual(endRange)) {
			return true;
		}


		if(startRange.isBefore(currentTaskDate) && currentTaskDate.isBefore(endRange))	{
			return true;
		}



		return false;
	}
	
	/**
	 * See if time of the task overlaps with the given range inclusive. 
	 * For timed tasks, both the start and end dates are used to establish the existence of an overlap.
	 * 
	 * @param startRange start of time range
	 * @param endRange end of time range
	 * @return true if the task overlaps with this range
	 * 
	 * @throws IllegalArgumentException if given date is null or of the form Task.INVALID_DATE_FIELD. 
	 * Also throw if start date is after end date
	 */
	
	public boolean clashesWithRange(DateTime startRange, DateTime endRange) {
		if((startRange == null) 
				|| (startRange.equals(INVALID_DATE_FIELD))
				|| (endRange == null) 
				|| (endRange.equals(INVALID_DATE_FIELD))
				|| (startRange.isAfter(endRange))) {
			throw new IllegalArgumentException();
		}
		
		
		if(this.isFloatingTask()) {
			return false;
		}
		
		if(this.isDeadlineTask()) {
			if(deadline.isEqual(startRange) || deadline.isEqual(endRange)) {
				return true;
			}

			if(startRange.isBefore(deadline) && deadline.isBefore(endRange))	{
				return true;
			}
		}
		
		if(this.isTimedTask()) {
			if(startDate.equals(startRange)
					|| startDate.equals(endRange)
					|| endDate.equals(startRange)
					|| endDate.equals(endRange)){
				return true;
			}
			
			if(startDate.isBefore(startRange) && endDate.isAfter(startRange)){
				return true;
			}
			
			if(startDate.isAfter(startRange) && startDate.isBefore(endRange)) {
				return true;
			}

		}
		
		
		
		return false;
		
	}


	private DateTime comparisonDate(Task dateCompare){
		if(dateCompare.isFloatingTask()) {
			return Task.INVALID_DATE_FIELD;
		}

		if(dateCompare.isDeadlineTask()) {
			return dateCompare.getDeadline();
		}
		else {
			return dateCompare.getStartDate();
		}
	}



	public static class SortByType implements Comparator<Task> {
		//Order of priority to be shown is Deadline, Timed and Floating.

		public int compare(Task o1, Task o2) {

			TaskType o1Type = o1.getType();
			TaskType o2Type = o2.getType();

			if(o1Type.equals(o2Type)) {
				return o1.compareTo(o2);
			}

			if(o1Type.equals(TaskType.FLOATING)) {
				return COMPARETO_BIGGER;
			}

			if(o2Type.equals(TaskType.FLOATING)) {
				return COMPARETO_SMALLER;
			}

			if(o1Type.equals(TaskType.DEADLINE) && o2Type.equals(TaskType.TIMED)){
				return COMPARETO_SMALLER;
			} else {
				return COMPARETO_BIGGER;
			}

		}
	}

	public static class SortByDone implements Comparator<Task> {

		public int compare(Task o1, Task o2) {

			boolean o1Done = o1.isDone();
			boolean o2Done = o2.isDone();

			if(o1Done == o2Done) {
				return o1.compareTo(o2);
			}

			if((o1Done == true) && (o2Done == false)) {
				return COMPARETO_BIGGER;
			} else {
				return COMPARETO_SMALLER;
			}
		}
	}

	public static class SortByStartDate implements Comparator<Task> {

		public int compare(Task o1, Task o2) {

			if(o1.isFloatingTask() && o2.isFloatingTask()) {
				return o1.compareTo(o2);
			}

			if(o1.isFloatingTask()) {
				return COMPARETO_BIGGER;
			}

			if(o2.isFloatingTask()) {
				return COMPARETO_SMALLER;
			}

			DateTime o1Start;
			DateTime o2Start;

			if(o1.isTimedTask()) {
				o1Start = o1.getStartDate();
			} else {
				o1Start = o1.getDeadline();
			}

			if(o2.isTimedTask()) {
				o2Start = o2.getStartDate();
			} else {
				o2Start = o2.getDeadline();
			}
			
			int dateCompare = o1Start.compareTo(o2Start);

			if(dateCompare == COMPARETO_EQUAL){
				return o1.getTaskName().compareToIgnoreCase(o2.getTaskName());
			} else {
				return dateCompare;
			}
		}
	}

	public static class SortByEndDate implements Comparator<Task> {

		public int compare(Task o1, Task o2) {

			if(o1.isFloatingTask() && o2.isFloatingTask()) {
				return o1.compareTo(o2);
			}

			if(o1.isFloatingTask()) {
				return COMPARETO_BIGGER;
			}

			if(o2.isFloatingTask()) {
				return COMPARETO_SMALLER;
			}

			DateTime o1End;
			DateTime o2End;

			if(o1.isTimedTask()) {
				o1End = o1.getEndDate();
			} else {
				o1End = o1.getDeadline();
			}

			if(o2.isTimedTask()) {
				o2End = o2.getEndDate();
			} else {
				o2End = o2.getDeadline();
			}

			int dateCompare = o1End.compareTo(o2End);

			if(dateCompare == COMPARETO_EQUAL){
				return o1.getTaskName().compareToIgnoreCase(o2.getTaskName());
			} else {
				return dateCompare;
			}
		}
	}

	public static class SortByName implements Comparator<Task> {

		public int compare(Task o1, Task o2) {
			String o1Name = o1.getTaskName();
			String o2Name = o2.getTaskName();

			return o1Name.compareToIgnoreCase(o2Name);
		}
	}



	/**
	 * DO NOT USE for production. Debugging and testing only. Show Task info in file format
	 *                      
	 *@return task details in database file line format
	 * 
	 */
	public String showInfo() {	

		String FILE_EMPTY_DATE = "----------------------";

		DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormat.forPattern("dd-MMM-yyyy HHmm Z");
		String FILE_PARAM_DELIMITER = " | ";
		String FILE_LINE_FORMAT = "%1$s" + FILE_PARAM_DELIMITER + "%2$s" + FILE_PARAM_DELIMITER + "%3$s" + FILE_PARAM_DELIMITER + "%4$s" + FILE_PARAM_DELIMITER + "%5$s" + FILE_PARAM_DELIMITER + "%6$s";

		String TYPE_FLOATING = "F";
		String TYPE_DEADLINE = "D";
		String TYPE_TIMED = "T";

		String DONE = "*";
		String UNDONE = "-";

		String typeString;
		String doneString;

		if(this.getType().equals(TaskType.TIMED)) {
			typeString = TYPE_TIMED;
		} else if(this.getType().equals(TaskType.DEADLINE)) {
			typeString = TYPE_DEADLINE;
		} else {
			typeString = TYPE_FLOATING;
		}


		if(this.isDone()) {
			doneString = DONE;
		} else {
			doneString = UNDONE;
		}



		String dead;
		String start;
		String end;


		if(this.getStartDate().equals(INVALID_DATE_FIELD)) {
			start = FILE_EMPTY_DATE;
		}

		else start = FILE_DATE_FORMAT.print(this.getStartDate());



		if(this.getEndDate().equals(INVALID_DATE_FIELD))	{
			end = FILE_EMPTY_DATE;
		}

		else end = FILE_DATE_FORMAT.print(endDate);



		if(this.getDeadline().equals(INVALID_DATE_FIELD)) {
			dead = FILE_EMPTY_DATE;
		}

		else dead = FILE_DATE_FORMAT.print(deadline);


		String task = this.getTaskName();


		return String.format(FILE_LINE_FORMAT, typeString, doneString, dead, start, end, task);
	}


















}
