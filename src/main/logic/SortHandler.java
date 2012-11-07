//@author A0081007U

package main.logic;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import main.shared.LogicToUi;
import main.shared.SearchTerms;
import main.shared.Task;
import main.shared.LogicToUi.SortStatus;
import main.shared.Task.SortByEndDate;
import main.shared.Task.SortByStartDate;

public class SortHandler extends CommandHandler{

	private SortParser parser;
	
	private static Comparator<Task> latestSorter = new SortByStartDate();
	private static SortStatus latestSorting = SortStatus.START;
	private List<Task> listFromLastListingCommand; 
	
	public SortHandler(String arguments){
		super(arguments);
		parser = new SortParser(arguments);
	}
	
	
	@Override
	public LogicToUi execute() {
		parser.parse();

		Comparator<Task> sorter = null;

		LogicToUi fromLatestListCommand = latestListingHandlerForUI.execute();
		
		SearchTerms searchFilters = fromLatestListCommand.getFilters();
		String listStatusMsg = fromLatestListCommand.getString();

		listFromLastListingCommand = fromLatestListCommand.getList();
		String statusMsg = null;

		if (parser.getType()) {
			sorter = new Task.SortByType();
			statusMsg = "sorted by Type";
			latestSorting = SortStatus.TYPE;

		} else if (parser.getDone()) {
			sorter = new Task.SortByDone();
			statusMsg = "sorted by Done";
			latestSorting = SortStatus.DONE;

		} else if (parser.getStart()) {
			sorter = new SortByStartDate();
			statusMsg = "sorted by Start Date/Deadline";
			latestSorting = SortStatus.START;

		} else if (parser.getEnd()) {
			sorter = new SortByEndDate();
			statusMsg = "sorted by End Date/Deadline";
			latestSorting = SortStatus.END;

		} else if (parser.getName()) {
			sorter = new Task.SortByName();
			statusMsg = "sorted by Name";
			latestSorting = SortStatus.NAME;

		} else {
			sorter = latestSorter;
			statusMsg = "incorrect parameter, sorted by Start Date/Deadline";
		}

		latestSorter = sorter;
		if (parser.getReverse()) {
			sorter = Collections.reverseOrder(sorter);
			statusMsg += " in descending order";
		}
		
		latestSortHandlerForUI = this;

		Collections.sort(listFromLastListingCommand, sorter);
		String appendedSortStatus = listStatusMsg + ", " + statusMsg;

		if (searchFilters == null) {
			return new LogicToUi(listFromLastListingCommand, appendedSortStatus,
					latestSorting, parser.getReverse());
		} else {
			return new LogicToUi(listFromLastListingCommand, appendedSortStatus,
					searchFilters, latestSorting, parser.getReverse());
		}
	}

}
