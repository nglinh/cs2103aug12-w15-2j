package main.logic;


public interface CommandParser {
	public void parse() throws EmptyDescriptionException, CannotParseDateException;
}
