//@author A0081007U
package main.logic;

import java.util.List;
import java.util.NoSuchElementException;
import main.shared.Task;

/**
 * This class extends the commandparser class.
 * 
 * An object of undone parser class associates with one undone handler. Undone
 * parser extract the information regarding the task to be undone and pass this
 * information to undone handler on request.
 * 
 */
public class UndoneParser extends CommandParser {

	private String arguments;
	private Integer index;
	private List<Task> lastShownToUi;
	private int serial;

	/**
	 * Constructor of the undone parser class.
	 * 
	 * @param arguments
	 *            : the argument string to be passed. In particular the string
	 *            is supposed to be the index number of the task.
	 */
	public UndoneParser(String arguments) {
		super(arguments);
		this.arguments = arguments;
		index = null;
		lastShownToUi = lastShownObject.getLastShownList();
	}

	/**
	 * This method overrides the parse method in command parser class. It checks
	 * the validity of the index number and extract the serial number of the
	 * task to be undone.
	 */
	@Override
	public void parse() throws NumberFormatException {
		if (arguments.length() == 0) {
			throw new NoSuchElementException();
		}
		index = Integer.parseInt(arguments) - 1; // counting from 0
		if ((index < 0) || ((index + 1) > lastShownToUi.size())) {
			throw new NoSuchElementException();
		}
		serial = lastShownToUi.get(index).getSerial();
	}

	public int getSerialOfTask() {
		return serial;
	}

}
