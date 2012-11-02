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
		if(undoClones.size() == 0){
			return new LogicToUi(ERROR_NO_MORE_UNDO);
		}


		try {
			List<Task> previous = undoClones.peek();
			dataBase.writeALL(previous);
			String status = "The " + undoMsgHistory.pop() + " has been undone";

			undoClones.pop();
			feedback = new LogicToUi(status);

		} catch (IOException e) {
			feedback = new LogicToUi(ERROR_IO);
		} catch (WillNotWriteToCorruptFileException e) {
			feedback = new LogicToUi(ERROR_FILE_CORRUPTED);
		}


		return feedback;
	}
}
