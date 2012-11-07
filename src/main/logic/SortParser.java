package main.logic;


public class SortParser extends CommandParser {

	private String arguments;
	
	public boolean type = false;
	public boolean done = false;
	public boolean start = false;
	public boolean end = false;
	public boolean name = false;
	
	boolean reverse = false;
	
	public SortParser(String arguments) {
		super(arguments);
		this.arguments = arguments;
	}

	@Override
	public void parse()  {
		if (arguments.length() == 0) {
			start = true;
			return;
		}
		
		if (arguments.contains("reverse")) {
			reverse = true;
		}

		if (arguments.contains("type")) {
			type = true;

		} else if (arguments.contains("done")) {
			done = true;

		} else if (arguments.contains("start")) {
			start = true;

		} else if (arguments.contains("end")) {
			end = true;

		} else if (arguments.contains("name")) {
			name = true;
			
		} 


	}

}
