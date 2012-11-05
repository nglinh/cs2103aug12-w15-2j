package main.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import main.logic.exceptions.EmptyDescriptionException;
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

	private static final String STRING_PM = "pm";
	private static final String STRING_AM = "am";
	private static final String STRING_EMPTY = "";
	private static final String STRING_TO = "to";
	

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
			dateString = removeTriggerWord(); // To avoid misinterpretation due
												// to limitations
												// of Natty.
		}

		CalendarSource.setBaseDate(new DateTime().withTime(23, 59, 00, 00)
				.toDate());
		groups = parser.parseWDefBaseDate(dateString);
		if (groups.size() != 0) {
			dateStringStartPosition = groups.get(0).getPosition();
			parseFullWordOnly(dateString);
		}
		determineTaskType();
		determineEndOfDateString();
		determineTaskName();
		adjustStartAndEndTime();
		if (taskType == TaskType.DEADLINE) {
			determineDeadline();
		}
		if (taskType == TaskType.TIMED) {
			determineStartAndEnd();
		}
	}

	private void parseFullWordOnly(String dateString) {
		while (!checkSeparatedBySpace()) {
			char[] tempCharArray = dateString.toCharArray();
			String tempString = STRING_EMPTY;
			int i = dateStringStartPosition;
			i = getToNextNonemptyWord(tempCharArray, i);
			if (i == tempCharArray.length) {
				dateStringStartPosition = argument.length();
				groups = parser.parseWDefBaseDate(STRING_EMPTY);
				break;
			}
			dateStringStartPosition = i;
			for (; i < dateString.length(); i++) {
				tempString = tempString + tempCharArray[i];
			}
			dateString = tempString;
			groups = parser.parseWDefBaseDate(dateString);
			if (groups.size() == 0) {
				break;
			}
			dateStringStartPosition += groups.get(0).getPosition();
		}
		return;
	}

	private void adjustStartAndEndTime() {
		if (taskType == TaskType.TIMED) {
			String dateString;
			dateString = groups.get(INT_0).getText();
			if (dateString.contains(STRING_TO)
					&& !dateString.toLowerCase().contains(STRING_AM)
					&& !dateString.toLowerCase().contains(STRING_PM))
			// Second and third condition to check if the string contains exact
			// time.
			{
				String tempStringArray[] = dateString.split(STRING_TO);
				if (tempStringArray.length == INT_2) {
					List<DateGroup> tempDateGroup = parser.parseWCustBaseDate(
							INT_0, INT_0, INT_0, tempStringArray[INT_0]);
					long tempTime = tempDateGroup.get(INT_0).getDates()
							.get(INT_0).getTime();
					Date time1 = groups.get(0).getDates().get(INT_0);
					Date time2 = groups.get(0).getDates().get(INT_1);
					time1.setTime(tempTime);
					tempDateGroup = parser
							.parseWDefBaseDate(tempStringArray[INT_1]);
					tempTime = tempDateGroup.get(INT_0).getDates().get(INT_0)
							.getTime();
					time2.setTime(tempTime);
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
		String result = STRING_EMPTY;
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
		String matchingValue = groups.get(INT_0).getText();
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
		String result = STRING_EMPTY;
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
		deadline = new DateTime(groups.get(INT_0).getDates()
				.get(INT_0));
	}

	/**
	 * This method determines start time and end time of the new task.
	 * Pre-condition: the new task is a timed task, i.e has 2 date.
	 */

	private void determineStartAndEnd() {
		DateTime time1 = new DateTime(groups.get(INT_0).getDates()
				.get(INT_0));
		DateTime time2 = new DateTime(groups.get(INT_0).getDates()
				.get(INT_1));
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
		} else if (groups.get(INT_0).getDates().size() == 1) {
			taskType = TaskType.DEADLINE;
		} else if (groups.get(INT_0).getDates().size() == 2) {
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

		if (taskName == null || taskName.isEmpty()) {
			throw new EmptyDescriptionException();
		}
	}

	/**
	 * Determine the end of the date string. This comes in handy when there is
	 * text AFTER the date string that needs to be added to the task
	 * description.
	 */

	private void determineEndOfDateString() {
		if (this.getTaskType() != TaskType.FLOATING) {
			String tempString = groups.get(INT_0).getText();
			dateStringEndPosition = dateStringStartPosition + groups.get(INT_0).getPosition()
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
		String result = STRING_EMPTY;
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
			result = STRING_EMPTY;
			for (int i = 0; i < tempStringArray.length - 1; ++i)
				result = result + tempStringArray[i] + " ";
		}
		for (int i = dateStringEndPosition; i < argument.length(); i++) {
			result += tempCharArray[i];
		}
		result = result.trim();
		if (result.compareTo(STRING_EMPTY) == 0) {
			throw new EmptyDescriptionException();
		} else {
			return result;
		}
	}

	private String getStringInsideApostrophe() {
		String result = STRING_EMPTY;
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
	 * ========== Below are API to extract the interpreted information
	 * out======= ==========================of the
	 * parser===================================
	 * ================================
	 * ==========================================
	 * ================================
	 * ==========================================
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
		return new DateTime(groups.get(INT_0).getDates().get(INT_0));
	}

	public DateTime getEndTime() {
		if (groups.get(INT_0).getDates().size() == 2) {
			return endDate;
		}
		return new DateTime(groups.get(INT_0).getDates().get(INT_0));
	}

	public DateTime getDeadline() {
		return this.deadline;
	}

	public List<DateGroup> getGroups() {
		return this.groups;
	}
}
