package main.ui;

import java.util.ArrayList;
import java.util.List;

import main.shared.Task;

public class UndatedTaskListRenderer {
	
	private List<Task> taskList;
	
	private List<Integer> indexList;
	
	private int highlightSerial = -1;
	
	public UndatedTaskListRenderer(List<Task> taskList){
		this.taskList = taskList;
		
		indexList = new ArrayList<Integer>();
	}
	
	public void setHighlightSerial(int highlightSerial) {
		this.highlightSerial = highlightSerial;
	}
	
	public String render(){
		
		StringBuffer sb = new StringBuffer();
		int index = 1;
		
		for(Task task : taskList){
			if (task.getType() == Task.TaskType.FLOATING){
				sb.append(renderTask(task, index));
			}
			index++;
		}
		
		return sb.toString();
	}
	
	public String renderTask(Task t, int index){
		StringBuffer sb = new StringBuffer();
		
		int taskIndex = index;
		String taskName = t.getTaskName();
		
		String cssClass = "taskbox";
		
		if(highlightSerial == t.getSerial()){
			cssClass = "taskboxhighlight";
			sb.append("<div class=separatorfirst><a name=\"highlight\"></a></div>");
		}
		
		sb.append("<table cellpadding=0 cellspacing=0 class=\""+cssClass+"\" width=100%>");
		sb.append("<tr><td rowspan=2><span class=\"taskDescription\"><a href=\"http://doit/editTask/"+taskIndex+"\">"+HTMLEncoder.encode(taskName)+"</a></span></td><td width=1 align=center>" + taskIndex + "</td></tr>");
		//sb.append("<tr><td valign=top align=center><input type=checkbox></td></tr>");
		if(t.isDone()){
			sb.append("<tr><td valign=top align=center><input type=checkbox checked></td></tr>");
		}else{
			sb.append("<tr><td valign=top align=center><input type=checkbox></td></tr>");
		}
		sb.append("</table>");
		
		indexList.add(taskIndex);
		
		return sb.toString();
	}
	
	public List<Integer> getIndexList(){
		return indexList;
	}

}
