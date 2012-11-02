package main.logic;

import java.io.IOException;

import main.shared.LogicToUi;
import main.storage.NoMoreUndoStepsException;
import main.storage.WillNotWriteToCorruptFileException;

public class UndoHandler extends CommandHandler{
	private static final String ERROR_NO_MORE_UNDO = "You don't have any more undo steps left";

	public LogicToUi execute(){
	try {
		if (dataBase.getUndoStepsLeft() == 0) {
			throw new NoMoreUndoStepsException();
		}

		dataBase.undo();
		String status = "This command \"" + undoHistory.pop()
				+ "\" has been undone";

		feedback = new LogicToUi(status);
	} catch (NoMoreUndoStepsException e) {
		feedback = new LogicToUi(ERROR_NO_MORE_UNDO);
	} catch (IOException e) {
		feedback = new LogicToUi(ERROR_IO);
	} catch (WillNotWriteToCorruptFileException e) {
		feedback = new LogicToUi(ERROR_FILE_CORRUPTED);
	}
	return feedback;
	}
}
