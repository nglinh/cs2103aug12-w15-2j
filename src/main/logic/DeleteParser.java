package main.logic;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import main.shared.Task;

public class DeleteParser extends CommandParser {
	private boolean isOver;
	private boolean isDone;
	private boolean isAll;
	private boolean onlyOneIndexFound;
	private int index;
	private String arg;
	private List<Task> lastShownToUi;
	private List<Integer> listOfToBeDeletedSerials;
	private List<Integer> listOfToBeDeletedIndexes;
	private int serial;

	public DeleteParser(String arguments) {
		super(arguments);
		isOver = false;
		isDone = false;
		isAll = false;
		onlyOneIndexFound = false;
		arg = arguments;

		listOfToBeDeletedIndexes = new LinkedList<Integer>();
		listOfToBeDeletedSerials = new LinkedList<Integer>();
		lastShownToUi = lastShownObject.getLastShownList();
	}

	public void parse() throws NumberFormatException {
		switch (arg.toLowerCase()) {
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

		String[] indexes = arg.split(STRING_SPACE);

		for (String indexString : indexes) {
			index = Integer.parseInt(indexString);
			index--; // To account for index starting from 1
			if ((index < INT_0) || ((index + INT_1) > lastShownToUi.size())) {
				throw new NoSuchElementException();
			}

			listOfToBeDeletedSerials.add(lastShownToUi.get(index).getSerial());
			listOfToBeDeletedIndexes.add(index + INT_1);

		}

		if (listOfToBeDeletedIndexes.size() == 1) {
			onlyOneIndexFound = true;
			serial = lastShownToUi.get(index).getSerial();
		}

	}

	public int getSerialOfTask() {
		return serial;
	}
	public boolean isDone(){
		return isDone;
	}
	public boolean isOver(){
		return isOver;
	}
	public boolean isAll(){
		return isAll;
	}
	public boolean isOnlyOneIndexFound(){
		return onlyOneIndexFound;
	}
	public List<Integer> getListOfToBeDeletedSerials(){
		return listOfToBeDeletedSerials;
	}
	public List<Integer> getListOfToBeDeletedIndexes(){
		return listOfToBeDeletedIndexes;
	}
}
