package main.logic;

import java.util.List;
import java.util.NoSuchElementException;

import main.shared.Task;

public class DeleteParser extends CommandParser {
	public boolean isOver;
	public boolean isDone;
	public boolean isAll;
	int index;
	String arg;
	Task toBeDeleted;
	List<Task> lastShownToUi;

	public DeleteParser(String arguments) {
		super(arguments);
		isOver = false;
		isDone = false;
		isAll = false;
		arg = arguments;
		lastShownToUi = lastShownObject.getLastShownList();
	}

	public void parse() throws NumberFormatException {
		switch(arg.toLowerCase()){
		case "over":
			isOver = true;
			return;
		case "done":
			isDone = true;
			return;
		case "all":
			isAll = true;
			return;
		}
		index = Integer.parseInt(arg);
		index--; //To account for index starting from 1
		if ((index < 0) || ((index + 1) > lastShownToUi.size())) {
			throw new NoSuchElementException();
		}
		toBeDeleted = lastShownToUi.get(index);

	}
	public Task getToBeDeleted(){
		return toBeDeleted;
	}
}
