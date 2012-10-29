package main.ui;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;

import main.shared.Task;

public class CalendarRenderer{
	
	private List<Task> taskList;
	private DateTime dt;
	private TreeMap<DateTime, List<Task>> datesWithTasks;
	
	public CalendarRenderer(DateTime dt, List<Task> taskList){
		this.dt = dt;
		this.taskList = taskList;
	}
	

	public String render(){
		
		generateDatesWithTasks();
		
		int month = dt.getMonthOfYear();
		int year = dt.getYear();
		
		// Get the first Sunday before the first of the month as the starting day
		DateTime startingDay = dt.withDayOfMonth(1).withTimeAtStartOfDay();
		while(startingDay.getDayOfWeek() != DateTimeConstants.SUNDAY){
			startingDay = startingDay.minusDays(1);
		}
		System.out.println(startingDay);
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("<table width=100%><tr>");
		sb.append("<td width=1><a href=\"http://doit/gotoMonth/"+DateTimeFormat.forPattern("yyyy-M").print(dt.minusMonths(1))+"\">&lt;</a></td>");
		sb.append("<td align=center>"+DateTimeFormat.forPattern("MMMM yyyy").print(dt)+"</td>");
		sb.append("<td width=1><a href=\"http://doit/gotoMonth/"+DateTimeFormat.forPattern("yyyy-M").print(dt.plusMonths(1))+"\">&gt;</a></td></tr></table>");
		sb.append("<table width=100% class=calendar cellpadding=1 cellspacing=1>");
		sb.append("<tr><td>S</td><td>M</td><td>T</td><td>W</td><td>T</td><td>F</td><td>S</td></tr>");
		sb.append("<tr>");
		
		DateTime currentDay = startingDay;
		//while(currentDay.isBefore(dt.withDayOfMonth(1).plusMonths(1).withTimeAtStartOfDay())){
		for (int i=0; i<42; i++){
			if(currentDay != startingDay && currentDay.getDayOfWeek() == DateTimeConstants.SUNDAY){
				sb.append("</tr><tr>");
			}
			System.out.println(currentDay + " " + startingDay.plusMonths(1));
			if(datesWithTasks.containsKey(currentDay.withTimeAtStartOfDay())){
				sb.append("<td align=right><a href=\"http://doit/showTasksForDay/"+DateTimeFormat.forPattern("yyyy-M-d").print(currentDay)+"\">"+currentDay.getDayOfMonth()+"</a></td>");
			}else{
				sb.append("<td align=right>"+currentDay.getDayOfMonth()+"</td>");
			}

			currentDay = currentDay.plusDays(1);
		}		
		sb.append("</tr>");
		sb.append("</table>");
		return sb.toString();		
		
	}
	
	public void generateDatesWithTasks(){		

		// We collect a list of ALL the dates that the tasks occur on
		// And we add the tasks occurring on that day to a list
		datesWithTasks = new TreeMap<DateTime, List<Task>>();
		for(Task task : taskList){
			if (task.getType() == Task.TaskType.TIMED){
				DateTime currentDay = task.getStartDate().withTimeAtStartOfDay();
				addTaskToMap(currentDay, task);
				
				while(task.getEndDate().withTimeAtStartOfDay().isAfter(currentDay)){
					currentDay = currentDay.plusDays(1).withTimeAtStartOfDay();
					addTaskToMap(currentDay, task);
				}
			}
			else if (task.getType() == Task.TaskType.DEADLINE){
				addTaskToMap(task.getDeadline().withTimeAtStartOfDay(), task);
			}
		}
		
		for(Map.Entry<DateTime, List<Task>> entry : datesWithTasks.entrySet()){
			System.out.println(entry.getKey());
			for (Task t : entry.getValue()){
				System.out.println(t.getTaskName());
			}
			System.out.println("");
		}
	}
	
	public void addTaskToMap(DateTime dateTime, Task task){
		if(datesWithTasks.containsKey(dateTime)){
			datesWithTasks.get(dateTime).add(task);
		}else{
			datesWithTasks.put(dateTime, new LinkedList<Task>());
			datesWithTasks.get(dateTime).add(task);
		}
	}
	
}
