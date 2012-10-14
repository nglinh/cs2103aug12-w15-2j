package storage;
/**  
 * FileManagement.java 
 * A class for managing all read and writes to disk
 * @author  Yeo Kheng Meng
 */ 
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import shared.Task;
import shared.Task.TaskType;


public class FileManagement {

	public static enum FileStatus {	FILE_ALL_OK, FILE_READ_ONLY, FILE_UNUSABLE, 
		FILE_PERMISSIONS_UNKNOWN, FILE_IS_CORRUPT};



		public static String filename = "database.txt";

		private static final String LINE_IGNORE_CHARACTER = "#";
		private static final String LINE_PARAM_DELIMITER_READ = " \\| ";
		private static final String LINE_PARAM_DELIMITER_WRITE = " | ";
		private static final String LINE_EMPTY_DATE = "----------------";

		private static final String LINE_DATE_TIME_FORMAT = "dd-MMM-yyyy HHmm";
		private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormat.forPattern(LINE_DATE_TIME_FORMAT);

		private static final String FILE_LINE_FORMAT = "%1$s" + LINE_PARAM_DELIMITER_WRITE + "%2$s" + LINE_PARAM_DELIMITER_WRITE + "%3$s" + LINE_PARAM_DELIMITER_WRITE + "%4$s" + LINE_PARAM_DELIMITER_WRITE + "%5$s" + LINE_PARAM_DELIMITER_WRITE + "%6$s";

		private static final int LINE_POSITION_TASKTYPE = 0;
		private static final int LINE_POSITION_DONE = 1;
		private static final int LINE_POSITION_DEADLINE_DATE = 2;
		private static final int LINE_POSITION_START_DATE = 3;
		private static final int LINE_POSITION_END_DATE = 4;
		private static final int LINE_POSITION_TASKNAME = 5;

		private static final int LINE_NUM_FIELDS = 6;


		private static final String[] filehelp = {	"##########################################################################################################################################" ,
			"#<Type F/D/T> | <Done U/D> | <Deadline dd-MMM-yyyy HHmm> | <Start dd-MMM-yyyy HHmm> | <End dd-MMM-yyyy HHmm> | <Task>                    #" ,
			"#D | U | 01-Jan-2012 0600 | ---------------- | ---------------- | This is a undone deadline task 0600 on 1st Jan 2012.                   #" ,
			"#T | U | ---------------- | 31-Dec-2012 2359 | 28-Feb-2013 2248 | This is an undone timed task from 2359 31 Dec 2012 to 2248 28 Feb 2013 #" , 
			"#F | D | ---------------- | ---------------- | ---------------- | This is a done floating task.                                          #" ,
			"##########################################################################################################################################"	
		};

		private static final String LINE_FLOATING = "F";
		private static final String LINE_DEADLINE = "D";
		private static final String LINE_TIMED = "T";

		private static final String LINE_DONE = "D";
		private static final String LINE_UNDONE = "U";
		
		private final String LINE_DATE_LONGER_FORMAT = "EEE dd-MMM-yyyy hh:mma";
		private final DateTimeFormatter LINE_DATE_LONGER_FORMATTER = DateTimeFormat.forPattern(LINE_DATE_LONGER_FORMAT);

		private final int ZERO_LENGTH_TASK_NAME = 0;
		
		private FileStatus fileAttributes;
		
		
		public FileManagement(ArrayList<Task> storeInHere)	{
			assert(storeInHere != null);

			fileAttributes = createAndCheckFileAttributes();

			if((fileAttributes.equals(FileStatus.FILE_ALL_OK)) || (fileAttributes.equals(FileStatus.FILE_READ_ONLY))) {
				try {
					readFiletoDataBase(storeInHere);
				} catch (Exception e) {
					fileAttributes = FileStatus.FILE_IS_CORRUPT;
				}
			}
		}
		public FileStatus getFileAttributes()	{
			return fileAttributes;
		}


		private void readFiletoDataBase(ArrayList<Task> storeInHere) throws IOException, DataFormatException {

			String lineFromInput;
			String parsed[] = null;

			BufferedReader inFile  = fileReading();
			while((lineFromInput = inFile.readLine()) != null)	{
				if(lineFromInput.startsWith(LINE_IGNORE_CHARACTER)) continue;

				parsed = lineFromInput.split(LINE_PARAM_DELIMITER_READ, LINE_NUM_FIELDS);

				Task toBeAdded = TaskParser(parsed);

				storeInHere.add(toBeAdded);
			}

		}

		private Task TaskParser(String[] parsed) throws DataFormatException {
			Task parsedTask;

			switch(parsed[LINE_POSITION_TASKTYPE])	{
			case LINE_FLOATING : parsedTask = parseInFloatingTask(parsed);
			break;
			case LINE_DEADLINE : parsedTask = parseInDeadlineTask(parsed);
			break;
			case LINE_TIMED : parsedTask = parseInTimedTask(parsed);
			break;
			default: throw new DataFormatException();

			}

			return parsedTask;

		}

		private Task parseInTimedTask(String[] parsed) throws DataFormatException {

			if(!((parsed[LINE_POSITION_DONE].equals(LINE_DONE)) || (parsed[LINE_POSITION_DONE].equals(LINE_UNDONE)))) {
				throw new DataFormatException();
			}
			
			DateTime startDate = parseDate(parsed[LINE_POSITION_START_DATE]); 
			DateTime endDate = parseDate(parsed[LINE_POSITION_END_DATE]); 

			String taskName = parsed[LINE_POSITION_TASKNAME];
			if(taskName.length() == ZERO_LENGTH_TASK_NAME) {
				throw new DataFormatException();
			}
			
			boolean done;

			if(parsed[LINE_POSITION_DONE] == LINE_DONE) {
				done = true;
			} else {
				done = false;
			}



			return new Task(taskName, startDate, endDate, done);
		}


