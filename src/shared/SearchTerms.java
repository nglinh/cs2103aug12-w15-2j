package shared;
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
	
	//  may support if needed
	//	private boolean caseSensitive = false;

	//Have to make sure startDate is always before endDate
	private DateTime startRange = INVALID_DATE_FIELD;
	private DateTime endRange = INVALID_DATE_FIELD;
	
	public SearchTerms(String[] keywords) {
		assert(keywords != null);
		
		this.completedTasks = true;
		this.incompleteTasks = true;
		this.timedTasks = true;
		this.deadlineTasks = true;
		this.floatingTasks = true;
		
		this.keywords = keywords;
		
	}
	
	public SearchTerms(DateTime startDate, DateTime endDate) {
		assert(startDate != null);
		assert(endDate != null);
		
		this.completedTasks = true;
		this.incompleteTasks = true;
		this.timedTasks = true;
		this.deadlineTasks = true;
		this.floatingTasks = true;
		
		this.startRange = startDate;
		this.endRange = endDate;
		

		
	}
	
	public SearchTerms(String[] keywords, DateTime startDate, DateTime endDate) {
		assert(keywords != null);
		assert(startDate != null);
		assert(endDate != null);
		
		
		this.completedTasks = true;
		this.incompleteTasks = true;
		this.timedTasks = true;
		this.deadlineTasks = true;
		this.floatingTasks = true;
		
		this.startRange = startDate;
		this.endRange = endDate;
		
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

		this.startRange = startDate;
		this.endRange = endDate;

		
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
	
	public boolean doesSearchContainKeymords() {
		if(keywords.length == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean doesSearchContainDateRange() {
		if(startRange == INVALID_DATE_FIELD) {
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
	
	public DateTime getStartRange() {
		return startRange;
	}
	
	/**
	 * Get end range of search                           
	 *
	 * @return end range if valid or SearchTerms.INVALID_DATE_FIELD if not filled.           
	 *  
	 */
	
	public DateTime getEndRange() {
		return endRange;
	}
}

