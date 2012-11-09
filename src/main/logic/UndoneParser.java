package main.logic;

import java.util.List;
import java.util.NoSuchElementException;

import main.shared.Task;

public class UndoneParser extends CommandParser {

	private String arguments;
	private Integer index;
	private List<Task> lastShownToUi;
	private int serial;

	public UndoneParser(String arguments) {
		super(arguments);
		this.arguments = arguments;
		index = null;
		lastShownToUi = lastShownObject.getLastShownList();
	}


	@Override
	public void parse() throws NumberFormatException{
		index = Integer.parseInt(arguments)-1;		// counting from 0
		if ((index < 0) || ((index + 1) > lastShownToUi.size())) {
			throw new NoSuchElementException();
		}
		serial = lastShownToUi.get(index).getSerial();
	}
	public int getSerialOfTask(){
		return serial;
	}

}
