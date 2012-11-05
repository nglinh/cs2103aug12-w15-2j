package main.logic;


public abstract class CommandParser {
	public CommandParser(String arguments){
	}
	
	public abstract void parse() throws EmptyDescriptionException, CannotParseDateException, CannotPostponeFloatingException;
	
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
