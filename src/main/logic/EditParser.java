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
//TODO use isEqualTo
public class EditParser extends CommandParser {
	private static final String DASH = "-";
	public boolean willChangeType = false;
	public boolean willChangeStartTime = false;
	public boolean willChangeDeadline = false;
	public boolean willChangeEndTime = false;
	public boolean willChangeName = false;

	private String newName;
	private DateTime newStartTime;
	private DateTime newEndTime;
	private DateTime newDeadline;
	private TaskType newType;
	private NattyParserWrapper parser;
	private Task toBeEdited;
	private String argument;
	private List<Task> lastShownToUi;

	public EditParser(String arguments) {
		super(arguments);
		argument = arguments;
		lastShownToUi = lastShownObject.getLastShownList();
		parser = NattyParserWrapper.getInstance();
	}

	@Override
	public void parse() throws EmptyDescriptionException,
			CannotParseDateException, NumberFormatException {
		int index;
		index = Integer.parseInt(getFirstWord(argument));
		argument = removeFirstWord(argument);
		index--; // Since arraylist index starts from 0

		if ((index < 0) || ((index + 1) > lastShownToUi.size())) {
			throw new NoSuchElementException();
		}
		toBeEdited = lastShownToUi.get(index);
		String[] tempStringArray = argument.split(DASH);
		for (int i = 0; i < tempStringArray.length; ++i) {
			if (tempStringArray[i] != null && tempStringArray[i].length() != 0) {
				String commandArgument = getFirstWord(tempStringArray[i]);
				String updatedField = removeFirstWord(tempStringArray[i]);
				updateField(commandArgument, updatedField);
			}
		}
	}

	private void updateField(String commandArgument, String newField)
			throws EmptyDescriptionException, CannotParseDateException {

		List<DateGroup> groupsOfDates;
		groupsOfDates = parser.parseWDefBaseDate(newField);
		switch (commandArgument.toLowerCase()) {
		case "name":
			// fall through
		case "n":
			willChangeName = true;
			newName = newField;
			if (newName.length() == 0) {
				throw new EmptyDescriptionException();
			}
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
			willChangeStartTime = true;
			if (groupsOfDates.size() != 0) {
				newStartTime = new DateTime(groupsOfDates.get(0).getDates()
						.get(0));
			} else {
				throw new CannotParseDateException();
			}
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
			willChangeEndTime = true;
			if (groupsOfDates.size() != 0) {
				newEndTime = new DateTime(groupsOfDates.get(0).getDates()
						.get(0));
			} else {
				throw new CannotParseDateException();
			}
			break;
		case "deadline":
			// fall through
		case "d":
			willChangeDeadline = true;
			if (groupsOfDates.size() != 0) {
				newDeadline = new DateTime(groupsOfDates.get(0).getDates()
						.get(0));
			} else {
				throw new CannotParseDateException();
			}
			break;
		case "tofloating":
			// fall through
		case "tofloat":
			willChangeType = true;
			newType = TaskType.FLOATING;
			break;
		}
	}

	public Task getToBeEdited() {
		return toBeEdited;
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
}
