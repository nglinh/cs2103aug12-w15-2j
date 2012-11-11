//@author A0081007U

package main.logic;


public class SortParser extends CommandParser {

	private String arguments;

	private boolean type = false;
	private boolean done = false;
	private boolean start = false;
	private boolean end = false;
	private boolean name = false;

	private boolean reverse = false;

	private String[] parsed;

	public SortParser(String arguments) {
		super(arguments);
		this.arguments = arguments.toLowerCase();
		parsed = this.arguments.split(" ");
	}

	@Override
	public void parse()  {
		if (parsed.length == 0) {
			start = true;
			return;
		}

		for(String param : parsed){
			if (param.equals("descending") || param.equals("reverse")) {
				reverse = true;
			}

			if (param.equals("type")) {
				type = true;

			} else if (param.equals("done")) {
				done = true;

			} else if (param.equals("start")) {
				start = true;

			} else if (param.equals("end")) {
				end = true;

			} else if (param.equals("name")) {
				name = true;

			} 

		}
	}

	public boolean getType() {
		return type;
	}

	public boolean getDone() {
		return done;
	}

	public boolean getStart() {
		return start;
	}
	public boolean getEnd() {
		return end;
	}
	public boolean getName() {
		return name;
	}
	public boolean getReverse() {
		return reverse;
	}

}
