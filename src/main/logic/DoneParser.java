package main.logic;

import java.util.List;
import java.util.NoSuchElementException;

import main.shared.Task;

public class DoneParser extends CommandParser {

	private String arguments;
	public Integer index;
	private Task toBeDone;
	List<Task> lastShownToUi;

	public DoneParser(String arguments) {
		super(arguments);
		this.arguments = arguments;
		index = null;
		toBeDone = null;
		lastShownToUi = LastShownToUI.getInstance();
	}


	@Override
	public void parse() throws NumberFormatException{
		index = Integer.parseInt(arguments)-1;		// counting from 0
		if ((index < 0) || ((index + 1) > lastShownToUi.size())) {
			throw new NoSuchElementException();
		}
		lastShownToUi.get(index);
	}
	public Task getToBeDone(){
		return toBeDone;
	}

}
