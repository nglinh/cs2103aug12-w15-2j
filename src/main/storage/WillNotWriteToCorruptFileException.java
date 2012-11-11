package main.storage;
/**  
 * WillNotWriteToCorruptFileException.java 
 * An exception class returned when Database refuses to write to a corrupted file to preserve user data
 * @author  Yeo Kheng Meng
 */ 


public class WillNotWriteToCorruptFileException extends Exception{

	private static final long serialVersionUID = 1L;

		public WillNotWriteToCorruptFileException() {
		  }

		  public WillNotWriteToCorruptFileException(String msg) {
		    super(msg);
		  }
		

}