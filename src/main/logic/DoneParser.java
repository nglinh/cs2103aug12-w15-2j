package main.logic;

public class DoneParser extends CommandParser {

	private String arguments;
	public Integer index = null;

	public DoneParser(String arguments) {
		super(arguments);
		this.arguments = arguments;

	}


	@Override
	public void parse() throws NumberFormatException{
		index = Integer.parseInt(arguments);
	}

}
