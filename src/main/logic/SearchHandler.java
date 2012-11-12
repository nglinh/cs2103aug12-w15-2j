//@author A0081007U
package main.logic;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import main.shared.LogicToUi;
import main.shared.SearchTerms;
import main.shared.Task;
import main.storage.WillNotWriteToCorruptFileException;

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
		
		String[] keywords = parser.getKeyWords();

		SearchTerms terms = new SearchTerms(keywords);
		List<Task> results = dataBase.search(terms);

		String statusMsg = "You have searched for ";

		for (String keyword : keywords) {
			statusMsg += " \"" + keyword + "\" ";
		}
		
		lastShownObject.setLastShownList(results);
		latestListingHandlerForUI = this;

		return new LogicToUi(results, statusMsg, terms);
	}

	@Override
	protected void updateDatabaseNSendToUndoStack()
			throws NoSuchElementException, IOException,
			WillNotWriteToCorruptFileException {
		throw new UnsupportedOperationException();
		
	}

}
