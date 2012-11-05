package main.logic;

public class UndoneParser extends CommandParser {

	private String arguments;
	public Integer index = null;

	public UndoneParser(String arguments) {
		super(arguments);
		this.arguments = arguments;

	}


	@Override
	public void parse() throws NumberFormatException{
		index = Integer.parseInt(arguments);
	}

}
