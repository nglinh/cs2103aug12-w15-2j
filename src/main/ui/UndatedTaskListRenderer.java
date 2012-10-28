package main.ui;

import java.util.ArrayList;
import java.util.List;
import main.shared.Task;

public class UndatedTaskListRenderer {
	
	private List<Task> taskList;
	
	private List<Integer> indexList;
	
	public UndatedTaskListRenderer(List<Task> taskList){
		this.taskList = taskList;
		
		indexList = new ArrayList<Integer>();
	}
	
	public String render(){
		
		StringBuffer sb = new StringBuffer();
		for(Task task : taskList){
			if (task.getType() == Task.TaskType.FLOATING){
				sb.append(renderTask(task));
			}
		}
		
		return sb.toString();
	}
	
	public String renderTask(Task t){
		StringBuffer sb = new StringBuffer();
		
		int taskIndex = taskList.indexOf(t)+1;
		String taskName = t.getTaskName();
		
		sb.append("<table cellpadding=0 cellspacing=0 class=\"taskbox\" width=100%>");
		sb.append("<tr><td rowspan=2><font size=5>"+HTMLEncoder.encode(taskName)+"</font></td><td width=1 align=center>" + taskIndex + "</td></tr>");
		sb.append("<tr><td valign=top align=center><input type=checkbox></td></tr>");
		sb.append("</table>");
		
		indexList.add(taskIndex);
		
		return sb.toString();
	}
	
	public List<Integer> getIndexList(){
		return indexList;
	}

}
