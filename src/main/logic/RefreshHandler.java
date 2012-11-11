package main.logic;

import java.io.IOException;
import java.util.NoSuchElementException;

import main.shared.LogicToUi;
import main.storage.WillNotWriteToCorruptFileException;

public class RefreshHandler extends CommandHandler {


	
	public RefreshHandler(String arguments) {
		super(arguments);
	}

	@Override
	public LogicToUi execute() {
		return latestSortHandlerForUI.execute();
		
	}

	@Override
	@Deprecated
	protected void updateDatabaseNSendToUndoStack()
			throws NoSuchElementException, IOException,
			WillNotWriteToCorruptFileException {
		// empty method
		
	}

}
