package shared;
/**  
 * SearchTerms.java 
 * A container for passing search terms from logic to database
 * @author  Yeo Kheng Meng
 */ 

import org.joda.time.DateTime;

public class SearchTerms {

	private boolean completedTasks = false;
	private boolean incompleteTasks = false;
	private boolean timedTasks = false;
	private boolean deadlineTasks = false;
	private boolean floatingTasks = false;

	private String keywords[] = null; //Not case sensitive
	
	//  may support if needed
	//	private boolean caseSensitive = false;

	//Have to make sure startDate is always before endDate
	private DateTime startRange = null;
	private DateTime endRange = null;

	public SearchTerms(boolean completedTasks,boolean incompleteTasks, 
			boolean timedTasks, boolean deadlineTasks, boolean floatingTasks) {

		this.completedTasks = completedTasks;
		this.incompleteTasks = incompleteTasks;
		this.timedTasks = timedTasks;
		this.deadlineTasks = deadlineTasks;
		this.floatingTasks = floatingTasks;
		
	}
	
	public SearchTerms(boolean completedTasks,boolean incompleteTasks, 
			boolean timedTasks, boolean deadlineTasks, boolean floatingTasks,
			String[] keywords) {

		this.completedTasks = completedTasks;
		this.incompleteTasks = incompleteTasks;
		this.timedTasks = timedTasks;
		this.deadlineTasks = deadlineTasks;
		this.floatingTasks = floatingTasks;

		this.keywords = keywords;		
	}
	
	public SearchTerms(boolean completedTasks,boolean incompleteTasks, 
			boolean timedTasks, boolean deadlineTasks, boolean floatingTasks,
			DateTime startDate, DateTime endDate) {
		
		if(startDate.isAfter(endDate)) {
			throw new IllegalArgumentException();
		}

		this.completedTasks = completedTasks;
		this.incompleteTasks = incompleteTasks;
		this.timedTasks = timedTasks;
		this.deadlineTasks = deadlineTasks;
		this.floatingTasks = floatingTasks;

		this.startRange = startDate;
		this.endRange = endDate;

		
	}

	public SearchTerms(boolean completedTasks,boolean incompleteTasks, 
			boolean timedTasks, boolean deadlineTasks, boolean floatingTasks,
			String[] keywords, DateTime startDate, DateTime endDate) {

		if(startDate.isAfter(endDate)) {
			throw new IllegalArgumentException();
		}
		
		this.completedTasks = completedTasks;
		this.incompleteTasks = incompleteTasks;
		this.timedTasks = timedTasks;
		this.deadlineTasks = deadlineTasks;
		this.floatingTasks = floatingTasks;


		this.keywords = keywords;
		this.startRange = startDate;
		this.endRange = endDate;

		
	}

	public boolean completeFlag() {
		return completedTasks;
	}

	public boolean incompleteFlag()	{
		return incompleteTasks;
	}

	public boolean timedFlag()	{
		return timedTasks;
	}

	public boolean deadlineFlag() {
		return deadlineTasks;
	}

	public boolean floatingFlag() {
		return floatingTasks;
	}


	public String[] getKeywords() {
		return keywords;
	}
	
	public DateTime getStartRange() {
		return startRange;
	}
	
	public DateTime getEndRange() {
		return endRange;
	}
}

