package logic;

import org.joda.time.DateTime;
import shared.Task;
public class TaskDetails {
	private Task.TaskType taskType;
	private DateTime startTime,endTime;
	private String taskName;
	TaskDetails(){
		taskName = null;
		taskType = null;
		startTime = null;
		endTime = null;
	}
	public Task.TaskType getTaskType(){
		return this.taskType;
	}
	public DateTime getStartTime(){
		return this.startTime;
	}
	public DateTime getEndTime(){
		return this.endTime;
	}
	public void setStartTime(DateTime STime){
		this.startTime=STime;
	}
	public void setEndTime(DateTime ETime){
		this.endTime=ETime;
	}
	public void setTaskType(Task.TaskType TType){
		this.taskType=TType;
	}
	public void setTaskName(String TName){
		this.taskName = TName;
	}
}
