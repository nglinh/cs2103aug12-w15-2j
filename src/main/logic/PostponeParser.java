package main.logic;

import java.util.List;
import java.util.NoSuchElementException;

import org.joda.time.DateTime;

import com.joestelmach.natty.DateGroup;

import main.logic.exceptions.CannotParseDateException;
import main.logic.exceptions.CannotPostponeFloatingException;
import main.logic.exceptions.EmptyDescriptionException;
import main.shared.NattyParserWrapper;
import main.shared.Task;
import main.shared.Task.TaskType;

//@author A0088427U

/**
 * This class extends command parser class.
 * 
 * Postpone parser parses commands for postpone command.
 * 
 * Information about postponed task is to be extracted
 * by postpone handler. Postpone parser does not release
 * any of the information unless it is asked by its handler.
 *
 */
public class PostponeParser extends CommandParser {
	private String argument;
	private Task toBePostponed;
	private NattyParserWrapper parser;
	private List<DateGroup> deadlineGroups;
	private List<DateGroup> startGroups;
	private List<DateGroup> endGroups;
	private DateTime newDeadline;
	private DateTime newStartTime;
	private DateTime newEndTime;
	private List<Task> lastShownToUi;
	private int toBePostponedSerial;
	
	/**
	 * Constructor of postpone parser class.
	 * @param arguments : string of command argument.
	 */
	public PostponeParser(String arguments) {
		super(arguments);
		argument = arguments;
		parser = NattyParserWrapper.getInstance();
		lastShownToUi = lastShownObject.getLastShownList();
	}

	@Override
	public void parse() throws EmptyDescriptionException,
			CannotParseDateException, CannotPostponeFloatingException, NumberFormatException {
		int index = checkArgumentValidity();
		toBePostponed = lastShownToUi.get(index);
		toBePostponedSerial = toBePostponed.getSerial();
		
		if (toBePostponed.getType() == TaskType.FLOATING) {
			throw new CannotPostponeFloatingException();
		}
		if (toBePostponed.getType() == TaskType.DEADLINE) {
			deadlineGroups = parser.parseWCustomBaseDate(
					toBePostponed.getDeadline(), argument);
			newDeadline = new DateTime(deadlineGroups.get(INT_0).getDates().get(INT_0));

		}
		if (toBePostponed.getType() == TaskType.TIMED) {
			startGroups = parser.parseWCustomBaseDate(
					toBePostponed.getStartDate(), argument);	// to get the period of time
																// the task will be postponed

			if (startGroups.isEmpty()) {
				throw new CannotParseDateException();
			}
			newStartTime = new DateTime(startGroups.get(INT_0).getDates().get(INT_0));

			endGroups = parser.parseWCustomBaseDate(toBePostponed.getEndDate(),
					argument);
			if (endGroups.isEmpty()) {
				throw new CannotParseDateException();
			}
			newEndTime = new DateTime(endGroups.get(INT_0).getDates().get(INT_0));
		}
	}
	/**
	 * This method check if argument is empty and if the index
	 * is out of bound
	 * @return the index if it is valid( in bound)
	 * @throws CannotParseDateException: if the argument is empty.
	 */
	private int checkArgumentValidity() throws CannotParseDateException {
		int index;
		if (argument.length() == 0) {
			throw new CannotParseDateException();
		}
		index = Integer.parseInt(getFirstWord(argument));
		index--; // Since arraylist index starts from 0
		argument = removeFirstWord(argument);
		if (argument.length() == 0) {
			throw new CannotParseDateException();
		}
		if ((index < 0) || ((index + 1) > lastShownToUi.size())) {
			throw new NoSuchElementException();
		}
		return index;
	}

	public int getToBePostponedSerial() {
		return toBePostponedSerial;
	}

	public DateTime getNewDl() {
		return newDeadline;
	}

	public DateTime getNewST() {
		return newStartTime;
	}

	public DateTime getNewET() {
		return newEndTime;
	}
}
