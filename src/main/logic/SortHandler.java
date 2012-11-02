package main.logic;

import java.util.Collections;
import java.util.Comparator;

import main.shared.LogicToUi;
import main.shared.SearchTerms;
import main.shared.Task;
import main.shared.LogicToUi.SortStatus;
import main.shared.Task.SortByEndDate;
import main.shared.Task.SortByStartDate;

public class SortHandler extends CommandHandler{

	private String arguments;
	private SortParser parser;
	
	public SortHandler(String arguments){
		super(arguments);
		this.arguments = arguments;
		parser = new SortParser(arguments);
	}
	
	
	@Override
	public LogicToUi execute() {
		parser.parse();
		
		if (arguments.length() == 0) {
			arguments = "start";
		}

		boolean needToReverse = false;
		if (arguments.contains("reverse")) {
			needToReverse = true;
		}

		Comparator<Task> sorter = null;

		LogicToUi fromListCommand = sendCommandToLogicAgain(latestRefreshCommandForUI);
		
		SearchTerms searchFilters = fromListCommand.getFilters();
		String listStatusMsg = fromListCommand.getString();

		String statusMsg = null;

		if (parser.type) {
			sorter = new Task.SortByType();
			statusMsg = "sorted by Type";
			latestSortArgument = arguments;
			latestSorting = SortStatus.TYPE;

		} else if (parser.done) {
			sorter = new Task.SortByDone();
			statusMsg = "sorted by Done";
			latestSortArgument = arguments;
			latestSorting = SortStatus.DONE;

		} else if (parser.start) {
			sorter = new SortByStartDate();
			statusMsg = "sorted by Start Date/Deadline";
			latestSortArgument = arguments;
			latestSorting = SortStatus.START;

		} else if (parser.end) {
			sorter = new SortByEndDate();
			statusMsg = "sorted by End Date/Deadline";
			latestSortArgument = arguments;
			latestSorting = SortStatus.END;

		} else if (parser.name) {
			sorter = new Task.SortByName();
			statusMsg = "sorted by Name";
			latestSortArgument = arguments;
			latestSorting = SortStatus.NAME;

		} else {
			sorter = latestSorter;
			statusMsg = "Incorrect parameter for sort command";
		}

		latestSorter = sorter;
		if (needToReverse) {
			sorter = Collections.reverseOrder(sorter);
		}

		Collections.sort(lastShownToUI, sorter);
		String appendedSortStatus = listStatusMsg + ", " + statusMsg;

		if (searchFilters == null) {
			return new LogicToUi(lastShownToUI, appendedSortStatus,
					latestSorting, needToReverse);
		} else {
			return new LogicToUi(lastShownToUI, appendedSortStatus,
					searchFilters, latestSorting, needToReverse);
		}
	}

}
