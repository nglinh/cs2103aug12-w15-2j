package main.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import main.shared.Task;
import main.shared.Task.TaskType;

public class DatedTaskListRenderer{
	
	private List<Task> taskList;
	Map<DateTime, List<Task>> datesWithTasks;

	private List<Integer> indexList;
	
	public DatedTaskListRenderer(){
	}
	
	public DatedTaskListRenderer(List<Task> taskList){
		this.taskList = taskList;
		
		indexList = new ArrayList<Integer>();
	}
	
	public String render(){
		generateDatesWithTasks();
		
		StringBuffer sb = new StringBuffer();
		for(Map.Entry<DateTime, List<Task>> entry : datesWithTasks.entrySet()){
			DateTime currentDay = entry.getKey();
			List<Task> tasksOnCurrentDay = entry.getValue();
			
			sb.append("<a name=\"date-"+DateTimeFormat.forPattern("yyyy-M-d").print(currentDay)+"\"></a>");
			sb.append("<table width=100% \">");
			sb.append("<tr>");
			
			sb.append("<td width=1 valign=top>");
			sb.append(renderDate(currentDay));
			sb.append("</td>");
			sb.append("<td valign=top>");
			for (Task t : tasksOnCurrentDay){
				sb.append(renderTask(t, currentDay));
			}
			sb.append("</td>");
			
			sb.append("</tr>");
			sb.append("</table>");
		}
		
		//System.out.println(sb.toString());
		
		return sb.toString();
	}
	
	public String renderDate(DateTime dt){
		
		StringBuffer sb = new StringBuffer();
		/*sb.append("<table>");
		sb.append("<tr><td>"+dt.getDayOfWeek()+"</td></tr>");
		sb.append("<tr><td>"+dt.getMonthOfYear()+"</td></tr>");
		sb.append("<tr><td>"+dt.getDayOfMonth()+"</td></tr>");
		sb.append("<tr><td>"+dt.getYear()+"</td></tr>");
		sb.append("</table>");*/
		/*sb.append("<table cellpadding=0 cellspacing=0 class=\"calendarbox\">");
		sb.append("<tr><td align=center><font size=3>"+DateTimeFormat.forPattern("EEE").print(dt).toUpperCase()+"</font></td></tr>");
		sb.append("<tr><td align=center><font size=3>"+DateTimeFormat.forPattern("MMM").print(dt).toUpperCase()+"</font></td></tr>");
		sb.append("<tr><td align=center><font size=6>"+dt.getDayOfMonth()+"</font></td></tr>");
		sb.append("<tr><td align=center><font size=3>"+dt.getYear()+"</font></td></tr>");
		sb.append("</table>");*/
		sb.append("<table cellpadding=0 cellspacing=0 class=\"calendarbox\">");
		sb.append("<tr><td align=center class=\"calendardayofweek\"><font size=3>"+DateTimeFormat.forPattern("EEE").print(dt).toUpperCase()+"</font></td></tr>");
		sb.append("<tr><td align=center><font size=6>"+dt.getDayOfMonth()+"</font></td></tr>");
		sb.append("<tr><td align=center><font size=3>"+DateTimeFormat.forPattern("MMM").print(dt).toUpperCase()+"&nbsp;"+dt.getYear()+"</font></td></tr>");
		sb.append("</table>");
		
		return sb.toString();
	}
	
	public String renderTask(Task t, DateTime currentDay){
		StringBuffer sb = new StringBuffer();
		
		int taskIndex = taskList.indexOf(t)+1;
		String taskName = t.getTaskName();
		
		sb.append("<table cellpadding=0 cellspacing=0 class=\"taskbox\" width=100%>");
		sb.append("<tr><td>"+renderTaskDate(t, currentDay)+"</td><td width=1 align=center>" + taskIndex + "</td></tr>");
		sb.append("<tr>");
		sb.append("<td><font size=5>"+HTMLEncoder.encode(taskName)+"</font></td>");
		if(t.isDone()){
			sb.append("<td valign=middle align=center><input type=checkbox checked></td>");
		}else{
			sb.append("<td valign=middle align=center><input type=checkbox></td>");
		}
		sb.append("</tr>");
		sb.append("</table>");
		
		indexList.add(taskIndex);
		
		return sb.toString();
	}
	
