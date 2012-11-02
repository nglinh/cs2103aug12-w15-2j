package main.logic;

import main.shared.LogicToUi;

public class RefreshHandler extends CommandHandler {


	
	public RefreshHandler(String arguments) {
		super(arguments);
	}

	@Override
	public LogicToUi execute() {
		CommandHandler sortHandler = new SortHandler(latestSortArgument);
		return sortHandler.execute();
		
	}

}
