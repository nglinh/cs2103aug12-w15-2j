package shared;
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

	private DateTime startTime = INVALID_DATE_FIELD;
	private DateTime endTime = INVALID_DATE_FIELD;
	private DateTime deadline = INVALID_DATE_FIELD;


	private boolean isCompleted = false;

	/**
	 * To instantiate an undone floating task                        
	 *
	 * @param name the task description
	 */
	
	public Task(String name) {
		
		assert(name != null);
		
		this.type = TaskType.FLOATING;
		this.taskName = name;

		serial = nextSerial;
		nextSerial++;
	}

	/**
	 * To instantiate a floating task with known done value.                     
	 *
	 * @param name the task description
	 * @param done the done value.
	 */

	public Task(String name, boolean done)	{
		
		assert(name != null);

		this.type = TaskType.FLOATING;
		this.taskName = name;
		this.isCompleted = done;

		serial = nextSerial;
		nextSerial++;
	}

	/**
	 * To instantiate an undone deadline task.                     
	 *
	 * @param name the task description
	 * @param deadline the deadline in Joda DateTime form
	 */

	public Task(String name, DateTime deadline) {
		
		assert(name != null);
		
		assert(deadline != null);
		assert(!deadline.equals(INVALID_DATE_FIELD));
		
		this.type = TaskType.DEADLINE;
		this.taskName = name;
		this.deadline = deadline; 

		serial = nextSerial;
		nextSerial++;

	}

	/**
	 * To instantiate a deadline task with known done value.                     
	 *
	 * @param name the task description
	 * @param deadline the deadline in Joda DateTime form
	 * @param done the done value.
	 */

	public Task(String name, DateTime deadline, boolean done) {
		
		assert(name != null);
		
		assert(deadline != null);
		assert(!deadline.isEqual(INVALID_DATE_FIELD));
		
		
		this.type = TaskType.DEADLINE;
		this.taskName = name;
		this.deadline = deadline; 
		this.isCompleted = done;

		serial = nextSerial;
		nextSerial++;

	}
	
	/**
	 * To instantiate an undone timed task.
	 * <p>
	 * Accepts case where start and end time are the same                     
	 *
	 * @param name the task description
	 * @param startTime the start time and date in Joda DateTime form
	 * @param endTime the end time and date in Joda DateTime form
	 */

	public Task(String name, DateTime startTime, DateTime endTime) {
		
		assert(name != null);
		
		assert(startTime != null);
		assert(!startTime.equals(INVALID_DATE_FIELD));
		
		assert(endTime != null);
		assert(!endTime.equals(INVALID_DATE_FIELD));
		
		assert(startTime.isBefore(endTime) || startTime.isEqual(endTime));
		
		this.type = TaskType.TIMED;
		this.taskName = name;
		this.startTime = startTime;
		this.endTime = endTime;

		serial = nextSerial;
		nextSerial++;
	}

	/**
	 * To instantiate a timed task with known done value.
	 * <p>
	 * Accepts case where start and end time are the same                      
	 *
	 * @param name the task description
	 * @param startTime the start time and date in Joda DateTime form
	 * @param endTime the end time and date in Joda DateTime form
	 * @param done the done value.
	 */

	public Task(String name, DateTime startTime, DateTime endTime, boolean done) {
		assert(name != null);
		
		assert(startTime != null);
		assert(!startTime.equals(INVALID_DATE_FIELD));
		
		assert(endTime != null);
		assert(!endTime.equals(INVALID_DATE_FIELD));
		
		assert(startTime.isBefore(endTime) || startTime.isEqual(endTime));
		
		this.type = TaskType.TIMED;
		this.taskName = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.isCompleted = done;

		serial = nextSerial;
		nextSerial++;
	}
	
	
	/**
	 * To clone the incoming task of all details including serial number      
	 *
	 * @param toBeCloned the new task to be cloned
	 */
	public Task(Task toBeCloned) {
		assert(toBeCloned != null);
		
		this.updateOrClone(toBeCloned);
	}

	/**
	 * To clone the current task given to this object.                           
	 *
	 * All the fields including serial number 
	 * of the parameter task will be copied to this object
	 *
	 * @param updated Task to be cloned           
	 * 
	 */

	public void updateOrClone(Task updated) {
		
		assert(updated != null);

		this.type = updated.getType();
		this.taskName = updated.getTaskName();
		this.isCompleted = updated.isDone();

		this.serial = updated.getSerial();

		if(!updated.getStartTime().equals(INVALID_DATE_FIELD)) {
			this.startTime = new DateTime(updated.getStartTime());
		}

		if(!updated.getEndTime().equals(INVALID_DATE_FIELD))	{
			this.endTime = new DateTime(updated.getEndTime());
		}

		if(!updated.getDeadline().equals(INVALID_DATE_FIELD))	{
			this.deadline = new DateTime(updated.getDeadline());
		}
	}

	/**
	 * To check if the Task is completed                        
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

	public DateTime getStartTime()	{
		return startTime;
	}
	
	/**
	 * Get end time of timed task                           
	 *
	 * @return end time if valid or Task.INVALID_DATE_FIELD if not a timed task.           
	 *  
	 */


	public DateTime getEndTime(){
		return endTime;
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



	public void done()	{
		this.isCompleted = true;
	}

	public void undone() {
		this.isCompleted = false;
	}

	public void changetoFloating() {
		this.type = TaskType.FLOATING;

		this.deadline = INVALID_DATE_FIELD;
		this.startTime = INVALID_DATE_FIELD;
		this.endTime = INVALID_DATE_FIELD;
	}

	public void changetoDeadline(DateTime newDeadline)	{
		
		assert(deadline != null);
		assert(!deadline.equals(INVALID_DATE_FIELD));
		
		this.type = TaskType.DEADLINE;

		this.deadline = newDeadline;

		this.startTime = INVALID_DATE_FIELD;
		this.endTime = INVALID_DATE_FIELD;

	}

	public void changetoTimed(DateTime newStartTime, DateTime newEndTime)	{
		assert(newStartTime != null);
		assert(!newStartTime.equals(INVALID_DATE_FIELD));
		
		assert(newEndTime != null);
		assert(!newEndTime.equals(INVALID_DATE_FIELD));
		
		
		this.type = TaskType.TIMED;

		this.startTime = newStartTime;
		this.endTime = newEndTime;

		this.deadline = INVALID_DATE_FIELD;

	}




	public void changeName(String newName)	{
		assert(newName != null);
		
		this.taskName = newName;
	}

	public void changeStartAndEndTime(DateTime newStartTime, DateTime newEndTime)	{
		assert(newStartTime != null);
		assert(!newStartTime.equals(INVALID_DATE_FIELD));
		
		assert(newEndTime != null);
		assert(!newEndTime.equals(INVALID_DATE_FIELD));
		
		assert(startTime.isBefore(newEndTime) || startTime.isEqual(newEndTime));
		
		this.startTime = newStartTime;
		this.endTime = newEndTime;
	}


	public void changeDeadline(DateTime newDeadline) {
		assert(deadline != null);
		assert(!deadline.equals(INVALID_DATE_FIELD));
		
		this.deadline = newDeadline;
	}





	@Override
	public int compareTo(Task input) {

		assert(input != null);
		
		if(this.isFloatingTask() && input.isFloatingTask()) {
			return COMPARETO_EQUAL;
		}

		//Floating Tasks are bigger than every other tasks to appear at the bottom
		if(this.isFloatingTask()) {
			return COMPARETO_BIGGER;
		}


		if(input.isFloatingTask())	{
			return COMPARETO_SMALLER;
		}

		DateTime currentTaskDate;
		DateTime inputTaskDate;

		if(this.isDeadlineTask()) {
			currentTaskDate = this.getDeadline();
		}
		else {
			currentTaskDate = this.getStartTime();
		}


		if(input.isDeadlineTask())	{
			inputTaskDate = input.getDeadline();
		}
		else {
			inputTaskDate = input.getStartTime();
		}


		return currentTaskDate.compareTo(inputTaskDate);

	}

	public boolean searchName(String term)	{
		assert(term != null);
		
		String taskNameLowerCase = taskName.toLowerCase();
		String termLowerCase = term.toLowerCase();
		
		if(taskNameLowerCase.contains(termLowerCase))	{
			return true;
		} else {
			return false;
		}
	}


	public boolean searchDateRange(DateTime startRange, DateTime endRange) {

		assert(startRange != null);
		assert(!startRange.equals(INVALID_DATE_FIELD));
		
		assert(endRange != null);
		assert(!endRange.equals(INVALID_DATE_FIELD));
		
		assert(startRange.isBefore(endRange) || startRange.isEqual(endRange));
		
		
		DateTime currentTaskDate;

		if(this.isDeadlineTask()) {
			currentTaskDate = this.getDeadline();
		}
		else {
			currentTaskDate = this.getStartTime();
		}


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
	
	
	
	static class SortByType implements Comparator<Task> {
		//Order of priority to be shown is Deadline, Timed and Floating.
		
		public int compare(Task o1, Task o2) {
			
			TaskType o1Type = o1.getType();
			TaskType o2Type = o2.getType();
			
			if(o1Type.equals(o2Type)) {
				return COMPARETO_EQUAL;
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
	
	static class SortByDone implements Comparator<Task> {

		public int compare(Task o1, Task o2) {
			
			boolean o1Done = o1.isDone();
			boolean o2Done = o2.isDone();
			
			if(o1Done == o2Done) {
				return COMPARETO_EQUAL;
			}
			
			if((o1Done == true) && (o2Done == false)) {
				return COMPARETO_BIGGER;
			} else {
				return COMPARETO_SMALLER;
			}
		}
	}
	
	static class SortByStartDate implements Comparator<Task> {

		public int compare(Task o1, Task o2) {
			
			if(o1.isFloatingTask()) {
				return COMPARETO_BIGGER;
			}
			
			if(o1.isFloatingTask()) {
				return COMPARETO_SMALLER;
			}
			
			DateTime o1Start;
			DateTime o2Start;
			
			if(o1.isTimedTask()) {
				o1Start = o1.getStartTime();
			} else {
				o1Start = o1.getDeadline();
			}
			
			if(o2.isTimedTask()) {
				o2Start = o2.getStartTime();
			} else {
				o2Start = o2.getDeadline();
			}
			
			
			return o1Start.compareTo(o2Start);
		}
	}
	
	static class SortByEndDate implements Comparator<Task> {

		public int compare(Task o1, Task o2) {
			
			if(o1.isFloatingTask()) {
				return COMPARETO_BIGGER;
			}
			
			if(o1.isFloatingTask()) {
				return COMPARETO_SMALLER;
			}
			
			DateTime o1End;
			DateTime o2End;
			
			if(o1.isTimedTask()) {
				o1End = o1.getEndTime();
			} else {
				o1End = o1.getDeadline();
			}
			
			if(o2.isTimedTask()) {
				o2End = o2.getEndTime();
			} else {
				o2End = o2.getDeadline();
			}
			
			return o1End.compareTo(o2End);
		}
	}
	
	static class SortByDescription implements Comparator<Task> {

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

		String FILE_EMPTY_DATE = "----------------";

		DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormat.forPattern("dd-MMM-yyyy HHmm");
		String FILE_PARAM_DELIMITER = " | ";
		String FILE_LINE_FORMAT = "%1$s" + FILE_PARAM_DELIMITER + "%2$s" + FILE_PARAM_DELIMITER + "%3$s" + FILE_PARAM_DELIMITER + "%4$s" + FILE_PARAM_DELIMITER + "%5$s" + FILE_PARAM_DELIMITER + "%6$s";

		String TYPE_FLOATING = "F";
		String TYPE_DEADLINE = "D";
		String TYPE_TIMED = "T";
	
		String DONE = "D";
		String UNDONE = "U";
		
		String typeString;
		String doneString;
		
		if(type.equals(TaskType.TIMED)) {
			typeString = TYPE_TIMED;
		} else if(type.equals(TaskType.DEADLINE)) {
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


		if(startTime.equals(INVALID_DATE_FIELD)) {
			start = FILE_EMPTY_DATE;
		}

		else start = FILE_DATE_FORMAT.print(startTime);



		if(endTime.equals(INVALID_DATE_FIELD))	{
			end = FILE_EMPTY_DATE;
		}

		else end = FILE_DATE_FORMAT.print(endTime);



		if(deadline.equals(INVALID_DATE_FIELD)) {
			dead = FILE_EMPTY_DATE;
		}

		else dead = FILE_DATE_FORMAT.print(deadline);


		String task = this.getTaskName();


		return String.format(FILE_LINE_FORMAT, typeString, doneString, dead, start, end, task);
	}














}
