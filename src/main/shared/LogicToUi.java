//@author A0081007U
package main.shared;
/**  
 * LogicToUi.java 
 * A class for to hold information returned from the Logic to the UI
 * @author  Yeo Kheng Meng
 */ 

import java.util.List;


public class LogicToUi {

	public static final int INVALID_SERIAL = Integer.MIN_VALUE; 
	
	public static enum SortStatus {TYPE, DONE, START, END, NAME };
	private boolean sortReverse = false;
	
	private String output = null;
	private List<Task> display = null;
	private SearchTerms filters = null;
	
	private int lastChangedSerial = INVALID_SERIAL;  
	
	
	private SortStatus currentSorting = null;

	
	
	public LogicToUi(String output)	{
		assert(output != null);
		this.output = output;
	}
	
	public LogicToUi(String output, int lastChangedSerial){
		assert(output != null);
		this.output = output;
		this.lastChangedSerial = lastChangedSerial;
	}
	
	public LogicToUi(List<Task> display, String output) {
		assert(display != null);
		assert(output != null);
		
		this.display = display;
		this.output = output;

		
	}
	
	public LogicToUi(List<Task> display, String output, SearchTerms filters) {
		assert(display != null);
		assert(output != null);
		assert(filters != null);
		
		this.display = display;
		this.output = output;
		this.filters = filters;
		
	}
	
	public LogicToUi(List<Task> display, String output, SortStatus sorting, boolean reverse) {
		assert(display != null);
		assert(output != null);

		this.display = display;
		this.output = output;
		this.currentSorting = sorting;
		this.sortReverse = reverse;
		
	}
	
	
	public LogicToUi(List<Task> display, String output, SearchTerms filters, SortStatus sorting, boolean reverse) {
		assert(display != null);
		assert(output != null);
		assert(filters != null);
		
		this.display = display;
		this.output = output;
		this.filters = filters;
		this.currentSorting = sorting;
		this.sortReverse = reverse;
		
	}
	
	
	
	public boolean containsSortStatus(){
		if(this.currentSorting == null) {
			return false;
		} else {
			return true;
		}
	}
	
	
	public SortStatus getSortStatus(){
		return currentSorting;
	}
	
	public boolean getReverseSortStatus() {
		return sortReverse;
	}
	
	
	
	public boolean containsFilters(){
		if(this.filters == null) {
			return false;
		} else {
			return true;
		}
	}
	
	public SearchTerms getFilters(){
		return filters;
	}
	
	
	public boolean containsList() {
		if (this.display == null) {
			return false;
		} else {
			return true;
		}
	}
	
	
	public String getString() {
		return output;
	}
	
	public List<Task> getList() {
		return display;
	}
	
	public boolean containsLastChangedSerial(){
		if(lastChangedSerial == INVALID_SERIAL){
			return false;
		} else {
			return true;
		}
	}
	
	public int getLastChangedSerial(){
		return lastChangedSerial;
	}
}
