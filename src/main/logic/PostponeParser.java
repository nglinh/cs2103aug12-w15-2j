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

	public PostponeParser(String arguments) {
		super(arguments);
		argument = arguments;
		parser = NattyParserWrapper.getInstance();
		lastShownToUi = LastShownToUI.getInstance();
	}

	@Override
	public void parse() throws EmptyDescriptionException,
			CannotParseDateException, CannotPostponeFloatingException {
		int index;
		index = Integer.parseInt(getFirstWord(argument));
		argument = removeFirstWord(argument);
		index--; // Since arraylist index starts from 0

		if ((index < 0) || ((index + 1) > lastShownToUi.size())) {
			throw new NoSuchElementException();
		}
		if (argument.length() == 0) {
			throw new CannotParseDateException();
		}
		toBePostponed = lastShownToUi.get(index);
		if (toBePostponed.getType() == TaskType.FLOATING) {
			throw new CannotPostponeFloatingException();
		}
		if (toBePostponed.getType() == TaskType.DEADLINE) {
			deadlineGroups = parser.parseWCustomBaseDate(
					toBePostponed.getDeadline(), argument);
			newDeadline = new DateTime(deadlineGroups.get(0).getDates().get(0));

		}
		if (toBePostponed.getType() == TaskType.TIMED) {
			startGroups = parser.parseWCustomBaseDate(
					toBePostponed.getStartDate(), argument);

			if (startGroups.size() == 0) {
				throw new CannotParseDateException();
			}
			newStartTime = new DateTime(startGroups.get(0).getDates().get(0));

			endGroups = parser.parseWCustomBaseDate(
					toBePostponed.getEndDate(), argument);
			if (endGroups.size() == 0) {
				throw new CannotParseDateException();
			}
			newEndTime = new DateTime(endGroups.get(0).getDates().get(0));
		}
	}

	public Task getToBePostponed() {
		return toBePostponed;
	}

	public DateTime getNewDeadline() {
		return newDeadline;
	}

	public DateTime getNewStartTime() {
		return newStartTime;
	}

	public DateTime getNewEndTime() {
		return newEndTime;
	}
}
