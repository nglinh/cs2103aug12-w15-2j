package main.logic;

import main.logic.exceptions.CannotParseDateException;
import main.logic.exceptions.CannotPostponeFloatingException;
import main.logic.exceptions.EmptyDescriptionException;


public abstract class CommandParser {
	protected static final int INT_2 = 2;
	protected static final int INT_1 = 1;
	protected static final int INT_0 = 0;
	public CommandParser(String arguments){
	}
	
	public abstract void parse() throws EmptyDescriptionException, CannotParseDateException,
										CannotPostponeFloatingException;
	
	protected String removeFirstWord(String string){
		return string.replaceFirst(getFirstWord(string), "").trim();
	}
	protected String getFirstWord(String string) {
		return string.split(" ")[0];
	}
	
	protected String[] splitArgumentsBySpaces(String arguments){
		return arguments.split(" ");
	}
	

}
