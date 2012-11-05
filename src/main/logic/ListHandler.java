package main.logic;

import java.util.List;

import org.joda.time.DateTime;

import main.shared.LogicToUi;
import main.shared.SearchTerms;
import main.shared.Task;

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

		if (parser.overdue) {
			DateTime startDate = new DateTime(Long.MIN_VALUE);
			DateTime endDate = new DateTime();

			filter = new SearchTerms(false, true, false, false, false,
					startDate, endDate);

			results = dataBase.search(filter);

			String overdueStatusMsg = "Listing based on these parameters: \"overdue\" ";

			lastShownToUI = results;
			latestRefreshHandlerForUI = this;
			return new LogicToUi(results, overdueStatusMsg, filter);
		}



		if (parser.today && parser.tomorrow) {
			DateTime startDate = new DateTime().withTimeAtStartOfDay();
			DateTime endDate = startDate.plusDays(1).withTime(23, 59, 59, 999);

			filter = new SearchTerms(parser.complete, parser.incomplete, parser.timed, parser.deadline,
					parser.floating, startDate, endDate);
		} else if (parser.today) {

			DateTime startDate = new DateTime().withTimeAtStartOfDay();
			DateTime endDate = startDate.withTime(23, 59, 59, 999);

			filter = new SearchTerms(parser.complete, parser.incomplete, parser.timed, parser.deadline,
					parser.floating, startDate, endDate);


		} else if (parser.tomorrow) {
			DateTime startDate = new DateTime().plusDays(1)
					.withTimeAtStartOfDay();
			DateTime endDate = startDate.plusDays(1).withTime(23, 59, 59, 999);

			filter = new SearchTerms(parser.complete,parser.incomplete, parser.timed, parser.deadline,
					parser.floating, startDate, endDate);

		} else {
			filter = new SearchTerms(parser.complete, parser.incomplete, parser.timed, parser.deadline,
					parser.floating);
		}

		results = dataBase.search(filter);

		lastShownToUI = results;
		latestRefreshHandlerForUI = this;
		return new LogicToUi(results, parser.statusMsg, filter);


	}



}