		private Task parseInDeadlineTask(String[] parsed) throws DataFormatException {

			if(!((parsed[LINE_POSITION_DONE].equals(LINE_DONE)) || (parsed[LINE_POSITION_DONE].equals(LINE_UNDONE)))) {
				throw new DataFormatException();
			}
			
			DateTime deadline = parseDate(parsed[LINE_POSITION_DEADLINE_DATE]);
			String taskName = parsed[LINE_POSITION_TASKNAME];
			
			if(taskName.length() == ZERO_LENGTH_TASK_NAME) {
				throw new DataFormatException();
			}

			boolean done;

			if(parsed[LINE_POSITION_DONE] == LINE_DONE) {
				done = true;
			} else {
				done = false;
			}

			return new Task(taskName, deadline, done);
		}

		private Task parseInFloatingTask(String[] parsed) throws DataFormatException {

			if(!((parsed[LINE_POSITION_DONE].equals(LINE_DONE)) || (parsed[LINE_POSITION_DONE].equals(LINE_UNDONE)))) {
				throw new DataFormatException();
			}
			
			String taskName = parsed[LINE_POSITION_TASKNAME];
			if(taskName.length() == ZERO_LENGTH_TASK_NAME) {
				throw new DataFormatException();
			}
			
			boolean done;

			if(parsed[LINE_POSITION_DONE] == LINE_DONE) {
				done = true;
			} else {
				done = false;
			}


			return new Task(taskName, done);
		}


		private DateTime parseDate(String parsed) throws DataFormatException {
			DateTime parsedDate = null;
			
			try {
				parsedDate = new DateTime(FILE_DATE_FORMAT.parseDateTime(parsed));
			} catch (IllegalArgumentException e) {
				throw new DataFormatException();
			}
			
			return parsedDate;
		}


		private String TaskToDatabaseString(Task toBeConverted) {	



			String typeString;

			TaskType typeOfIncomingTask = toBeConverted.getType();


			if(typeOfIncomingTask.equals(TaskType.TIMED)) {
				typeString = LINE_TIMED;
			} else if(typeOfIncomingTask.equals(TaskType.DEADLINE)) {
				typeString = LINE_DEADLINE;
			} else {
				typeString = LINE_FLOATING;
			}


			String doneString;
			boolean isIncomingTaskComplete = toBeConverted.isDone();

			if(isIncomingTaskComplete) {
				doneString = LINE_DONE;
			} else {
				doneString = LINE_UNDONE;
			}

			String deadline = getTimeFileFormat(toBeConverted.getDeadline());
			String start = getTimeFileFormat(toBeConverted.getStartTime());
			String end = getTimeFileFormat(toBeConverted.getEndTime());

			String task = toBeConverted.getTaskName();

			return String.format(FileManagement.FILE_LINE_FORMAT, typeString, doneString, deadline, start, end, task);
		}


		private String getTimeFileFormat(DateTime toBeConverted)	{
			if(Task.INVALID_DATE_FIELD.isEqual(toBeConverted))	{
				return LINE_EMPTY_DATE;
			}

			return FILE_DATE_FORMAT.print(toBeConverted);
		}



		public boolean canWriteFile() {
			try	{
				BufferedWriter out = new BufferedWriter(new FileWriter(filename, true));
				out.close();
			}
			catch(Exception e)	{
				return false;
			}

			return true;
		}

		public void writeDataBaseToFile(ArrayList<Task> toBeWritten) throws IOException, WillNotWriteToCorruptFileException	{
			assert(toBeWritten != null);

			if(fileAttributes.equals(FileStatus.FILE_IS_CORRUPT)) {
				throw new WillNotWriteToCorruptFileException();
			}
			
			BufferedWriter out = new BufferedWriter(new FileWriter(filename));

			for(String helpline : filehelp)	{
				out.write(helpline);
				out.newLine();
			}

			for(Task temp : toBeWritten) {
				out.write(TaskToDatabaseString(temp));			
				out.newLine();
			}
			
			String currentTime = LINE_DATE_LONGER_FORMATTER.print(new DateTime());
			
			out.write("#Last Modified: " + currentTime);
			out.newLine();

			out.close();
		}




		private FileStatus createAndCheckFileAttributes() {

			FileStatus attributes = FileStatus.FILE_PERMISSIONS_UNKNOWN;

			File databaseFile = new File(filename);


			try {

				if(!databaseFile.exists()) {
					databaseFile.createNewFile();
				}


				if(isFileReadable(databaseFile) && isFileWritable(databaseFile)) {
					attributes = FileStatus.FILE_ALL_OK;
				} else if(isFileReadable(databaseFile)){
					attributes = FileStatus.FILE_READ_ONLY;
				} else {
					attributes = FileStatus.FILE_UNUSABLE;
				}


			} catch (IOException e) {
				attributes = FileStatus.FILE_UNUSABLE;
			} catch (SecurityException e) {
				attributes = FileStatus.FILE_PERMISSIONS_UNKNOWN;
			}

			return attributes;
		}

		private static BufferedReader fileReading() throws FileNotFoundException {
			return new BufferedReader(new FileReader(filename));
		}

		private boolean isFileWritable(File databaseFile) {
			try	{
				BufferedWriter out = new BufferedWriter(new FileWriter(filename,true));
				out.close();
				return true;
			}
			catch(Exception e) {
				return false;
			}
		}

		private boolean isFileReadable(File databaseFile) {
			return databaseFile.canRead();
		}
		
		public boolean isFileCorrupt() {
			if(fileAttributes.equals(FileStatus.FILE_IS_CORRUPT)) {
				return true;
			} else {
				return false;
			}
		}
}
