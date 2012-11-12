//@author A0081007U
package main.logic;



public class ListParser extends CommandParser {

	
	private String arguments;
	private String statusMsg;
	private boolean overdue = false;
	private boolean complete = false;
	private boolean incomplete = false;
	private boolean timed = false;
	private boolean deadline = false;
	private boolean floating = false;
	private boolean today = false;
	private boolean tomorrow = false;
	
	private boolean noParam = false;

	public ListParser(String arguments) {
		super(arguments);
		this.arguments = arguments;
	}

	@Override
	public void parse(){
		
		statusMsg = "Listing based on these parameters: ";

		String[] parameters = arguments.split(STRING_SPACE);

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
		
		if(!(overdue || complete || incomplete || timed || deadline || floating || today || tomorrow)){
			statusMsg += " \"All Tasks\" ";
			noParam = true;
		}
	}
	
	public String getStatusMsg(){
		return statusMsg;
	}
	
	public boolean isOverdue(){
		return overdue;
	}
	
	public boolean isComplete(){
		return complete;
	}
	
	public boolean isIncomplete(){
		return incomplete;
	}
	
	public boolean isTimed(){
		return timed;
	}
	
	public boolean isDeadline(){
		return deadline;
	}
	
	public boolean isFloating(){
		return floating;
	}
	
	public boolean isToday(){
		return today;
	}
	
	public boolean isTomorrow(){
		return tomorrow;
	}
	
	public boolean isNoParam(){
		return noParam;
	}



}
