package main.logic;

import java.util.ArrayList;
import java.util.List;

import main.shared.Task;

public class LastShownToUI {
	private static LastShownToUI theOne = null;
	private List<Task> lastShownList = null;
	public static LastShownToUI getInstance(){
		if(theOne == null){
			theOne = new LastShownToUI();
		}
		return theOne;	
	}
	private LastShownToUI(){
		lastShownList = new ArrayList<Task>();
	}
	public void setLastShownList(List<Task> newList){
		this.lastShownList = newList;
	}
	public List<Task> getLastShownList(){
		return this.lastShownList;
	}
}
