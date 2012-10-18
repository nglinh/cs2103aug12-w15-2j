package storage;
/**  
 * FileManagement.java 
 * A class for managing all read and writes to disk. Will lock the database file during the duration of the program run
 * @author  Yeo Kheng Meng
 */ 
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringReader;

import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import java.util.ArrayList;
import java.util.zip.DataFormatException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import shared.Task;
import shared.Task.TaskType;


public class FileManagement {

	


	public static enum FileStatus {	FILE_ALL_OK, FILE_READ_ONLY, 
		FILE_PERMISSIONS_UNKNOWN, FILE_IS_CORRUPT, FILE_IS_LOCKED};



		public static String filename = "database.txt";

		private static final String LINE_IGNORE_CHARACTER = "#";
		private static final String LINE_PARAM_DELIMITER_READ = " \\| ";
		private static final String LINE_PARAM_DELIMITER_WRITE = " | ";
		private static final String LINE_EMPTY_DATE = "----------------";

		private static final String LINE_DATE_TIME_FORMAT = "dd-MMM-yyyy HHmm";
		private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormat.forPattern(LINE_DATE_TIME_FORMAT);

		private static final String FILE_LINE_FORMAT = "%1$3d" + LINE_PARAM_DELIMITER_WRITE + "%2$s" + LINE_PARAM_DELIMITER_WRITE + "%3$s" + LINE_PARAM_DELIMITER_WRITE + "%4$s" + LINE_PARAM_DELIMITER_WRITE + "%5$s" + LINE_PARAM_DELIMITER_WRITE + "%6$s" + LINE_PARAM_DELIMITER_WRITE + "%7$s";

//		private static final int LINE_POSITION_TASKINDEX = 0; //To indicate that position 0 is task index
		private static final int LINE_POSITION_TASKTYPE = 1;
		private static final int LINE_POSITION_DONE = 2;
		private static final int LINE_POSITION_DEADLINE_DATE = 3;
		private static final int LINE_POSITION_START_DATE = 4;
		private static final int LINE_POSITION_END_DATE = 5;
		private static final int LINE_POSITION_TASKNAME = 6;

		private static final int LINE_NUM_FIELDS = 7;


		private static final String[] filehelp = {
			"##############################################################################################################################################" ,
			"# Ref| Type | Done |     Deadline     |       Start      |       End        |                                  Task                          #" ,
			"#  1 |  D   |   D  | 01-Jan-2012 0600 | ---------------- | ---------------- | An undone deadline task by 0600 1st Jan 2012                   #" ,
			"#  2 |  T   |   U  | ---------------- | 31-Dec-2012 2359 | 28-Feb-2013 2248 | An undone timed task from 2359 31 Dec 2012 to 2248 28 Feb 2013 #" , 
			"#  3 |  F   |   D  | ---------------- | ---------------- | ---------------- | A done floating task                                           #" ,
			"#The reference number is not used in the parsing process. DoIt will ignore non-consecutive or wrong reference numbers.                       #" ,
			"##############################################################################################################################################"	
		};

		private static final String LINE_FLOATING = "F";
		private static final String LINE_DEADLINE = "D";
		private static final String LINE_TIMED = "T";

		private static final String LINE_DONE = "D";
		private static final String LINE_UNDONE = "U";

		private final String LINE_DATE_LONGER_FORMAT = "EEE dd-MMM-yyyy hh:mma";
		private final DateTimeFormatter LINE_DATE_LONGER_FORMATTER = DateTimeFormat.forPattern(LINE_DATE_LONGER_FORMAT);

		private static final String LINE_LAST_MODIFIED = "#Last Modified: %1$s";
		
		private static final String LINE_END_OF_LINE = System.getProperty( "line.separator" );
	
		private final int ZERO_LENGTH_TASK_NAME = 0;
		
		private final int START_OF_FILE = 0;

		private FileStatus fileAttributes = FileStatus.FILE_PERMISSIONS_UNKNOWN;

		File databaseFile = new File(filename);
		FileLock databaseFileLock;
		FileChannel databaseChannel;
		RandomAccessFile randDatabaseAccess;

		public FileManagement(ArrayList<Task> storeInHere)	{
			assert(storeInHere != null);

			prepareDatabaseFile();

			if((fileAttributes.equals(FileStatus.FILE_ALL_OK)) || (fileAttributes.equals(FileStatus.FILE_READ_ONLY))) {
				try {
					readFiletoDataBase(storeInHere);
				} catch (Exception e) {
					fileAttributes = FileStatus.FILE_IS_CORRUPT;
				}
			}


		}
		public void prepareDatabaseFile() {

			boolean isRWLockSucessful = true;
			try {
				randDatabaseAccess = new RandomAccessFile(databaseFile, "rws");
				databaseChannel = randDatabaseAccess.getChannel();
				databaseFileLock = databaseChannel.tryLock();
				
				if(databaseFileLock == null) {
					fileAttributes = FileStatus.FILE_IS_LOCKED;
					isRWLockSucessful = false;
				} else {
					fileAttributes = FileStatus.FILE_ALL_OK;
				}
			} catch (IOException e) {
				isRWLockSucessful = false;
			}

			if(isRWLockSucessful || fileAttributes.equals(FileStatus.FILE_IS_LOCKED)) {
				return;
			}

			try {
				randDatabaseAccess = new RandomAccessFile(databaseFile, "r");
				databaseChannel = randDatabaseAccess.getChannel();
				fileAttributes = FileStatus.FILE_READ_ONLY;
			} catch (IOException e) {
				fileAttributes = FileStatus.FILE_PERMISSIONS_UNKNOWN;
			}

		}

