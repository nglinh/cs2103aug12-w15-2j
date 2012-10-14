package logic;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.joda.time.DateTime;

import shared.Task.TaskType;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

public class AddParser {
	private List<DateGroup> groups;
	private Parser parser;
	private String argument;
	private TaskType taskType;
	private int matchingPosition;
	public AddParser(String str){
		argument = str;
		String dateString = removeTriggerWord(argument);
		matchingPosition = 0;
		parser = new Parser(TimeZone.getDefault());
		groups = parser.parse(dateString);
		if(this.getTaskType()!= TaskType.FLOATING){
			int location = groups.get(0).getPosition();
			matchingPosition = location;
			while(!checkFullWord(dateString,location)){
				char[] tempCharArray = dateString.toCharArray();
				String tempString = "";
				int i = location;
				while(i<tempCharArray.length && tempCharArray[i]!=' '){
					i++;
				}
				if(i == tempCharArray.length){
					dateString = argument.trim();
					location = argument.length();
					matchingPosition = location;
					groups = parser.parse("");
					break;
				}
				matchingPosition += i-location+1;
				for(int k = i;k<dateString.length();k++){
					tempString = tempString + tempCharArray[k];
				}
				dateString = tempString.trim();
				groups = parser.parse(dateString);
				location = groups.get(0).getPosition();
				matchingPosition += location;
			}
		}
	}
	private String removeTriggerWord(String argument2) {
		String result = "";
		String[] tempStringArray = argument2.split(" ");
		for (int i =0;i<tempStringArray.length;++i){
			if(tempStringArray[i].compareTo("from")!=0 &&
					tempStringArray[i].compareTo("by")!=0 &&
					tempStringArray[i].compareTo("before")!=0)
				result = result+ tempStringArray[i]+" ";
		}
		return result;
	}
	private boolean checkFullWord(String dateString, int location) {
		char[] tempCharArray = dateString.toCharArray();
		String matchingValue = groups.get(0).getText();
		char[] matchingArray = matchingValue.toCharArray();
		if(location+matchingArray.length < tempCharArray.length &&
				tempCharArray[location+matchingArray.length]!= ' ')
			return false;
		return true;
	}
	public TaskType getTaskType(){
		if(groups.size()==0)
			taskType = TaskType.FLOATING;
		else if(groups.get(0).getDates().size()==1){
			taskType =  TaskType.DEADLINE;
		}
		else 
			taskType = TaskType.TIMED;
		return taskType;
	}
	public String getTaskDescription(){
		if(taskType == TaskType.FLOATING)
			return argument;
		else{
			return buildString(argument,this.matchingPosition);
		}
	}
	private String buildString(String argument2, int matchingPosition2) {
		String result = "";
		char[] tempCharArray = argument2.toCharArray();
		for(int i =0;i<matchingPosition2;++i)
			result = result+tempCharArray[i];
		result = result.trim();
		String[] tempStringArray = result.split(" ");
		if(tempStringArray[tempStringArray.length-1]=="from"||
				tempStringArray[tempStringArray.length-1]=="by"||
				tempStringArray[tempStringArray.length-1]=="before"){
			tempStringArray[tempStringArray.length-1] = "";
			result = "";
			for(int i=0;i<tempStringArray.length;++i)
				result = result + tempStringArray[i]+" ";
			result = result.trim();
		}
		return result;
	}
	public DateTime getBeginTime(){
		Date st = this.getGroups().get(0).getDates().get(0);
		DateTime result = new DateTime(st);
		return result;
	}
	public DateTime getEndTime(){
		Date et = this.getGroups().get(0).getDates().get(1);
		DateTime result = new DateTime(et);
		return result;
	}
	public List<DateGroup> getGroups(){
		return this.groups;
	}
}