	public String renderTaskDate(Task t, DateTime currentDay) {
		DateTime nearStartOfCurrentDay = currentDay.withTime(0, 0, 1, 0);
		DateTime nearEndOfCurrentDay = currentDay.withTime(23, 58, 59, 0);
		
		if (t.getType() == TaskType.DEADLINE) {
			
			if (t.getDeadline().isAfter(nearEndOfCurrentDay)){
				return "By today";
			}else{
				return "By "
						+ DateTimeFormat.forPattern("h:mma").print(t.getDeadline())
								.toLowerCase();
			}
			
		} else if (t.getType() == TaskType.TIMED) {
			
			boolean isMultiDayTask = !t.getStartDate().withTimeAtStartOfDay().equals(t.getEndDate().withTimeAtStartOfDay());

			if(isMultiDayTask){
								
				if (t.getStartDate().isBefore(nearStartOfCurrentDay)
						&& t.getEndDate().isAfter(nearEndOfCurrentDay)) {
					return "All day";
				}
				else if(t.getStartDate().isBefore(nearStartOfCurrentDay)){
					// Task ends today
					return "Ends at " + DateTimeFormat.forPattern("h:mma").print(t.getEndDate()).toLowerCase();
				}
				else if(t.getEndDate().isAfter(nearEndOfCurrentDay)){
					// Task starts today
					return "Starts at " + DateTimeFormat.forPattern("h:mma").print(t.getStartDate()).toLowerCase();
				}
				
				/*
				String startTime, endTime;
				
				if(t.getStartDate().withTimeAtStartOfDay().equals(currentDay)){
					startTime = "This day " + DateTimeFormat.forPattern("h:mma").print(t.getStartDate()).toLowerCase();
				}else{
					startTime = DateTimeFormat.forPattern("EEE dd MMM yyyy").print(t.getStartDate()) + " " +
							DateTimeFormat.forPattern("h:mma").print(t.getStartDate()).toLowerCase();
				}
				
				if(t.getEndDate().withTimeAtStartOfDay().equals(currentDay)){
					endTime = "This day " + DateTimeFormat.forPattern("h:mma").print(t.getEndDate()).toLowerCase();				
				}else{
					endTime = DateTimeFormat.forPattern("EEE dd MMM yyyy").print(t.getEndDate()) + " " +
							DateTimeFormat.forPattern("h:mma").print(t.getEndDate()).toLowerCase();
				}
				return startTime + " to " + endTime;
				*/
				
			}else{
				if (t.getStartDate().isBefore(nearStartOfCurrentDay)
						&& t.getEndDate().isAfter(nearEndOfCurrentDay)) {
					return "All day";
				}
				else{
					return DateTimeFormat.forPattern("h:mma").print(t.getStartDate()).toLowerCase() + " to "
							+ DateTimeFormat.forPattern("h:mma").print(t.getEndDate()).toLowerCase();
				}
			}

			// Case 1: Task starts today and ends today
			// Case 2: Task started before today and ends today
			// Case 3: Task started
			
			/*
			 * Case 1: Task starts today and ends today
			 * - All day
			 * Case 2: Task started before today and ends today
			 * All day until 25
			 * 
			 */
		}
		return null;
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
		
		/*for(Map.Entry<DateTime, List<Task>> entry : datesWithTasks.entrySet()){
			System.out.println(entry.getKey());
			for (Task t : entry.getValue()){
				System.out.println(t.getTaskName());
			}
			System.out.println("");
		}*/
	}
	
	public void addTaskToMap(DateTime dateTime, Task task){
		if(datesWithTasks.containsKey(dateTime)){
			datesWithTasks.get(dateTime).add(task);
		}else{
			datesWithTasks.put(dateTime, new LinkedList<Task>());
			datesWithTasks.get(dateTime).add(task);
		}
	}
	
	public List<Integer> getIndexList(){
		return indexList;
	}
	
}
