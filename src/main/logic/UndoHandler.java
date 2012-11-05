package main.logic;

import java.io.IOException;
import java.util.List;

import main.shared.LogicToUi;
import main.shared.Task;
import main.storage.WillNotWriteToCorruptFileException;

public class UndoHandler extends CommandHandler{

	public UndoHandler(String arguments) {
		super(arguments);
	}

	private static final String ERROR_NO_MORE_UNDO = "You don't have any more undo steps left";

	public LogicToUi execute(){
		if(super.undoStepsRemaining() == 0){
			return new LogicToUi(ERROR_NO_MORE_UNDO);
		}


		try {
			List<Task> previous = super.peekUndoClones();
			dataBase.writeALL(previous);
			String status = "The " + super.popAndGetPrevUndoMsg() + " has been undone";

			super.popUndoClones();
			feedback = new LogicToUi(status);

		} catch (IOException e) {
			feedback = new LogicToUi(ERROR_IO);
		} catch (WillNotWriteToCorruptFileException e) {
			feedback = new LogicToUi(ERROR_FILE_CORRUPTED);
		}


		return feedback;
	}
}
