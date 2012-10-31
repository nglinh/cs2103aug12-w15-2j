package main.storage;
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

import java.util.List;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import main.LogHandler;
import main.shared.Task;
import main.shared.Task.TaskType;


public class FileManagement {

	public enum FileStatus {	FILE_ALL_OK, FILE_READ_ONLY, 
		FILE_PERMISSIONS_UNKNOWN, FILE_IS_CORRUPT, FILE_IS_LOCKED};



		public final String filename = "database.txt";
		
		private final int FILE_INDEX_START = 1;

		private final String LINE_IGNORE_CHARACTER = "#";
		private final String LINE_PARAM_DELIMITER_READ = " \\| ";
		private final String LINE_PARAM_DELIMITER_WRITE = " | ";
		private final String LINE_EMPTY_DATE = "----------------------";

		private final String LINE_DATE_TIME_FORMAT = "dd-MMM-yyyy HHmm Z";
		private final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormat.forPattern(LINE_DATE_TIME_FORMAT);

		private final String FILE_LINE_FORMAT = "%1$3d" + LINE_PARAM_DELIMITER_WRITE + "%2$s" + LINE_PARAM_DELIMITER_WRITE + "%3$s" + LINE_PARAM_DELIMITER_WRITE + "%4$s" + LINE_PARAM_DELIMITER_WRITE + "%5$s" + LINE_PARAM_DELIMITER_WRITE + "%6$s" + LINE_PARAM_DELIMITER_WRITE + "%7$s";

//		private final int LINE_POSITION_TASKINDEX = 0; //To indicate that position 0 is task index
		private final int LINE_POSITION_TASKTYPE = 1;
		private final int LINE_POSITION_DONE = 2;
		private final int LINE_POSITION_DEADLINE_DATE = 3;
		private final int LINE_POSITION_START_DATE = 4;
		private final int LINE_POSITION_END_DATE = 5;
		private final int LINE_POSITION_TASKNAME = 6;

		private final int LINE_NUM_FIELDS = 7;


		private final String[] filehelp = {
			"################################################################################################################################################################" ,
			"# Ref| Type | Done |        Deadline        |          Start         |          End           |                                  Task                          #" ,
			"#  1 |  D   |   *  | 01-Jan-2012 0600 +0800 | ---------------------- | ---------------------- | A done deadline task by 0600 1st Jan 2012                      #" ,
			"#  2 |  T   |   -  | ---------------------- | 31-Dec-2012 2359 +0800 | 28-Feb-2013 2248 +0800 | An undone timed task from 2359 31 Dec 2012 to 2248 28 Feb 2013 #" , 
			"#  3 |  F   |   *  | ---------------------- | ---------------------- | ---------------------- | A done floating task                                           #" ,
			"#The reference number is not used in the parsing process. DoIt will ignore non-consecutive or wrong reference numbers.                                         #" ,
			"################################################################################################################################################################"	
		};

		private final String LINE_FLOATING = "F";
		private final String LINE_DEADLINE = "D";
		private final String LINE_TIMED = "T";

		private final String LINE_DONE = "*";
		private final String LINE_UNDONE = "-";

		private final String LINE_DATE_LONGER_FORMAT = "EEE dd-MMM-yyyy hh:mma Z";
		private final DateTimeFormatter LINE_DATE_LONGER_FORMATTER = DateTimeFormat.forPattern(LINE_DATE_LONGER_FORMAT);

		private final String LINE_END_OF_LINE = System.getProperty( "line.separator" );

		private final String LINE_LAST_MODIFIED = "#Last Modified: %1$s";
		private final int ZERO_LENGTH_TASK_NAME = 0;

		private final long START_OF_FILE = 0;
		private final long INITIAL_FILE_SIZE = 0;

		private FileStatus fileAttributes = FileStatus.FILE_PERMISSIONS_UNKNOWN;

		private File databaseFile = new File(filename);
		private FileLock databaseFileLock = null;
		private FileChannel databaseChannel = null;
		private RandomAccessFile randDatabaseAccess = null;
		
		private Logger log = LogHandler.getLogInstance();

		private static FileManagement theOne = null;

		public static FileManagement getInstance(){
			if(theOne == null){
				theOne = new FileManagement();
			}

			return theOne;
		}

		private FileManagement()	{
			log.info("FileMgmt instance created");
		}


