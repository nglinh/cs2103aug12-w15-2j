package main.logic;

import java.util.List;
import java.util.NoSuchElementException;

import org.joda.time.DateTime;

import com.joestelmach.natty.DateGroup;

import main.logic.exceptions.CannotParseDateException;
import main.logic.exceptions.EmptyDescriptionException;
import main.shared.NattyParserWrapper;
import main.shared.Task;
import main.shared.Task.TaskType;

//@author A0088427U
/**
 * This class is to parser the edit command.
 * 
 * Each Editparser associates with 1 edit handler.
 * 
 * Parsed information is to be extracted by the corresponding edit handler on
 * request. An Editparser object will NOT return information on its extracted
 * information unless requested by it's corresponding EditHandler.
 * 
 */
public class EditParser extends CommandParser {
	private static final String DASH = "-";
	private boolean isChangedToFloat = false;
	private boolean isStartTimeChanged = false;
	private boolean isDeadlineChanged = false;
	private boolean isEndTimeChanged = false;
	private boolean isNameChanged = false;

	private String newName;
	private DateTime newStartTime;
	private DateTime newEndTime;
	private DateTime newDeadline;
	private TaskType newType;
	private NattyParserWrapper parser;
	private String argument;
	private List<Task> lastShownToUi;
	private int toBeEditedSerial;

	/**
	 * Constructor of Editparser
	 * 
	 * @param arguments
	 *            : String to be parsed.
	 */
	public EditParser(String arguments) {
		super(arguments);
		argument = arguments;
		lastShownToUi = lastShownObject.getLastShownList();
		parser = NattyParserWrapper.getInstance();
	}

	/**
	 * Parse the input string.
	 * 
	 * Override parse method in class CommnandParser.
	 */
	@Override
	public void parse() throws EmptyDescriptionException,
			CannotParseDateException, NumberFormatException {
		extractTaskSerial();
		argument = removeFirstWord(argument);
		String[] tempStringArray = argument.split(DASH);
		for (int i = 0; i < tempStringArray.length; ++i) {
			if (tempStringArray[i] != null && !tempStringArray[i].isEmpty()) {
				String commandArgument = getFirstWord(tempStringArray[i]);
				String updatedField = removeFirstWord(tempStringArray[i]);
				updateField(commandArgument, updatedField);
			}
		}
	}

	/**
	 * Extract task serial of the task to be edited.
	 * 
	 * @throws NumberFormatException
	 *             : if the index of the task is invalid.
	 * @throws NoSuchElementException
	 *             : if the index of the task is out of bound.
	 */
	private void extractTaskSerial() throws NumberFormatException,
			NoSuchElementException {
		int index = Integer.parseInt(getFirstWord(argument));
		index--; // Since arraylist index starts from 0

		if ((index < 0) || ((index + 1) > lastShownToUi.size())) {
			throw new NoSuchElementException();
		}
		toBeEditedSerial = lastShownToUi.get(index).getSerial();
	}

	/**
	 * This method update the new fields of this parser object with the new
	 * information extracted from the command.
	 * 
	 * @param commandArgument
	 *            : syntax of the command i.e name, starttime...
	 * @param newField
	 *            : string between the earlier syntax word and the next syntax
	 *            word.
	 * @throws EmptyDescriptionException
	 *             : if the new name is empty
	 * @throws CannotParseDateException
	 *             : if the new date cannot be parsed.
	 */
	private void updateField(String commandArgument, String newField)
			throws EmptyDescriptionException, CannotParseDateException {

		List<DateGroup> groupsOfDates;
		groupsOfDates = parser.parseWDefBaseDate(newField);
		switch (commandArgument.toLowerCase()) {
		case "name":
			// fall through
		case "n":
			extractNewName(newField);
			break;
		case "begintime":
			// fall through
		case "starttime":
			// fall through
		case "stime":
			// fall through
		case "start":
			// fall through
		case "st":
			extractNewST(groupsOfDates);
			break;
		case "endtime":
			// fall through
		case "finishtime":
			// fall through
		case "et":
			// fall through
		case "end":
			// fall through
		case "ft":
			extractNewET(groupsOfDates);
			break;
		case "deadline":
			// fall through
		case "d":
			extractNewDeadline(groupsOfDates);
			break;
		case "tofloating":
			// fall through
		case "tofloat":
			isChangedToFloat = true;
			newType = TaskType.FLOATING;
			break;
		}
	}

	/**
	 * This method extract new name (if the command requires)
	 * 
	 * @param newField
	 *            : the string following syntax word
	 * @throws EmptyDescriptionException
	 *             : if newfield is empty
	 * 
	 */
	private void extractNewName(String newField)
			throws EmptyDescriptionException {
		isNameChanged = true;
		newName = newField;
		if (newName.length() == 0) {
			throw new EmptyDescriptionException();
		}
	}

	/**
	 * To extract new deadline (if the command requires to change deadline)
	 * 
	 * @param groupsOfDates
	 *            : parsed group of date
	 * @throws CannotParseDateException
	 *             : if the parsed group of date is empty.
	 */
	private void extractNewDeadline(List<DateGroup> groupsOfDates)
			throws CannotParseDateException {
		isDeadlineChanged = true;
		if (!groupsOfDates.isEmpty()) {
			newDeadline = new DateTime(groupsOfDates.get(0).getDates().get(0));
		} else {
			throw new CannotParseDateException();
		}
	}

	/**
	 * To extract new End time (if the command requires to change end time)
	 * 
	 * @param groupsOfDates
	 *            : parsed group of date
	 * @throws CannotParseDateException
	 *             : if the parsed group size is 0.
	 */
	private void extractNewET(List<DateGroup> groupsOfDates)
			throws CannotParseDateException {
		isEndTimeChanged = true;
		if (!groupsOfDates.isEmpty()) {
			newEndTime = new DateTime(groupsOfDates.get(INT_0).getDates()
					.get(INT_0));
		} else {
			throw new CannotParseDateException();
		}
	}

	/**
	 * To extract new Start time (if the command requires to change start time).
	 * 
	 * @param groupsOfDates
	 *            : parsed date group
	 * @throws CannotParseDateException
	 *             : if the parsed group is emtpy
	 */
	private void extractNewST(List<DateGroup> groupsOfDates)
			throws CannotParseDateException {
		isStartTimeChanged = true;
		if (!groupsOfDates.isEmpty()) {
			newStartTime = new DateTime(groupsOfDates.get(INT_0).getDates()
					.get(INT_0));
		} else {
			throw new CannotParseDateException();
		}
	}

	public int getToBeEditedSerial() {
		return toBeEditedSerial;
	}

	public String getNewName() {
		return newName;
	}

	public DateTime getNewStartTime() {
		return newStartTime;
	}

	public DateTime getNewEndTime() {
		return newEndTime;
	}

	public DateTime getNewDeadline() {
		return newDeadline;
	}

	public TaskType getNewType() {
		return newType;
	}

	public boolean willChangeStartTime() {
		return isStartTimeChanged;
	}

	public boolean willChangeEndTime() {
		return isEndTimeChanged;
	}

	public boolean willChangeName() {
		return isNameChanged;
	}

	public boolean willChangeDeadline() {
		return isDeadlineChanged;
	}

	public boolean willChangeToFloat() {
		return isChangedToFloat;
	}
}
