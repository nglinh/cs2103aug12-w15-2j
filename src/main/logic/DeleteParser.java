package main.logic;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import main.shared.Task;

public class DeleteParser extends CommandParser {
	public boolean isOver;
	public boolean isDone;
	public boolean isAll;
	public boolean onlyOneIndexFound;
	int index;
	String arg;
	Task toBeDeleted;
	List<Task> lastShownToUi;
	List<Integer> listOfToBeDeletedSerials;
	List<Integer> listOfToBeDeletedIndexes;
	int serial;

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
		
		
		
		String[] indexes = arg.split(" ");
		
		
		for(String indexString : indexes){
			index = Integer.parseInt(indexString);
			index--; //To account for index starting from 1
			if ((index < 0) || ((index + 1) > lastShownToUi.size())) {
				throw new NoSuchElementException();
			}
			

			listOfToBeDeletedSerials.add(lastShownToUi.get(index).getSerial());
			listOfToBeDeletedIndexes.add(index + 1);
			
		}
		
		if(listOfToBeDeletedIndexes.size() == 1){
			onlyOneIndexFound = true;
			serial = lastShownToUi.get(index).getSerial();
		}
	

	}
	public int getSerialOfTask(){
		return serial;
	}
}
