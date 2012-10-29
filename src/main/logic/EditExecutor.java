package main.logic;

import java.io.IOException;
import java.util.NoSuchElementException;

import main.shared.LogicToUi;
import main.shared.Task;
import main.shared.Task.TaskType;
import main.storage.WillNotWriteToCorruptFileException;

public class EditExecutor extends CommandExecutor{
	private static final String ERROR_MUST_CHANGE_BOTH_TIME = "In order to change to timed task, you need to specify" +
			"both start time and end time.";
	private EditParser editParser;
	private Task toBeEdited;
	public EditExecutor(EditParser parsr){
		editParser = parsr;
		toBeEdited = editParser.getToBeEdited();
	}

	@Override
	public LogicToUi execute(){
		try{
			LogicToUi feedback;
			feedback = checkParseResult();
			if(feedback != null){
				return feedback;
			}
			changeDeadline();
			changename();
			changeStartTime();
			changeEndTime();
			changeToFloat();
			dataBase.update(toBeEdited.getSerial(), toBeEdited);
			//pushCommandToUndoHistoryStack();
			return new LogicToUi(taskToString(toBeEdited) + " updated.",toBeEdited.getSerial());
		}catch (NoSuchElementException e) {
			return new LogicToUi(
					"Sorry this index number or parameter you provided is not valid. " +
					"Please try again with a correct number or refresh the list.");
		} catch (IOException e) {
			return new LogicToUi(ERROR_IO);
		} catch (WillNotWriteToCorruptFileException e) {
			return new LogicToUi(ERROR_FILE_CORRUPTED);
		}
	}

	private void changeToFloat() {
		if(editParser.willChangeType){
			toBeEdited.changetoFloating();
		}
	}

	private void changeEndTime() {
		if(editParser.willChangeEndTime){
			if(toBeEdited.getType()!=TaskType.TIMED){
				toBeEdited.changetoTimed(editParser.getNewStartTime(), editParser.getNewEndTime());
			}
			else{
				toBeEdited.changeStartAndEndDate(toBeEdited.getStartDate(), editParser.getNewEndTime());
			}
		}
	}

	private void changeStartTime() {
		if(editParser.willChangeStartTime){
			if(toBeEdited.getType()!=TaskType.TIMED){
				toBeEdited.changetoTimed(editParser.getNewStartTime(), editParser.getNewEndTime());
			}
			else{
				toBeEdited.changeStartAndEndDate(editParser.getNewStartTime(), toBeEdited.getEndDate());
			}
		}
	}

	private void changename() {
		if(editParser.willChangeName){
			toBeEdited.changeName(editParser.getNewName());
		}
	}

	private void changeDeadline() {
		if(editParser.willChangeDeadline){
			if(toBeEdited.getType()!= TaskType.DEADLINE){
				toBeEdited.changetoDeadline(editParser.getNewDeadline());
			}
			else{
				toBeEdited.changeDeadline(editParser.getNewDeadline());
			}
		}
	}

	private LogicToUi checkParseResult() {
		if(!editParser.isIndexValid){
			return new LogicToUi("Please check your index. It's not in the list.");
		}
		if(!editParser.canParseDeadline){
			return new LogicToUi("Please check the new deadline. I cannot parse it :(.");
		}
		if(!editParser.canParseName){
			return new LogicToUi("Please check the new name. It cannot be empty.");
		}
		if(!editParser.canParseEndTime){
			return new LogicToUi("Please check the new end time. I cannot parse it :(.");
		}
		if(!editParser.canParseStartTime){
			return new LogicToUi("Please check the new start time. I cannot parse it :(.");
		}
		if(toBeEdited.getType()!=TaskType.TIMED){
			if((!editParser.willChangeStartTime&&editParser.willChangeEndTime)){
				return new LogicToUi(ERROR_MUST_CHANGE_BOTH_TIME);
			}
		}
		return null;
	}
}
