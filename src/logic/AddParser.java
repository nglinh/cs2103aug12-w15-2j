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
	private int matchingPosition1;
	private int endDateStringPosition;
	public AddParser(String str){
		argument = str;
		String dateString = removeTriggerWord(argument);
		matchingPosition1 = 0;
		endDateStringPosition = 0;
		parser = new Parser(TimeZone.getDefault());
		groups = parser.parse(dateString);
		if(this.getTaskType()!= TaskType.FLOATING){
			int location = groups.get(0).getPosition();
			matchingPosition1 = location;
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
					matchingPosition1 = location;
					groups = parser.parse("");
					break;
				}
				matchingPosition1 += i-location+1;
				for(int k = i;k<dateString.length();k++){
					tempString = tempString + tempCharArray[k];
				}
				dateString = tempString.trim();
				groups = parser.parse(dateString);
				location = groups.get(0).getPosition();
				matchingPosition1 += location;
			}
			determineEndOfDateString();

			//adjustTimeBasedOnTriggerWords();
		}
	}
	private void determineEndOfDateString() {
		if(this.getTaskType()!=TaskType.FLOATING){
			String tempString = groups.get(0).getText();
			endDateStringPosition = matchingPosition1+tempString.length()+1;

		}

	}
	/*private void adjustTimeBasedOnTriggerWords() {
		char[] tempCharArray = argument.toCharArray();
		String tempString = "";
		for(int i=0;i<matchingPosition;++i){
			tempString += tempCharArray[i];
		}
	    String[] tempStringArray = tempString.split(" ");
	    int len = tempStringArray.length;
	    if(tempStringArray[len-1].compareTo("before")==0){
	    	groups.get(0).getDates().get(0)
	    }

	}*/
	private String removeTriggerWord(String argument2) {
		String result = "";
		String[] tempStringArray = argument2.split(" ");
		for (int i =0;i<tempStringArray.length;++i){
			if(tempStringArray[i].compareTo("from")==0 )
				result = result + "    ";
			else if(tempStringArray[i].compareTo("by")==0)
				result = result + "   ";
			else if(tempStringArray[i].compareTo("before")==0)
				result = result + "       ";
			else if(tempStringArray[i].compareTo("on")==0||
					tempStringArray[i].compareTo("at")==0)
				result = result +"   ";
			else
				result = result+ tempStringArray[i]+" ";
		}
		return result.trim();
	}
	private boolean checkFullWord(String dateString, int location) {
		char[] tempCharArray = dateString.toCharArray();
		String matchingValue = groups.get(0).getText();
		char[] matchingArray = matchingValue.toCharArray();
		if((location+matchingArray.length < tempCharArray.length &&
				tempCharArray[location+matchingArray.length] != ' ')||
				location!=0 && tempCharArray[location-1]!=' ')
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
			return buildString(argument,this.matchingPosition1);
		}
	}
	private String buildString(String argument, int matchingPosition) {
		String result = "";
		char[] tempCharArray = argument.toCharArray();
		for(int i =0;i<matchingPosition;++i)
			result = result+tempCharArray[i];
		String[] tempStringArray = result.split(" ");
		if(tempStringArray[tempStringArray.length-1].compareTo("from")==0||
				tempStringArray[tempStringArray.length-1].compareTo("by")==0||
				tempStringArray[tempStringArray.length-1].compareTo("before")==0||
				tempStringArray[tempStringArray.length-1].compareTo("on")==0||
				tempStringArray[tempStringArray.length-1].compareTo("at")==0){
			tempStringArray[tempStringArray.length-1] = "";
			result = "";
			for(int i=0;i<tempStringArray.length;++i)
				result = result + tempStringArray[i]+" ";
		}
		for(int i = endDateStringPosition;i<argument.length();i++){
			result+= tempCharArray[i];
		}
		result = result.trim();
		return result;
	}
	public DateTime getBeginTime(){
		Date st = this.getGroups().get(0).getDates().get(0);
		DateTime time1 = new DateTime(st);
		if(groups.get(0).getDates().size()==2){
			Date et = groups.get(0).getDates().get(1);
			DateTime time2 = new DateTime(et);
			if(time2.isBefore(time1)){
				return time2;
			}
			else
				return time1;
		}
		return time1;
	}
	public DateTime getEndTime(){
		Date et = this.getGroups().get(0).getDates().get(1);
		DateTime time2 = new DateTime(et);
		if(groups.get(0).getDates().size()==2){
			Date st = this.getGroups().get(0).getDates().get(0);
			DateTime time1 = new DateTime(st);
			if(time2.isBefore(time1)){
				return time1;
			}
			else
				return time2;
		}
		return time2;
	}
	public List<DateGroup> getGroups(){
		return this.groups;
	}
}
