//@author A0081007U

package main.logic;

public class SearchParser extends CommandParser{

	private String arguments;
	private String[] keywords = new String[0];
	
	
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
	
	public String[] getKeyWords(){
		return keywords;
	}

}