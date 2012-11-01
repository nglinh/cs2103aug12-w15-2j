package main.logic;

import java.io.IOException;

import main.shared.LogicToUi;
import main.shared.Task;
import main.storage.WillNotWriteToCorruptFileException;

public class DeleteHandler extends CommandHandler {
	private Task toBeDeleted;
	private DeleteParser parser;
	DeleteHandler(String str){
		parser = new DeleteParser(str);
	}

	public LogicToUi execute() {

		try {
			parser.parse();
			if (parser.isAll) {
				dataBase.deleteAll();
				feedback = new LogicToUi("All tasks have been deleted");
			} else if (parser.isDone) {
				dataBase.deleteDone();
				feedback = new LogicToUi(
						"All completed tasks have been deleted");
			} else if (parser.isOver) {
				dataBase.deleteOver();
				feedback = new LogicToUi(
						"All tasks that has ended before this moment have been deleted");
			} else {
				toBeDeleted = parser.getToBeDeleted();
				dataBase.delete(toBeDeleted.getSerial());
				String taskDetails = taskToString(toBeDeleted);
				feedback = new LogicToUi(taskDetails + " has been deleted");
			}

		} catch (IOException e) {
			feedback = new LogicToUi(ERROR_IO);
		} catch (WillNotWriteToCorruptFileException e) {
			feedback = new LogicToUi(ERROR_FILE_CORRUPTED);
		}

		return feedback;

	}
}
