package main.logic;

import java.util.List;

import org.joda.time.DateTime;

import com.joestelmach.natty.Parser;
import com.joestelmach.natty.DateGroup;

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
	public EditParser(String arguments) throws CannotParseDateException {
		String[] tempStringArray = arguments.split("-");
		for(int i =0;i<tempStringArray.length;++i){
			if(tempStringArray[i]!=null&&
					tempStringArray[i].length()!=0){
				String commandArgument = getFirstWord(tempStringArray[i]);
				String updatedField = removeFisrtWord(tempStringArray[i]);
				updateField(commandArgument,updatedField);
			}
		}
	}
	private void updateField(String commandArgument, String newField) throws CannotParseDateException {
		parser = new Parser();
		List<DateGroup> groupsOfDates;
		groupsOfDates = parser.parse(newField);
		switch(commandArgument.toLowerCase()){
		case "name":
			//fall through
		case "n":
			willChangeName = true;
			newName = newField;
			break;
		case "begintime":
			//fall through
		case "starttime":
			//fall through
		case "stime":
			//fall through
		case "st":
			willChangeStartTime = true;
			if(groupsOfDates.size()!=0){
				newStartTime =  new DateTime(groupsOfDates.get(0).getDates().get(0));
			}
			else{
				throw new CannotParseDateException();
			}
			break;
		case "endtime":
			//fall through
		case "finishtime":
			//fall through
		case "et":
			//fall through
		case "ft":
			willChangeEndTime = true;
			if(groupsOfDates.size()!=0){
				newEndTime =  new DateTime(groupsOfDates.get(0).getDates().get(0));
			}
			else{
				throw new CannotParseDateException();
			}
			break;
		case "deadline":
			//fall through
		case "d":
			willChangeDeadline = true;
			if(groupsOfDates.size()!=0){
				newDeadline =  new DateTime(groupsOfDates.get(0).getDates().get(0));
			}
			else{
				throw new CannotParseDateException();
			}
			break;
		case "tofloating":
			//fall through
		case "tofloat":
			willChangeType = true;
			newType = TaskType.FLOATING;
			break;
		}
	}
	private String removeFisrtWord(String string){
		return string.replaceFirst(getFirstWord(string), "").trim();
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
