package main.logic;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import main.shared.NattyParserWrapper;
import main.shared.Task.TaskType;

import com.joestelmach.natty.CalendarSource;
import com.joestelmach.natty.DateGroup;

/**
 * This class defines the parser for add command. Each AddParser object
 * associates with 1 command only. The information extracted from the argument
 * is private and to be extracted using designed API (get methods).
 * 
 * @author mrlinh
 * 
 */
public class AddParser extends CommandParser {

	private static final int SECOND_ENTRY = 1;
	private static final int FIRST_ENTRY = 0;
	private static final int START_INDEX = 0;

	public boolean isTaskNameNonempty = true;

	private List<DateGroup> groups;
	private NattyParserWrapper parser; // natty parser to be used internally.
	private String argument; // passed in by logic.
	private String taskName;
	private TaskType taskType;
	private int dateStringStartPosition;
	private int dateStringEndPosition;
	private DateTime startDate;
	private DateTime endDate;
	private DateTime deadline;

	/**
	 * Constructor for AddParser
	 * 
	 * @param arg
	 *            : the string argument of add command.
	 */
	public AddParser(String arguments) {
		super(arguments);
		argument = arguments;
		parser = NattyParserWrapper.getInstance();
		groups = new ArrayList<DateGroup>();
		taskType = null;
		dateStringStartPosition = 0;
		dateStringEndPosition = 0;
		startDate = null;
		endDate = null;
		deadline = null;
	}

	/**
	 * Parse the argument. Extracted results are memorized in private variables
	 * associate with this AddParser object.
	 * 
	 * @throws EmptyDescriptionException
	 */
	public void parse() throws EmptyDescriptionException {
		String dateString;
		if (hasAposPair()) {
			dateString = removeTextInsideApostrophe();
		} else {
			dateString = removeTriggerWord();
		}

		CalendarSource.setBaseDate(new DateTime().withTime(23, 59, 00, 00)
				.toDate());
		groups = parser.parseWithDefaultBaseDate(dateString);
		if (groups.size() != 0) {
			dateStringStartPosition = groups.get(0).getPosition();
			while (!checkSeparatedBySpace()) {
				char[] tempCharArray = dateString.toCharArray();
				String tempString = "";
				int i = dateStringStartPosition;
				i = getToNextNonemptyWord(tempCharArray, i);
				if (i == tempCharArray.length) {
					dateStringStartPosition = argument.length();
					groups = parser.parseWithDefaultBaseDate("");
					break;
				}
				dateStringStartPosition = i;
				for (; i < dateString.length(); i++) {
					tempString = tempString + tempCharArray[i];
				}
				dateString = tempString;
				groups = parser.parseWithDefaultBaseDate(dateString);
				if (groups.size() == 0) {
					break;
				}
				dateStringStartPosition += groups.get(0).getPosition();
			}
			// adjustTimeBasedOnTriggerWords();
		}
		determineTaskType();
		determineEndOfDateString();
		determineTaskName();
		adjustStartTimeAndEndTime();
		if (taskType == TaskType.DEADLINE) {
			determineDeadline();
		}
		if (taskType == TaskType.TIMED) {
			determineStartAndEnd();
		}
	}

	/*
	 * private void adjustTimeBasedOnTriggerWords() { char[] tempCharArray =
	 * argument.toCharArray(); String tempString = ""; for(int
	 * i=0;i<matchingPosition;++i){ tempString += tempCharArray[i]; } String[]
	 * tempStringArray = tempString.split(" "); int len =
	 * tempStringArray.length;
	 * if(tempStringArray[len-1].compareTo("before")==0){
	 * groups.get(0).getDates().get(0) }
	 * 
	 * }
	 */
	private void adjustStartTimeAndEndTime() {
		if (taskType == TaskType.TIMED) {
			String dateString;
			dateString = groups.get(START_INDEX).getText();
			if (dateString.contains("to")
					&& !dateString.toLowerCase().contains("am")
					&& !dateString.toLowerCase().contains("pm")) // second and
																	// third
																	// condition
																	// to check
																	// if the
																	// string
																	// contain a
																	// number.
			{
				String tempStringArray[] = dateString.split("to");
				if (tempStringArray.length == 2) {
					groups.get(0)
							.getDates()
							.get(START_INDEX)
							.setTime(
									parser.parseWithCustomisedBaseDate(00, 00,
											00, tempStringArray[0]).get(0)
											.getDates().get(0).getTime());
					CalendarSource.setBaseDate(new DateTime().withTime(23, 59,
							00, 00).toDate());
					groups.get(0)
							.getDates()
							.get(SECOND_ENTRY)
							.setTime(
									parser.parseWithDefaultBaseDate(
											tempStringArray[1]).get(0)
											.getDates().get(0).getTime());
				}
			}
		}
	}