		public FileStatus getFileAttributes()	{
			return fileAttributes;
		}


		private void readFiletoDataBase(ArrayList<Task> storeInHere) throws IOException, DataFormatException {

			int fileSize = (int) databaseFile.length();
			byte[] fileByteContents = new byte[fileSize];

			randDatabaseAccess.seek(START_OF_FILE);
			randDatabaseAccess.read(fileByteContents);

			String fileInStringFormat = new String(fileByteContents);
			BufferedReader fileStringReader = new BufferedReader(new StringReader(fileInStringFormat));


			String lineFromInput;
			String parsed[] = null;

			while((lineFromInput = fileStringReader.readLine()) != null)	{
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

			String taskName = parseTaskName(parsed[LINE_POSITION_TASKNAME]);
			boolean done = retrieveTaskDoneStatus(parsed[LINE_POSITION_DONE]);
			DateTime startDate = parseDate(parsed[LINE_POSITION_START_DATE]); 
			DateTime endDate = parseDate(parsed[LINE_POSITION_END_DATE]); 

			if(startDate.isAfter(endDate)) {
				throw new DataFormatException();
			}

			return new Task(taskName, startDate, endDate, done);
		}


		private String parseTaskName(String taskName)
				throws DataFormatException {

			if(taskName.length() == ZERO_LENGTH_TASK_NAME) {
				throw new DataFormatException();
			}
			return taskName;
		}


		private Task parseInDeadlineTask(String[] parsed) throws DataFormatException {

			String taskName = parseTaskName(parsed[LINE_POSITION_TASKNAME]);		
			boolean done = retrieveTaskDoneStatus(parsed[LINE_POSITION_DONE]);
			DateTime deadline = parseDate(parsed[LINE_POSITION_DEADLINE_DATE]);

			return new Task(taskName, deadline, done);
		}

		private Task parseInFloatingTask(String[] parsed) throws DataFormatException {

			String taskName = parseTaskName(parsed[LINE_POSITION_TASKNAME]);
			boolean done = retrieveTaskDoneStatus(parsed[LINE_POSITION_DONE]);

			return new Task(taskName, done);
		}

		private boolean retrieveTaskDoneStatus(String parsed)
				throws DataFormatException {
			boolean done;

			if(parsed.equals(LINE_DONE)) {
				done = true;
			} else if(parsed.equals(LINE_UNDONE)){
				done = false;
			} else {
				throw new DataFormatException();
			}
			return done;
		}


		private DateTime parseDate(String date) throws DataFormatException {
			DateTime parsedDate = null;

			try {
				parsedDate = new DateTime(FILE_DATE_FORMAT.parseDateTime(date));
			} catch (IllegalArgumentException e) {
				throw new DataFormatException();
			}

			return parsedDate;
		}


		private String taskToDatabaseString(Task toBeConverted, int index) {	



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
			String start = getTimeFileFormat(toBeConverted.getStartDate());
			String end = getTimeFileFormat(toBeConverted.getEndDate());

			String task = toBeConverted.getTaskName();

			return String.format(FileManagement.FILE_LINE_FORMAT, index, typeString, doneString, deadline, start, end, task);
		}


		private String getTimeFileFormat(DateTime toBeConverted)	{
			if(Task.INVALID_DATE_FIELD.isEqual(toBeConverted))	{
				return LINE_EMPTY_DATE;
			}

			return FILE_DATE_FORMAT.print(toBeConverted);
		}


		public void writeDataBaseToFile(ArrayList<Task> toBeWritten) throws IOException, WillNotWriteToCorruptFileException	{
			assert(toBeWritten != null);

			if(fileAttributes.equals(FileStatus.FILE_IS_CORRUPT)) {
				throw new WillNotWriteToCorruptFileException();
			}

			if(randDatabaseAccess == null) {
				throw new IOException();
			}

			StringBuffer dataToBeWritten = new StringBuffer();

			for(String helpline : filehelp)	{
				dataToBeWritten.append(helpline + LINE_END_OF_LINE);
			}

			int index = 1; //Start index number from 1

			for(Task temp : toBeWritten) {
				dataToBeWritten.append(taskToDatabaseString(temp, index) + LINE_END_OF_LINE);
				index++;
			}

			String currentTime = LINE_DATE_LONGER_FORMATTER.print(new DateTime());

			String appendLastModified = String.format(LINE_LAST_MODIFIED, currentTime);
			dataToBeWritten.append(appendLastModified + LINE_END_OF_LINE);

			String dataStringToBeWritten = dataToBeWritten.toString();

			randDatabaseAccess.seek(START_OF_FILE);
			randDatabaseAccess.writeBytes(dataStringToBeWritten);

		}


}
