//@author A0081007U
package main.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.joda.time.DateTime;

import main.shared.LogicToUi;
import main.shared.SearchTerms;
import main.shared.Task;
import main.storage.WillNotWriteToCorruptFileException;

public class ListHandler extends CommandHandler {

	private ListParser parser;

	public ListHandler(String arguments) {
		super(arguments);
		parser = new ListParser(arguments);
	}

	@Override
	public LogicToUi execute() {

		parser.parse();
		List<Task> results;

		SearchTerms filter;

		if (parser.isOverdue()) {
			results = new ArrayList<Task>();
			DateTime currentTime = new DateTime();
			
			List<Task> everything = dataBase.getAll();

			for(Task entry : everything){
				if(entry.isFloatingTask()) {
					continue;
				}
				
				DateTime toCompare;
				
				if(entry.isDeadlineTask()){
					toCompare = entry.getDeadline();
				} else if (entry.isTimedTask()){
					toCompare = entry.getEndDate();
				} else {
					continue;
				}
				
				if((!entry.isDone()) && toCompare.isBefore(currentTime)) {
					results.add(entry);
				}
				
			}
			
			String overdueStatusMsg = "Listing based on these parameters: \"overdue\" ";

			lastShownObject.setLastShownList(results);
			latestListingHandlerForUI = this;
			return new LogicToUi(results, overdueStatusMsg);
		}



		if (parser.isToday() && parser.isTomorrow()) {
			DateTime startDate = new DateTime().withTimeAtStartOfDay();
			DateTime endDate = startDate.plusDays(1).withTime(23, 59, 59, 999);

			filter = new SearchTerms(parser.isComplete(), parser.isIncomplete(), parser.isTimed(), parser.isDeadline(),
					parser.isFloating(), startDate, endDate);
		} else if (parser.isToday()) {

			DateTime startDate = new DateTime().withTimeAtStartOfDay();
			DateTime endDate = startDate.withTime(23, 59, 59, 999);

			filter = new SearchTerms(parser.isComplete(), parser.isIncomplete(), parser.isTimed(), parser.isDeadline(),
					parser.isFloating(), startDate, endDate);


		} else if (parser.isTomorrow()) {
			DateTime startDate = new DateTime().plusDays(1)
					.withTimeAtStartOfDay();
			DateTime endDate = startDate.plusDays(1).withTime(23, 59, 59, 999);

			filter = new SearchTerms(parser.isComplete(), parser.isIncomplete(), parser.isTimed(), parser.isDeadline(),
					parser.isFloating(), startDate, endDate);

		} else {
			filter = new SearchTerms(parser.isComplete(), parser.isIncomplete(), parser.isTimed(), parser.isDeadline(),
					parser.isFloating());
		}

		results = dataBase.search(filter);

		lastShownObject.setLastShownList(results);
		latestListingHandlerForUI = this;
		return new LogicToUi(results, parser.getStatusMsg(), filter);


	}

	@Override
	@Deprecated
	protected void updateDatabaseNSendToUndoStack()
			throws NoSuchElementException, IOException,
			WillNotWriteToCorruptFileException {
		// empty method.
		
	}



}
