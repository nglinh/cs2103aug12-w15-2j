//@author A0081007U

package main.logic;

/**
 * An object of this class parse sort commands. Each instance of this class
 * associates with 1 sort handler object. Arguments regarding the sort command
 * are passed to sort handler on request.
 * 
 */
public class SortParser extends CommandParser {

	private String arguments;

	private boolean type = false;
	private boolean done = false;
	private boolean start = false;
	private boolean end = false;
	private boolean name = false;

	private boolean reverse = false;

	private String[] parsed;

	/**
	 * Constructor of sortParser class.
	 * 
	 * @param arguments
	 *            : argument to be parsed.
	 */
	public SortParser(String arguments) {
		super(arguments);
		this.arguments = arguments.toLowerCase();
		parsed = this.arguments.split(" ");
	}

	/**
	 * This method overrides the parse method in commandparser class. It
	 * extracts information out of the argument string.
	 */
	@Override
	public void parse() {
		if (arguments.length() == 0) {
			start = true;
			return;
		}

		for (String param : parsed) {
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
