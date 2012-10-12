package logic;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import org.joda.time.DateTime;

import shared.Task;
import shared.Task.TaskType;
public class AddParser {
	public static Parser parser;
	public static List<DateGroup> groups;
	public static TaskType getType(String argument){
		parser = new Parser(TimeZone.getDefault());
		String temp = reverseString(argument);
		temp = removeFrom(temp);	
		argument = reverseString(temp);
		groups = parser.parse(argument);
		if(groups.size()==0){
			return Task.TaskType.FLOATING;
		}
		else if(groups.get(0).getDates().size()==1){
			return Task.TaskType.DEADLINE;
		}
		else{
			return Task.TaskType.TIMED;
		}
	}
	private static String removeFrom(String temp) {
		String[] stringArray = temp.split(" ");
		for(int i =0;i<stringArray.length;++i){
			if(stringArray[i].trim().compareTo("from")==0
					|| stringArray[i].trim().compareTo("by")==0){
					stringArray[i]= "";
					break;
				}
		}
		String result = "";
		for(int i=0;i<stringArray.length;++i)
			result = result+stringArray[i]+" ";
		return result;
	}
	public static DateTime getBeginTime(String argument){
		Date st = groups.get(0).getDates().get(0);
		DateTime result = new DateTime(st);
		return result;
	}
	public static DateTime getEndTime(String argument){
		Date et = groups.get(0).getDates().get(1);
		DateTime result = new DateTime(et);
		return result;
	}
	public static String getTaskName(String argument){
		int start  = groups.get(0).getPosition();
		char[] charArray = argument.toCharArray();
		return formatString(charArray,start-1);
	}
	private static String formatString(char[] charArray, int endString){
		String result = new String("");
		for(int i=0;i<endString;++i){
			result += charArray[i];
		}
		return result;
	}
	private static String reverseString(String str){
		String[] stringArray = str.trim().split(" ");
		String temp = "";
		for(int i =stringArray.length-1;i>=0;--i){
			temp = temp + stringArray[i]+" ";
		}
		return temp;
	}
}
