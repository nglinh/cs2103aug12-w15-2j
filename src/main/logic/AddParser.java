package main.logic;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.joda.time.DateTime;

import main.shared.Task.TaskType;

import com.joestelmach.natty.CalendarSource;
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
		String dateString;
		if(hasApostrophePair()){
			dateString = removeDescription(argument);
		}
		else{
			dateString = removeTriggerWord(argument);
		}
		matchingPosition1 = 0;
		endDateStringPosition = 0;
		parser = new Parser(TimeZone.getDefault());
		CalendarSource.setBaseDate(new DateTime().withTime(23,59,00,00).toDate());
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
	private String removeDescription(String argument2) {
		String result = "";
		for(int i =argument2.indexOf("\"");i<=argument2.lastIndexOf("\"");++i)
			result+= " ";
		if(argument2.lastIndexOf("\"")<argument2.length()-1){
			for(int i = argument2.lastIndexOf("\"")+1;i<argument2.length();++i)
				result+= argument2.toCharArray()[i];
		}
		return result;
	}
	private boolean hasApostrophePair() {
		if(argument.indexOf("\"")==argument.lastIndexOf("\""))
			return false;
		return true;
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
			if(tempStringArray[i].compareTo("from")==0 || 
					tempStringArray[i].compareTo("this")==0)
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
	public String getTaskDescription() throws EmptyDescriptionException{
		if(!hasApostrophePair()){
			if(taskType == TaskType.FLOATING)
				return argument;
			else{
				return buildString(argument,this.matchingPosition1);
			}
		}
		else{
			int temp1 = argument.indexOf("\"");
			int temp2 = argument.lastIndexOf("\"");
			if(temp1 == temp2){
				if(taskType == TaskType.FLOATING)
					return argument;
				else{
					return buildString(argument,this.matchingPosition1);
				}
			}
			else{
				return getTaskDescriptionInsideApostrophe(temp1, temp2);
			}

		}
	}
	private String getTaskDescriptionInsideApostrophe(int temp1, int temp2) {
		String result = "";
		for(int i =temp1+1;i<=temp2-1;++i){
			result+= argument.toCharArray()[i];
		}
		result.trim();
		return result;
	}
	private String buildString(String argument, int matchingPosition) throws EmptyDescriptionException {
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
			result = "";
			for(int i=0;i<tempStringArray.length-1;++i)
				result = result + tempStringArray[i]+" ";
		}
		for(int i = endDateStringPosition;i<argument.length();i++){
			result+= tempCharArray[i];
		}
		result = result.trim();
		if(result.compareTo("")==0)
			throw new EmptyDescriptionException();
		else
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
