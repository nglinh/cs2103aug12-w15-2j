//@author A0081007U

package main.logic;

/**
 * An object of this class parse a search command. Each object of searchparser
 * associates with only one search handler object.
 * 
 */
public class SearchParser extends CommandParser {

	private String arguments;
	private String[] keywords = new String[0];

	/**
	 * Constructor of the class
	 * 
	 * @param arguments
	 *            : argument string associating with the command
	 */
	public SearchParser(String arguments) {
		super(arguments);
		this.arguments = arguments;
	}

	/**
	 * This method override the parse method in command parser class.
	 */
	@Override
	public void parse() {

		if (arguments.length() == 0) {
			return;
		}

		keywords = arguments.split(" ");

	}

	public String[] getKeyWords() {
		return keywords;
	}

}