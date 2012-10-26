package main.logic;

import org.joda.time.DateTime;

import com.joestelmach.natty.Parser;

import main.shared.Task.TaskType;

public class EditParser {
	public boolean willChangeType= false;
	public boolean willChangeStartTime = false;
	public boolean willChangeDeadline = false;
	public boolean willChangeEndTime = false;
	public boolean willChangeName = false;
	private String newName;
	private DateTime newStartTime;
	private DateTime newEndTime;
	private DateTime newDeadline;
	private TaskType newType;
	private Parser parser;
	public EditParser(String arguments) {
		String[] tempStringArray = arguments.split("-");
		for(int i =0;i<tempStringArray.length;++i){
			if(tempStringArray[i]!=null&&
					tempStringArray[i].compareTo("")!=0){
				String commandArgument = getFirstWord(tempStringArray[i]);
				String newField = removeFisrtWord(tempStringArray[i]);
				updateField(commandArgument,newField);
			}
		}
	}
	private void updateField(String commandArgument, String newField) {
		if(commandArgument.toLowerCase().compareTo("name")==0){
			willChangeName = true;
			newName = newField;
		}
		else if(commandArgument.toLowerCase().compareTo("begintime")==0){
			willChangeStartTime = true;
			newStartTime =  (new AddParser(newField)).getBeginTime();
		}
		else if(commandArgument.toLowerCase().compareTo("endtime")==0){
			willChangeEndTime = true;
			newEndTime = (new AddParser(newField).getBeginTime());
		}
		else if(commandArgument.toLowerCase().compareTo("tofloating")==0){
			willChangeType = true;
			newType = TaskType.FLOATING;
		}
		else if(commandArgument.toLowerCase().compareTo("deadline")==0){
			willChangeDeadline = true;
			newDeadline = (new AddParser(newField).getBeginTime());
		}

	}
	private String removeFisrtWord(String string) {
		return string.replaceFirst(string.split(" ")[0], "").trim();
	}
	private String getFirstWord(String string) {
		return string.split(" ")[0];
	}
	public String getNewName(){
		return newName;
	}
	public DateTime getNewStartTime(){
		return newStartTime;
	}
	public DateTime getNewEndTime(){
		return newEndTime;
	}
	public DateTime getNewDeadline(){
		return newDeadline;
	}
	public TaskType getNewType(){
		return newType;
	}
}