		public void readFileAndDetectCorruption(List<Task> storeInHere) {
			if(storeInHere == null) {
				throw new IllegalArgumentException();
			}

			if((fileAttributes.equals(FileStatus.FILE_ALL_OK)) || (fileAttributes.equals(FileStatus.FILE_READ_ONLY))) {
				try {
					readFiletoDataBase(storeInHere);
				} catch (Exception e) {
					fileAttributes = FileStatus.FILE_IS_CORRUPT;
				}
			}
		}
		public void prepareDatabaseFile() {

			log.info("Prepare database file");
			assert(databaseFile != null);
			
			boolean isRWLockSucessful = true;
			//Open file as read and write
			try {
				randDatabaseAccess = new RandomAccessFile(databaseFile, "rws");
				databaseChannel = randDatabaseAccess.getChannel();
				
				log.info("Attempting to get file lock");
				databaseFileLock = databaseChannel.tryLock();

				if(databaseFileLock == null) {
					fileAttributes = FileStatus.FILE_IS_LOCKED;
					isRWLockSucessful = false;
					log.warning("File is locked");
				} else {
					fileAttributes = FileStatus.FILE_ALL_OK;
					log.info("Full permissions obtained for file");
				}
			} catch (IOException e) {
				isRWLockSucessful = false;
				log.warning("Cannot open for writing " + e);
			}

			//If the above is successful, we end this method
			if(isRWLockSucessful || fileAttributes.equals(FileStatus.FILE_IS_LOCKED)) {
				return;
			}

			//Open File as read only
			try {
				log.warning("Attempt to open as read only");
				randDatabaseAccess = new RandomAccessFile(databaseFile, "r");
				databaseChannel = randDatabaseAccess.getChannel();
				fileAttributes = FileStatus.FILE_READ_ONLY;
			} catch (IOException e) {
				fileAttributes = FileStatus.FILE_PERMISSIONS_UNKNOWN;
				log.severe("Unknown file permissions " + e);
			}

		}

		public FileStatus getFileAttributes()	{
			return fileAttributes;
		}

		public void closeFile(){
			log.info("Closing File");
			try {
				if(databaseFileLock != null ){
					databaseFileLock.release();
					databaseFileLock = null;
				}
				if(databaseChannel != null){
					databaseChannel.close();
					databaseChannel = null;
				}

				if (randDatabaseAccess != null) {
					randDatabaseAccess.close();
				}
				randDatabaseAccess = null;

			} catch (IOException e) {
				log.severe("Cannot close file " + databaseFileLock + " " + databaseChannel + " " + randDatabaseAccess + " " + e );
			}
		}


		private void readFiletoDataBase(List<Task> storeInHere) throws IOException, DataFormatException {
			log.info("Reading file method");
			
			assert(storeInHere != null);
			assert(databaseFile != null);
			
			
			if(randDatabaseAccess == null) {
				log.warning("randDatabase is null, database file probably not prepared");
				throw new IOException();
			}
			
			
			
			int fileSize = (int) databaseFile.length();
			byte[] fileByteContents = new byte[fileSize];

			
			assert(START_OF_FILE >= 0);
			
			log.info("Start reading from disk");
			
			randDatabaseAccess.seek(START_OF_FILE);
			randDatabaseAccess.read(fileByteContents);
			
			log.info("Reading to RAM completed");

			String fileInStringFormat = new String(fileByteContents);
			BufferedReader fileStringReader = new BufferedReader(new StringReader(fileInStringFormat));


			String lineFromInput;
			String parsed[] = null;
			
			log.info("Parsing contents");

			while((lineFromInput = fileStringReader.readLine()) != null)	{
				log.info("Input line " + lineFromInput);
				
				if(lineFromInput.startsWith(LINE_IGNORE_CHARACTER)) continue;

				parsed = lineFromInput.split(LINE_PARAM_DELIMITER_READ, LINE_NUM_FIELDS);

				Task toBeAdded = TaskParser(parsed);
				log.info("Parsed line " + toBeAdded.showInfo());

				storeInHere.add(toBeAdded);
			}
			
			log.info("Parsing complete");

		}

		private Task TaskParser(String[] parsed) throws DataFormatException {
			assert(parsed != null);
			
			Task parsedTask;

			switch(parsed[LINE_POSITION_TASKTYPE])	{
			case LINE_FLOATING : parsedTask = parseInFloatingTask(parsed);
			break;
			case LINE_DEADLINE : parsedTask = parseInDeadlineTask(parsed);
			break;
			case LINE_TIMED : parsedTask = parseInTimedTask(parsed);
			break;
			default: throw new DataFormatException("Unknown task type");

			}

			return parsedTask;

		}

