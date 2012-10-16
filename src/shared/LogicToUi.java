package shared;
/**  
 * LogicToUi.java 
 * A class for to hold information returned from the Logic to the UI
 * @author  Yeo Kheng Meng
 */ 

import java.util.ArrayList;


public class LogicToUi {

	String output = null;
	ArrayList<Task> display = null;
	SearchTerms filters = null;

	
	public LogicToUi(String output)	{
		assert(output != null);
		this.output = output;
	}
	
	public LogicToUi(ArrayList<Task> display, String output) {
		assert(display != null);
		assert(output != null);
		
		this.display = display;
		this.output = output;

		
	}
	
	public LogicToUi(ArrayList<Task> display, String output, SearchTerms filters) {
		assert(display != null);
		assert(output != null);
		assert(filters != null);
		
		this.display = display;
		this.output = output;
		this.filters = filters;
		
	}
	
	public boolean containsFilters(){
		if(this.filters == null) {
			return false;
		} else {
			return true;
		}
	}
	
	
	public boolean containsList() {
		if (this.display == null) {
			return false;
		} else {
			return true;
		}
	}
	
	
	public String getString() {
		return output;
	}
	
	public ArrayList<Task> getList() {
		return display;
	}
}
