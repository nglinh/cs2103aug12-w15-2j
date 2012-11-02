package main.logic;

import java.util.NoSuchElementException;

import main.shared.Task;

public class DeleteParser extends CommandParser {
	public boolean isOver;
	public boolean isDone;
	public boolean isAll;
	int index;
	String arg;
	Task toBeDeleted;

	DeleteParser(String str) {
		isOver = false;
		isDone = false;
		isAll = false;
		arg = str;
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
		if ((index < 0) || ((index + 1) > Logic.lastShownToUI.size())) {
			throw new NoSuchElementException();
		}
		toBeDeleted = Logic.lastShownToUI.get(index);

	}
	public Task getToBeDeleted(){
		return toBeDeleted;
	}
}
