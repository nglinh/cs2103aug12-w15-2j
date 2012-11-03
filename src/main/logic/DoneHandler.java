package main.logic;

import java.io.IOException;
import java.util.NoSuchElementException;

import main.shared.LogicToUi;
import main.shared.Task;
import main.storage.WillNotWriteToCorruptFileException;

public class DoneHandler extends CommandHandler{

	private String arguments;
	private DoneParser parser;

	public DoneHandler(String arguments) {
		super(arguments);
		this.arguments = arguments;
		parser = new DoneParser(arguments);
	}

	@Override
	public LogicToUi execute() {
		
		
		if (arguments.length() == 0) {
			return new LogicToUi(
					ERROR_INDEX_NUMBER_NOT_VALID);
		}

		

		int index;

		try{
			parser.parse();
			index = parser.index;
			index--; //To account for table index starting from 1

			if ((index < 0) || ((index + 1) > lastShownToUI.size())) {
				throw new NoSuchElementException();
			}

		} catch (NumberFormatException | NoSuchElementException e){
			return new LogicToUi(ERROR_INDEX_NUMBER_NOT_VALID);
		}

		boolean commandSuccess = false;
		try{
			
			pushCurrentTaskListToUndoStack();
			Task toBeUpdated =  lastShownToUI.get(index);

			if(toBeUpdated.isDone()){
				return new LogicToUi(taskToString(toBeUpdated) + " has been already been marked as done.");
			}


			toBeUpdated.done(true);
			int serial = toBeUpdated.getSerial();
			
			
			dataBase.update(serial, toBeUpdated);

			String taskDetails = taskToString(toBeUpdated);
			String undoMessage = "marking of task \"" + taskDetails + "\" as done";
			commandSuccess = true;
			pushUndoStatusMessage(undoMessage);
			return new LogicToUi(taskDetails + " has been marked as done.", serial);

		} catch (NoSuchElementException e) {
			return new LogicToUi(	ERROR_INDEX_NUMBER_NOT_VALID);
		} catch (IOException e) {
			return new LogicToUi(ERROR_IO);
		} catch (WillNotWriteToCorruptFileException e) {
			return new LogicToUi(ERROR_FILE_CORRUPTED);
		} finally {
			if(commandSuccess == false ) {
				popUndoClones();
			}
		}
	}

}