	private int getToNextNonemptyWord(char[] tempCharArray, int i) {
		while (i < tempCharArray.length && tempCharArray[i] != ' ') {
			i++;
		}
		while (i < tempCharArray.length && tempCharArray[i] == ' ') {
			i++;
		}
		return i;
	}

	/**
	 * 
	 * Remove trigger words from original text to ensure correctness of parsing.
	 * This is due to natty's issue with parsing text containing trigger words.
	 * Trigger words are: from, this, by, to, on, at...
	 * 
	 * @return the string with trigger words replaced by space.
	 * 
	 */

	private String removeTriggerWord() {
		String result = "";
		String[] tempStringArray = argument.split(" ");
		for (int i = 0; i < tempStringArray.length; ++i) {
			switch (tempStringArray[i].toLowerCase()) {
			case "from":
			case "this":
				result += "     ";
				break;
			case "by":
			case "on":
			case "at":
				result += "   ";
				break;
			default:
				result += tempStringArray[i] + " ";
			}
		}
		return result.trim();
	}

	/**
	 * Determine whether the argument has a pair of apostrophe. As long as the
	 * text has 2 apostrophe then it will return true. i.e, will return true
	 * even if the text has an odd number of apostrophes.
	 * 
	 * @return true if it has a pair of apostrophe, else returns false.
	 */
	private boolean hasAposPair() {
		if (argument.indexOf("\"") == argument.lastIndexOf("\""))
			return false;
		return true;
	}

	/**
	 * Check if the matching text returned by natty is separated from the next
	 * word by a space. Example: Natty may interpret "go to market" as mar in
	 * March. This method is to make sure natty only parse mar if it is in a
	 * string like "do something in mar at somewhere".
	 * 
	 * @return true if the parsed text is separated by space, else return false.
	 */

	private boolean checkSeparatedBySpace() {
		char[] tempCharArray = argument.toCharArray();
		String matchingValue = groups.get(START_INDEX).getText();
		char[] matchingArray = matchingValue.toCharArray();
		if ((dateStringStartPosition + matchingArray.length < tempCharArray.length && tempCharArray[dateStringStartPosition
				+ matchingArray.length] != ' ')
				|| (dateStringStartPosition != 0 && tempCharArray[dateStringStartPosition - 1] != ' '))
			return false;
		return true;
	}

	/**
	 * This method remove the text inside the outermost pair of apostrophe to
	 * get a date string contains only text that is outside the pair of
	 * apostrophe.
	 * 
	 * @return the text outside apostrophe.
	 */
	private String removeTextInsideApostrophe() {
		String result = "";
		for (int i = argument.indexOf("\""); i <= argument.lastIndexOf("\""); ++i)
			result += " ";
		if (argument.lastIndexOf("\"") < argument.length() - 1) {
			for (int i = argument.lastIndexOf("\"") + 1; i < argument.length(); ++i)
				result += argument.toCharArray()[i];
		}
		return result;
	}

	/**
	 * This method determines the deadline of the new task. Pre-condition: the
	 * new task is a deadline task, i.e has only 1 date.
	 */

	private void determineDeadline() {
		deadline = new DateTime(groups.get(FIRST_ENTRY).getDates()
				.get(FIRST_ENTRY));
	}

	/**
	 * This method determines start time and end time of the new task.
	 * Pre-condition: the new task is a timed task, i.e has 2 date.
	 */

