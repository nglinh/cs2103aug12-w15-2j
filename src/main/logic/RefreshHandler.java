package main.logic;
//@author A0081007U
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
	protected void updateDatabaseNSendToUndoStack()
			throws NoSuchElementException, IOException,
			WillNotWriteToCorruptFileException {
		throw new UnsupportedOperationException();
	}


		
	

}
