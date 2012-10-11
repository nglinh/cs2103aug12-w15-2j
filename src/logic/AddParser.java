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
		groups = parser.parse(argument);
		if(groups.get(0).getDates().size()==0){
			return Task.TaskType.FLOATING;
		}
		else if(groups.get(0).getDates().size()==1){
			return Task.TaskType.DEADLINE;
		}
		else{
			return Task.TaskType.TIMED;
		}
	}
	public static DateTime getBeginTime(String argument){
		Date st = groups.get(0).getDates().get(0);
		System.out.println(st.toString());
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
		String temp = "";
		for(int i=start-6;i<start;++i){
			temp+=charArray[i];
		}
		if(temp.trim().compareTo("from")==0)
			return formatString(charArray,start-6);
		return formatString(charArray,start-1);
	}
	private static String formatString(char[] charArray, int endString){
		String result = new String("");
		for(int i=0;i<endString;++i){
			result += charArray[i];
		}
		return result;
	}
}
