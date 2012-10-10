package logic;


import org.joda.time.DateTime;

import shared.Task;
import shared.Task.TaskType;
import com.mdimension.jchronic.*;
import com.mdimension.jchronic.utils.Span;

public class AddParser {
	public static TaskType getType(String argument){
		Span time = Chronic.parse(argument);
		if(time == null){
			return Task.TaskType.FLOATING;
		}
		else if(time.isSingularity()){
			return Task.TaskType.DEADLINE;
		}
		else{
			return Task.TaskType.TIMED;
		}
	}
	public static DateTime getBeginTime(String argument){
		DateTime dt = new DateTime(Chronic.parse(argument).getBegin());
		return dt;
	}
	public static DateTime getEndTime(String argument){
		DateTime dt = new DateTime(Chronic.parse(argument).getBegin());
		return dt;
	}
	public static String getTaskName(String argument){
		String[] strArray = argument.split(" ");
		for(int i=0;i<strArray.length;i++){
			String temp = cutString(strArray,i);
			if(Chronic.parse(temp)==null)
				return formatString(strArray,i-1);
		}
		throw new NullPointerException();
	}
	private static String cutString(String[] strArray,int startString){
		String result = new String();
		for(int i=startString;i<strArray.length;++i){
			result.concat(strArray[i]);
		}
		return result;
	}
	private static String formatString(String[] strArray, int endString){
		String result = new String();
		for(int i=0;i<endString;++i){
			result.concat(strArray[i]);
		}
		return result;
	}
}
