package main.logic;

import main.logic.exceptions.CannotParseDateException;
import main.logic.exceptions.CannotPostponeFloatingException;
import main.logic.exceptions.EmptyDescriptionException;
/**
 * This class is to be extended by all parser class. 
 * 
 * The associated handler object will have to extract information
 * from parser object. Parser will NOT parse information out
 * unless its handler object asks for it.
 * 
 * The intention of this class is to make it easy for future
 * extension. All supported command will need to implement a
 * parser and a handler.
 * 
 * @author A0088427U
 *
 */
public abstract class CommandParser {
	protected static final int INT_2 = 2;
	protected static final int INT_1 = 1;
	protected static final int INT_0 = 0;
	protected static final String STRING_SPACE = " ";
	protected static LastShownToUI lastShownObject = LastShownToUI
			.getInstance();
	/**
	 * Constructor to be implemented by children class
	 * @param arguments : argument of the command.
	 */
	public CommandParser(String arguments) {
	}
	/**
	 * Parse the command, to be overidden by respective parsers' classes
	 * @throws EmptyDescriptionException in case the description of 
	 * 					the task is empty.
	 * @throws CannotParseDateException in case the date cannot be
	 * 					parsed.
	 * @throws CannotPostponeFloatingException in case the user
	 * 					postpone a floating task.
	 */
	public abstract void parse() throws EmptyDescriptionException,
			CannotParseDateException, CannotPostponeFloatingException;

	protected String removeFirstWord(String string) {
		return string.replaceFirst(getFirstWord(string), "").trim();
	}

	protected String getFirstWord(String string) {
		return string.split(" ")[0];
	}


}
