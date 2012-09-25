package shared;
/**  
 * UiResult.java 
 * A class for to hold information returned from the Logic to the UI
 * @author  Yeo Kheng Meng
 */ 

import java.util.ArrayList;


public class UiResult {

	String output = null;
	ArrayList<Task> display = null;
	
	public UiResult(String output)	{
		this.output = output;
	}
	
	public UiResult(ArrayList<Task> display) {
		this.display = display;
	}
	
	public boolean isReturnValueAString() {
		if (this.output == null) {
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
