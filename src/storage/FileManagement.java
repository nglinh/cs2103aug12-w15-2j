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




public class FileManagement {

	public static enum FileStatus {	FILE_CAN_READ_AND_WRITE, FILE_READ_ONLY, FILE_WRITE_ONLY, 
		FILE_CANNOT_CREATE, FILE_CANNOT_WRITE, FILE_IS_CORRUPT};



		public static String filename = "database.txt";

		public static final String LINE_IGNORE_CHARACTER = "#";
		public static final String LINE_PARAM_DELIMITER_READ = " \\| ";
		public static final String LINE_PARAM_DELIMITER_WRITE = " | ";
		public static final String LINE_EMPTY_DATE = "----------------";

		public static final String LINE_DATE_TIME_FORMAT = "dd-MMM-yyyy HHmm";
		public static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormat.forPattern(LINE_DATE_TIME_FORMAT);

		public static final String FILE_LINE_FORMAT = "%1$s" + LINE_PARAM_DELIMITER_WRITE + "%2$s" + LINE_PARAM_DELIMITER_WRITE + "%3$s" + LINE_PARAM_DELIMITER_WRITE + "%4$s" + LINE_PARAM_DELIMITER_WRITE + "%5$s" + LINE_PARAM_DELIMITER_WRITE + "%6$s";

		public static final int LINE_POSITION_TASKTYPE = 0;
		public static final int LINE_POSITION_DONE = 1;
		public static final int LINE_POSITION_DEADLINE_DATE = 2;
		public static final int LINE_POSITION_START_DATE = 3;
		public static final int LINE_POSITION_END_DATE = 4;
		public static final int LINE_POSITION_TASKNAME = 5;

		public static final int LINE_NUM_FIELDS = 6;

		private FileStatus fileAttributes;


		private static final String[] filehelp = {	"##########################################################################################################################################" ,
			"#<Type F/D/T> | <Done U/D> | <Deadline dd-MMM-yyyy HHmm> | <Start dd-MMM-yyyy HHmm> | <End dd-MMM-yyyy HHmm> | <Task>                    #" ,
			"#D | U | 01-Jan-2012 0600 | ---------------- | ---------------- | This is a undone deadline task 0600 on 1st Jan 2012.                   #" ,
			"#T | U | ---------------- | 31-Dec-2012 2359 | 28-Feb-2013 2248 | This is an undone timed task from 2359 31 Dec 2012 to 2248 28 Feb 2013 #" , 
			"#F | D | ---------------- | ---------------- | ---------------- | This is a done floating task.                                          #" ,
			"##########################################################################################################################################"	
		};



		public FileManagement(ArrayList<Task> storeInHere)	{
			fileAttributes = createAndCheckFileAttributes();

			if((fileAttributes.equals(FileStatus.FILE_CAN_READ_AND_WRITE)) || (fileAttributes.equals(FileStatus.FILE_READ_ONLY))) {
				try {
					readFiletoDataBase(storeInHere);
				} catch (Exception e) {
					fileAttributes = FileStatus.FILE_IS_CORRUPT;

					//	If file is corrupt do we clear whatever has been written in, or keep the data written so far??					
					//	storeInHere.clear();
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
			case Task.TYPE_FLOATING : parsedTask = parseInFloatingTask(parsed);
			break;
			case Task.TYPE_DEADLINE : parsedTask = parseInDeadlineTask(parsed);
			break;
			case Task.TYPE_TIMED : parsedTask = parseInTimedTask(parsed);
			break;
			default: throw new DataFormatException();

			}

			return parsedTask;

		}

		private Task parseInTimedTask(String[] parsed) {
			String done = parsed[LINE_POSITION_DONE];
			DateTime startDate = parseDate(parsed[LINE_POSITION_START_DATE]); 
			DateTime endDate = parseDate(parsed[LINE_POSITION_END_DATE]); 

			String taskname = parsed[LINE_POSITION_TASKNAME];

			return new Task(taskname, startDate, endDate, done);
		}


		private Task parseInDeadlineTask(String[] parsed) {
			String done = parsed[LINE_POSITION_DONE];
			DateTime deadline = parseDate(parsed[LINE_POSITION_DEADLINE_DATE]);
			String taskname = parsed[LINE_POSITION_TASKNAME];

			return new Task(taskname, deadline, done);
		}

		private Task parseInFloatingTask(String[] parsed) {
			return new Task(parsed[LINE_POSITION_TASKNAME], parsed[LINE_POSITION_DONE]);
		}


		private DateTime parseDate(String parsed) {
			return new DateTime(FILE_DATE_FORMAT.parseDateTime(parsed));
		}


		private String TaskToDatabaseString(Task toBeConverted) {	
			String type = toBeConverted.getType();
			String done = toBeConverted.getDone();

			String deadline = getTimeFileFormat(toBeConverted.getDeadline());
			String start = getTimeFileFormat(toBeConverted.getStartTime());
			String end = getTimeFileFormat(toBeConverted.getEndTime());

			String task = toBeConverted.getTaskName();

			return String.format(FileManagement.FILE_LINE_FORMAT, type, done, deadline, start, end, task);
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

		public void writeDataBaseToFile(ArrayList<Task> toBeWritten) throws IOException	{


			BufferedWriter out = new BufferedWriter(new FileWriter(filename));

			for(String helpline : filehelp)	{
				out.write(helpline);
				out.newLine();
			}

			for(Task temp : toBeWritten) {
				out.write(TaskToDatabaseString(temp));			
				out.newLine();
			}		

			out.close();
		}




		private FileStatus createAndCheckFileAttributes() {

			FileStatus attributes = FileStatus.FILE_CANNOT_CREATE;

			File databaseFile = new File(filename);

			try {
				if(databaseFile.createNewFile()) {
					if(isFileReadable(databaseFile)) {
						attributes = FileStatus.FILE_CAN_READ_AND_WRITE;
					}
					else {
						attributes = FileStatus.FILE_WRITE_ONLY;
					}
				}
				else { //if file already exists or unable to create due to permissions issues
					if(isFileReadable(databaseFile) && isFileWritable(databaseFile)) {
						attributes = FileStatus.FILE_CAN_READ_AND_WRITE;
					}
					else if(isFileReadable(databaseFile)) {
						attributes = FileStatus.FILE_READ_ONLY;
					}
					else if(isFileWritable(databaseFile)) {
						attributes = FileStatus.FILE_WRITE_ONLY;
					}
					else {
						attributes = FileStatus.FILE_CANNOT_CREATE;
					}

				}

			} 
			catch (IOException e) {
				attributes = FileStatus.FILE_CANNOT_CREATE;
			}


			return attributes;
		}

		private static BufferedReader fileReading() throws FileNotFoundException {
			return new BufferedReader(new FileReader(filename));
		}

		private boolean isFileWritable(File databaseFile) {
			return databaseFile.canWrite();
		}

		private boolean isFileReadable(File databaseFile) {
			return databaseFile.canRead();
		}
}
