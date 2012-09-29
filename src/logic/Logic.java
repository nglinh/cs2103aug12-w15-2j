package logic;
import ui.UI;
import shared.LogicToUi;
import storage.Database;
public class Logic {
	private static enum CommandType{ADD,DELETE};
	public UI doItUi;
	public Database dataBase;
	
	public LogicToUi uiCommunicator(String command){
		CommandType commandType = parseCommand(command); 
		LogicToUi feedback = executeCommand(commandType,command);
		return feedback;
	}
	
	private static CommandType parseCommand(String command) {
		// TODO Auto-generated method stub
		return null;
	}
	private static LogicToUi executeCommand(CommandType commandType,String arguments){
		LogicToUi feedback = new LogicToUi("Test");
		return feedback;
	}
	public Logic(UI userInterface){
		doItUi = userInterface;
		dataBase = new Database();
		
	}
	
}
