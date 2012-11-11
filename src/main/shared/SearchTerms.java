//@author A0081007U
package main.shared;
/**  
 * SearchTerms.java 
 * A container for passing search terms from logic to database
 * @author  Yeo Kheng Meng
 */ 

import org.joda.time.DateTime;

public class SearchTerms {

	public static final DateTime INVALID_DATE_FIELD = new DateTime(Long.MAX_VALUE);
	
	
	private boolean completedTasks = false;
	private boolean incompleteTasks = false;
	private boolean timedTasks = false;
	private boolean deadlineTasks = false;
	private boolean floatingTasks = false;

	private String keywords[] = new String[0]; //Not case sensitive

	private DateTime startDate = INVALID_DATE_FIELD;
	private DateTime endDate = INVALID_DATE_FIELD;
	
	
	public SearchTerms(String[] keywords) {
		assert(keywords != null);
		
		this.keywords = keywords;
		
	}
	
	public SearchTerms(DateTime startDate, DateTime endDate) {
		assert(startDate != null);
		assert(endDate != null);
		assert(startDate.isBefore(endDate) || startDate.isEqual(endDate));
		
		this.startDate = startDate;
		this.endDate = endDate;
		

		
	}
	
	public SearchTerms(String[] keywords, DateTime startDate, DateTime endDate) {
		assert(keywords != null);
		assert(startDate != null);
		assert(endDate != null);
		assert(startDate.isBefore(endDate) || startDate.isEqual(endDate));
		
		this.startDate = startDate;
		this.endDate = endDate;
		
		this.keywords = keywords;
		
	}
	
	
	
	

	public SearchTerms(boolean completedTasks, boolean incompleteTasks, 
			boolean timedTasks, boolean deadlineTasks, boolean floatingTasks) {

		this.completedTasks = completedTasks;
		this.incompleteTasks = incompleteTasks;
		this.timedTasks = timedTasks;
		this.deadlineTasks = deadlineTasks;
		this.floatingTasks = floatingTasks;
		
	}
	
	public SearchTerms(boolean completedTasks, boolean incompleteTasks, 
			boolean timedTasks, boolean deadlineTasks, boolean floatingTasks,
			String[] keywords) {

		assert(keywords != null);	
		
		this.completedTasks = completedTasks;
		this.incompleteTasks = incompleteTasks;
		this.timedTasks = timedTasks;
		this.deadlineTasks = deadlineTasks;
		this.floatingTasks = floatingTasks;

		this.keywords = keywords;		
	}
	
	public SearchTerms(boolean completedTasks, boolean incompleteTasks, 
			boolean timedTasks, boolean deadlineTasks, boolean floatingTasks,
			DateTime startDate, DateTime endDate) {
		
		assert(startDate != null);
		assert(endDate != null);
		
		assert(startDate.isBefore(endDate) || startDate.isEqual(endDate));

		this.completedTasks = completedTasks;
		this.incompleteTasks = incompleteTasks;
		this.timedTasks = timedTasks;
		this.deadlineTasks = deadlineTasks;
		this.floatingTasks = floatingTasks;

		this.startDate = startDate;
		this.endDate = endDate;

		
	}

	public SearchTerms(boolean completedTasks, boolean incompleteTasks, 
			boolean timedTasks, boolean deadlineTasks, boolean floatingTasks,
			String[] keywords, DateTime startDate, DateTime endDate) {

		
		assert(keywords != null);
		assert(startDate != null);
		assert(endDate != null);
		
		assert(startDate.isBefore(endDate) || startDate.isEqual(endDate));
		
		this.completedTasks = completedTasks;
		this.incompleteTasks = incompleteTasks;
		this.timedTasks = timedTasks;
		this.deadlineTasks = deadlineTasks;
		this.floatingTasks = floatingTasks;


		this.keywords = keywords;
		this.startDate = startDate;
		this.endDate = endDate;

		
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
	
	public boolean doesSearchContainKeywords() {
		if(keywords.length == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean doesSearchContainDateRange() {
		if(startDate.equals(INVALID_DATE_FIELD)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Get search keywords                           
	 *
	 * @return array of keywords or size 0 array if not filled          
	 *  
	 */


	public String[] getKeywords() {
		return keywords;
	}
	
	/**
	 * Get start range of search                           
	 *
	 * @return start range if valid or SearchTerms.INVALID_DATE_FIELD if not filled.           
	 *  
	 */
	
	public DateTime getStartDate() {
		return startDate;
	}
	
	/**
	 * Get end range of search                           
	 *
	 * @return end range if valid or SearchTerms.INVALID_DATE_FIELD if not filled.           
	 *  
	 */
	
	public DateTime getEndDate() {
		return endDate;
	}
}

