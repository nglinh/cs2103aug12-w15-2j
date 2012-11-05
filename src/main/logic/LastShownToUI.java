package main.logic;

import java.util.ArrayList;
import java.util.List;

import main.shared.Task;

public class LastShownToUI {
	private static List<Task> theOne;
	public static List<Task> getInstance(){
		if(theOne == null){
			theOne = new ArrayList<Task>();
		}
		return theOne;	
	}
}
