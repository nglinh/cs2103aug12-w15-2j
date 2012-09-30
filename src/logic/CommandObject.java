package logic;

import org.joda.time.DateTime;

public class CommandObject {
	private TaskDetails taskDetail;
	private Logic.CommandType commandType;
	
	CommandObject(){
		taskDetail = new TaskDetails();
		commandType = null;
	}
	public void setCommandType(Logic.CommandType TType){
		commandType = TType;
	}
	public void setEndTime(DateTime newEndTime){
		taskDetail.setEndTime(newEndTime);
	}
	public void setStartTime(DateTime newStartTime){
		taskDetail.setStartTime(newStartTime);
	}
	public void setTaskName(String newName){
		taskDetail.setTaskName(newName);
	}
}