	private void determineStartAndEnd() {
		DateTime time1 = new DateTime(groups.get(FIRST_ENTRY).getDates()
				.get(FIRST_ENTRY));
		DateTime time2 = new DateTime(groups.get(FIRST_ENTRY).getDates()
				.get(SECOND_ENTRY));
		if (time2.isBefore(time1)) {
			startDate = time2;
			endDate = time1;
		} else {
			startDate = time1;
			endDate = time2;
		}
	}

	/**
	 * Determines task type of the new task base on the number of successfully
	 * interpreted dates.
	 */

	private void determineTaskType() {
		if (groups.size() == 0) {
			taskType = TaskType.FLOATING;
		} else if (groups.get(FIRST_ENTRY).getDates().size() == 1) {
			taskType = TaskType.DEADLINE;
		} else if (groups.get(FIRST_ENTRY).getDates().size() == 2) {
			taskType = TaskType.TIMED;
		}
	}

	private void determineTaskName() throws EmptyDescriptionException {
		if (!hasAposPair()) {
			if (taskType == TaskType.FLOATING)
				taskName = argument;
			else {
				taskName = buildTaskNameString();
			}
		} else {
			taskName = getStringInsideApostrophe();
		}
	}

	/**
	 * Determine the end of the date string. This comes in handy when there is
	 * text AFTER the date string that needs to be added to the task
	 * description.
	 */

	private void determineEndOfDateString() {
		if (this.getTaskType() != TaskType.FLOATING) {
			String tempString = groups.get(START_INDEX).getText();
			dateStringEndPosition = groups.get(START_INDEX).getPosition()
					+ tempString.length() + 1;

		}

	}

	/**
	 * This method builds the task description after removing trigger words
	 * 
	 * @return string of task description including text both before and after
	 *         the time component.
	 * 
	 * @throws EmptyDescriptionException
	 */

	private String buildTaskNameString() throws EmptyDescriptionException {
		String result = "";
		char[] tempCharArray = argument.toCharArray();
		for (int i = 0; i < dateStringStartPosition; ++i)
			result = result + tempCharArray[i];
		String[] tempStringArray = result.split(" ");
		if (tempStringArray[tempStringArray.length - 1].compareTo("from") == 0
				|| tempStringArray[tempStringArray.length - 1].compareTo("by") == 0
				|| tempStringArray[tempStringArray.length - 1]
						.compareTo("before") == 0
				|| tempStringArray[tempStringArray.length - 1].compareTo("on") == 0
				|| tempStringArray[tempStringArray.length - 1].compareTo("at") == 0
				|| tempStringArray[tempStringArray.length - 1]
						.compareTo("this") == 0) {
			result = "";
			for (int i = 0; i < tempStringArray.length - 1; ++i)
				result = result + tempStringArray[i] + " ";
		}
		for (int i = dateStringEndPosition; i < argument.length(); i++) {
			result += tempCharArray[i];
		}
		result = result.trim();
		if (result.compareTo("") == 0) {
			throw new EmptyDescriptionException();
		} else {
			return result;
		}
	}

	private String getStringInsideApostrophe() {
		String result = "";
		int firstApostrophePos = argument.indexOf('\"');
		int lastApostrophePos = argument.lastIndexOf('\"');
		for (int i = firstApostrophePos + 1; i <= lastApostrophePos - 1; ++i) {
			result += argument.toCharArray()[i];
		}
		result.trim();
		return result;
	}

	/*
	 * ==========================================================================
	 * ========= ====== Below are API to extract the interpreted information out
	 * of the parser======
	 * ======================================================
	 * =============================
	 */

	public TaskType getTaskType() {
		return taskType;
	}

	public String getTaskName() {
		return taskName;
	}

	public DateTime getBeginTime() {
		if (groups.get(0).getDates().size() == 2) {
			return startDate;
		}
		return new DateTime(groups.get(FIRST_ENTRY).getDates().get(FIRST_ENTRY));
	}

	public DateTime getEndTime() {
		if (groups.get(FIRST_ENTRY).getDates().size() == 2) {
			return endDate;
		}
		return new DateTime(groups.get(FIRST_ENTRY).getDates().get(FIRST_ENTRY));
	}

	public DateTime getDeadline() {
		return this.deadline;
	}

	public List<DateGroup> getGroups() {
		return this.groups;
	}
}
