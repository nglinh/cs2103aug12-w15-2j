package main.logic;

import java.io.IOException;
import java.util.NoSuchElementException;

import main.shared.LogicToUi;
import main.shared.Task;
import main.storage.WillNotWriteToCorruptFileException;

public class UndoneHandler extends CommandHandler{

	private String arguments;
	private UndoneParser parser;

	public UndoneHandler(String arguments) {
		super(arguments);
		this.arguments = arguments;
		parser = new UndoneParser(arguments);
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

			if(!toBeUpdated.isDone()){
				return new LogicToUi(taskToString(toBeUpdated) + " has been already been marked as undone.");
			}


			toBeUpdated.done(false);
			int serial = toBeUpdated.getSerial();
			
			
			dataBase.update(serial, toBeUpdated);

			String taskDetails = taskToString(toBeUpdated);
			
			String undoMessage = "marking of task \"" + taskDetails + "\" as undone";
			commandSuccess = true;
			pushUndoStatusMessage(undoMessage);
			
			return new LogicToUi(taskDetails + " has been marked as undone.", serial);

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
