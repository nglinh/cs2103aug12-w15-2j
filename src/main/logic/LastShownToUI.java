package main.logic;

//@author A0088427U

/**
 * This class serves as a wrapper of the last shown to UI list of tasks.
 * 
 * This class implements the Singleton pattern since only one list of last shown to UI tasks
 * should be maintained.  
 * @author A0088427U
 */
import java.util.ArrayList;
import java.util.List;

import main.shared.Task;

public class LastShownToUI {
	private static LastShownToUI theOne = null;
	private List<Task> lastShownList = null;

	public static LastShownToUI getInstance() {
		if (theOne == null) {
			theOne = new LastShownToUI();
		}
		return theOne;
	}

	private LastShownToUI() {
		lastShownList = new ArrayList<Task>();
	}

	public void setLastShownList(List<Task> newList) {
		this.lastShownList = newList;
	}

	public List<Task> getLastShownList() {
		return this.lastShownList;
	}
}
