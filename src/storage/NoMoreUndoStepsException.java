package storage;
/**  
 * NoMoreUndoStepsException.java 
 * An exception class returned when Database has no more return steps
 * @author  Yeo Kheng Meng
 */ 


public class NoMoreUndoStepsException extends Exception{
	private static final long serialVersionUID = 1L;

		public NoMoreUndoStepsException() {
		  }

		  public NoMoreUndoStepsException(String msg) {
		    super(msg);
		  }
		

}
