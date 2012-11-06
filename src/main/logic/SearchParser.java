package main.logic;

public class SearchParser extends CommandParser{

	private String arguments;
	public String[] keywords = new String[0];
	
	
	public SearchParser(String arguments) {
		super(arguments);
		this.arguments = arguments;
	}

	@Override
	public void parse(){
		
		if (arguments.length() == 0) {
			return;
		}
		
		keywords = arguments.split(" ");

	}

}