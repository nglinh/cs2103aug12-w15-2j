package shared;
/**  
 * Task.java 
 * A class for holding all the information for each Task
 * @author  Yeo Kheng Meng
 */ 



import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class Task implements Comparable<Task> {

	public static final DateTime INVALID_DATE_FIELD = new DateTime(Long.MAX_VALUE);

	public static final String TYPE_FLOATING = "F";
	public static final String TYPE_DEADLINE = "D";
	public static final String TYPE_TIMED = "T";

	public static final String DONE = "D";
	public static final String UNDONE = "U";

	private static final int COMPARETO_SMALLER = -1;
	private static final int COMPARETO_EQUAL = 0;
	private static final int COMPARETO_BIGGER = 1;


	public static int nextSerial = 0; 


	protected int serial; //A unique identifier for each task. Will reset on each new program launch

	protected String type = null;
	protected String taskName = null;

	protected DateTime startTime = null;
	protected DateTime endTime = null;
	protected DateTime deadline = null;


	protected String done = UNDONE;


	public Task(String name) {
		this.type = TYPE_FLOATING;
		this.taskName = name;

		serial = nextSerial;
		nextSerial++;
	}



	public Task(String name, String done)	{
		this.type = TYPE_FLOATING;
		this.taskName = name;
		this.done = done;

		serial = nextSerial;
		nextSerial++;
	}


	public Task(String name, DateTime deadline) {
		this.type = TYPE_DEADLINE;
		this.taskName = name;
		this.deadline = deadline; 

		serial = nextSerial;
		nextSerial++;

	}



	public Task(String name, DateTime deadline, String done) {
		this.type = TYPE_DEADLINE;
		this.taskName = name;
		this.deadline = deadline; 
		this.done = done;

		serial = nextSerial;
		nextSerial++;

	}



	public Task(String input, DateTime startTime, DateTime endTime) {
		
		if(startTime.isAfter(endTime)) {
			throw new IllegalArgumentException();
		}
		
		this.type = TYPE_TIMED;
		this.taskName = input;
		this.startTime = startTime;
		this.endTime = endTime;

		serial = nextSerial;
		nextSerial++;
	}


	public Task(String input, DateTime startTime, DateTime endTime, String done) {
		if(startTime.isAfter(endTime)) {
			throw new IllegalArgumentException();
		}
		
		this.type = TYPE_TIMED;
		this.taskName = input;
		this.startTime = startTime;
		this.endTime = endTime;
		this.done = done;

		serial = nextSerial;
		nextSerial++;
	}


	public Task(Task toBeCloned) {
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

		this.type = updated.getType();
		this.taskName = updated.getTaskName();
		this.done = updated.getDone();

		this.serial = updated.getSerial();

		if(updated.getStartTime() != INVALID_DATE_FIELD) {
			this.startTime = new DateTime(updated.getStartTime());
		}

		if(updated.getEndTime() != INVALID_DATE_FIELD)	{
			this.endTime = new DateTime(updated.getEndTime());
		}

		if(updated.getDeadline() != INVALID_DATE_FIELD)	{
			this.deadline = new DateTime(updated.getDeadline());
		}
	}

	/**
	 * To check if the Task is completed                        
	 *
	 * @return true if task is completed
	 */

	public boolean isDone()	{
		return done.equals(DONE);

	}

	/**
	 * To check if the Task is a Floating Task                       
	 *
	 * @return true if Task is floating
	 */

	public boolean isFloatingTask()	{
		if (this.type.equals(TYPE_FLOATING)) {
			return true;
		}

		return false;
	}

	public boolean isTimedTask() {
		if (this.type.equals(TYPE_TIMED)) {
			return true;
		}

		return false;
	}

	public boolean isDeadlineTask()	{
		if (this.type.equals(TYPE_DEADLINE)) {
			return true;
		}

		return false;
	}

	public int getSerial() {
		return serial;
	}


	public String getType()	{
		return type;
	}

	public String getDone()	{
		return done;
	}

	public String getTaskName()	{
		return taskName;
	}



	public DateTime getStartTime()	{

		if(startTime == null) {
			return INVALID_DATE_FIELD;
		}

		return startTime;
	}


	public DateTime getEndTime(){

		if(endTime == null) {
			return INVALID_DATE_FIELD;
		}

		return endTime;
	}

	public DateTime getDeadline(){
		if(deadline == null) {
			return INVALID_DATE_FIELD;
		}

		return deadline;
	}



	public void done()	{
		this.done = DONE;
	}

	public void undone() {
		this.done = UNDONE;
	}

	public void changetoFloating() {
		this.type = TYPE_FLOATING;

		this.deadline = null;
		this.startTime = null;
		this.endTime = null;
	}

	public void changetoDeadline(DateTime newDeadline)	{
		this.type = TYPE_DEADLINE;

		this.deadline = newDeadline;

		this.startTime = null;
		this.endTime = null;

	}

	public void changetoTimed(DateTime newStartTime, DateTime newEndTime)	{
		this.type = TYPE_TIMED;

		this.startTime = newStartTime;
		this.endTime = newEndTime;

		this.deadline = null;

	}




	public void changeName(String newName)	{
		this.taskName = newName;
	}

	public void changeStartTime(DateTime newStart)	{
		this.startTime = newStart;
	}

	public void changeEndTime(DateTime newEnd)	{	
		this.endTime = newEnd;
	}

	public void changeDeadline(DateTime newDeadline) {
		this.deadline = newDeadline;
	}





	@Override
	public int compareTo(Task input) {

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
		String taskNameLowerCase = taskName.toLowerCase();
		String termLowerCase = term.toLowerCase();
		
		if(taskNameLowerCase.contains(termLowerCase))	{
			return true;
		} else {
			return false;
		}
	}


	public boolean searchDateRange(DateTime startRange, DateTime endRange) {

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

	//For debugging purposes only. DO NOT USE for production
	public String showInfo() {	

		String FILE_EMPTY_DATE = "----------------";

		DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormat.forPattern("dd-MMM-yyyy HHmm");
		String FILE_PARAM_DELIMITER = " | ";
		String FILE_LINE_FORMAT = "%1$s" + FILE_PARAM_DELIMITER + "%2$s" + FILE_PARAM_DELIMITER + "%3$s" + FILE_PARAM_DELIMITER + "%4$s" + FILE_PARAM_DELIMITER + "%5$s" + FILE_PARAM_DELIMITER + "%6$s";

		String type = this.getType();
		String done = this.getDone();

		String dead;
		String start;
		String end;


		if(startTime == null) {
			start = FILE_EMPTY_DATE;
		}

		else start = FILE_DATE_FORMAT.print(startTime);



		if(endTime == null)	{
			end = FILE_EMPTY_DATE;
		}

		else end = FILE_DATE_FORMAT.print(endTime);



		if(deadline == null) {
			dead = FILE_EMPTY_DATE;
		}

		else dead = FILE_DATE_FORMAT.print(deadline);




		String task = this.getTaskName();


		return String.format(FILE_LINE_FORMAT, type, done, dead, start, end, task);
	}












}
