package main.logic;



public class ListParser extends CommandParser {

	private String arguments;
	public String statusMsg;
	public boolean overdue = false;
	public boolean complete = false;
	public boolean incomplete = false;
	public boolean timed = false;
	public boolean deadline = false;
	public boolean floating = false;
	public boolean today = false;
	public boolean tomorrow = false;

	public ListParser(String arguments) {
		super(arguments);
		this.arguments = arguments;
	}

	@Override
	public void parse(){
		
		statusMsg = "Listing based on these parameters: ";

		String[] parameters = splitArgumentsBySpaces(arguments);

		for (String eachParam : parameters) {

			if (eachParam.equals("overdue")) {
				overdue = true;
				statusMsg += " \"overdue\" ";
			}
			
			if (eachParam.equals("completed") || eachParam.equals("done")) {
				complete = true;
				statusMsg += " \"complete\" ";
			}

			if (eachParam.equals("incomplete") || eachParam.equals("undone")) {
				incomplete = true;
				statusMsg += " \"undone\" ";
			}

			if (eachParam.equals("timed")) {
				timed = true;
				statusMsg += " \"timed\" ";
			}

			if (eachParam.equals("deadline")) {
				deadline = true;
				statusMsg += " \"deadline\" ";
			}

			if (eachParam.equals("floating")) {
				floating = true;
				statusMsg += " \"floating\" ";
			}

			if (eachParam.equals("today")) {
				today = true;
				statusMsg += " \"today\" ";
			}

			if (eachParam.equals("tomorrow")) {
				tomorrow = true;
				statusMsg += " \"tomorrow\" ";
			}
			
			




		}
	}


}