		private Task parseInTimedTask(String[] parsed) throws DataFormatException {
			assert(parsed != null);
			
			String taskName = parseTaskName(parsed[LINE_POSITION_TASKNAME]);
			boolean done = retrieveTaskDoneStatus(parsed[LINE_POSITION_DONE]);
			DateTime startDate = parseDate(parsed[LINE_POSITION_START_DATE]); 
			DateTime endDate = parseDate(parsed[LINE_POSITION_END_DATE]); 

			if(startDate.isAfter(endDate)) {
				throw new DataFormatException("Start date after end date");
			}

			return new Task(taskName, startDate, endDate, done);
		}


		private String parseTaskName(String taskName)
				throws DataFormatException {
			assert(taskName != null);
			
			if(taskName.length() == ZERO_LENGTH_TASK_NAME) {
				throw new DataFormatException("0 length task name");
			}
			return taskName;
		}


		private Task parseInDeadlineTask(String[] parsed) throws DataFormatException {
			assert(parsed != null);
			
			String taskName = parseTaskName(parsed[LINE_POSITION_TASKNAME]);		
			boolean done = retrieveTaskDoneStatus(parsed[LINE_POSITION_DONE]);
			DateTime deadline = parseDate(parsed[LINE_POSITION_DEADLINE_DATE]);

			return new Task(taskName, deadline, done);
		}

		private Task parseInFloatingTask(String[] parsed) throws DataFormatException {
			assert(parsed != null);
			
			String taskName = parseTaskName(parsed[LINE_POSITION_TASKNAME]);
			boolean done = retrieveTaskDoneStatus(parsed[LINE_POSITION_DONE]);

			return new Task(taskName, done);
		}

		private boolean retrieveTaskDoneStatus(String parsed) throws DataFormatException {
			assert(parsed != null);
			
			boolean done;

			if(parsed.equals(LINE_DONE)) {
				done = true;
			} else if(parsed.equals(LINE_UNDONE)){
				done = false;
			} else {
				throw new DataFormatException("Unknown Done value");
			}
			return done;
		}


		private DateTime parseDate(String date) throws DataFormatException {
			
			assert(date != null);
			
			DateTime parsedDate = null;

			try {
				parsedDate = new DateTime(FILE_DATE_FORMAT.parseDateTime(date));
			} catch (IllegalArgumentException e) {
				throw new DataFormatException("Date not in correct format");
			}

			return parsedDate;
		}


		private String taskToDatabaseString(Task toBeConverted, int index) {	
			assert(toBeConverted != null);
			
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
			
			String taskString = String.format(FILE_LINE_FORMAT, index, typeString, doneString, deadline, start, end, task);

			log.info("Task string generated " + taskString);
			return taskString;
		}


		private String getTimeFileFormat(DateTime toBeConverted)	{
			if(Task.INVALID_DATE_FIELD.isEqual(toBeConverted))	{
				return LINE_EMPTY_DATE;
			}

			return FILE_DATE_FORMAT.print(toBeConverted);
		}


		public void writeDataBaseToFile(List<Task> toBeWritten) throws IOException, WillNotWriteToCorruptFileException	{
			log.info("Attempt to write file");
			
			if(toBeWritten == null) {
				log.warning("Null arguments");
				throw new IllegalArgumentException("Null arguments");
			}

			if(fileAttributes.equals(FileStatus.FILE_IS_CORRUPT)) {
				log.warning("Attempting to write to corrupt file");
				throw new WillNotWriteToCorruptFileException("Corrupt File");
			}

			if(randDatabaseAccess == null) {
				log.warning("randDatabaseAccess is null");
				throw new IOException("Database file not prepared");
			}

			StringBuffer dataToBeWritten = new StringBuffer();

			for(String helpline : filehelp)	{
				dataToBeWritten.append(helpline + LINE_END_OF_LINE);
			}

			int index = FILE_INDEX_START;

			for(Task temp : toBeWritten) {
				String writeLine = taskToDatabaseString(temp, index) + LINE_END_OF_LINE;
				
				log.info("Written line " + writeLine);
				
				dataToBeWritten.append(writeLine);
				index++;
			}

			String currentTime = LINE_DATE_LONGER_FORMATTER.print(new DateTime());

			String appendLastModified = String.format(LINE_LAST_MODIFIED, currentTime);
			dataToBeWritten.append(appendLastModified + LINE_END_OF_LINE);

			String dataStringToBeWritten = dataToBeWritten.toString();
			log.info("Data to be written " + dataStringToBeWritten );
			
			//Truncate the file to 0 or INITIAL_FILE_SIZE size to clear the old database file before writing
			log.info("Attempt to write to disk");
			
			randDatabaseAccess.setLength(INITIAL_FILE_SIZE);
			randDatabaseAccess.seek(START_OF_FILE);
			randDatabaseAccess.writeBytes(dataStringToBeWritten);
			
			log.info("Disk Write complete");

		}


}
