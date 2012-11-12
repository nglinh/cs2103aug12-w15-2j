package main.logic;
//@author A0081007U
import java.io.IOException;
import java.util.NoSuchElementException;

import main.shared.LogicToUi;
import main.storage.WillNotWriteToCorruptFileException;

public class ExitHandler extends CommandHandler {

	public ExitHandler(String arguments) {
		super(arguments);
	}

	@Override
	public LogicToUi execute() {
		dataBase.unlockFileToExit();
		System.exit(0);
		return new LogicToUi("Exiting DoIt");
	}

	@Override
	protected void updateDatabaseNSendToUndoStack()
			throws NoSuchElementException, IOException,
			WillNotWriteToCorruptFileException {
		throw new UnsupportedOperationException();
		
	}

}
