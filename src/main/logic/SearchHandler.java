package main.logic;

import java.util.List;

import main.shared.LogicToUi;
import main.shared.SearchTerms;
import main.shared.Task;

public class SearchHandler extends CommandHandler{
	
	private String arguments;
	private SearchParser parser;
	
	public SearchHandler(String arguments){
		super(arguments);
		this.arguments = arguments;
		parser = new SearchParser(arguments);
	}

	@Override
	public LogicToUi execute() {
		if (arguments.length() == 0) {
			return new LogicToUi("No search terms specified.");
		}
		parser.parse();
		
		String[] keywords = parser.keywords;

		SearchTerms terms = new SearchTerms(keywords);
		List<Task> results = dataBase.search(terms);

		String statusMsg = "You have searched for ";

		for (String keyword : keywords) {
			statusMsg += " \"" + keyword + "\" ";
		}
		
		lastShownObject.setLastShownList(results);
		latestRefreshHandlerForUI = this;

		return new LogicToUi(results, statusMsg, terms);
	}

}
