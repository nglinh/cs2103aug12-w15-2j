package main.ui;

public class GuiUpdate {
	
	public static void update(Object source){
		GuiMain2.getInstance().updateWindow(source);
		GuiMain.getInstance().updateWindow(source);
		GuiQuickAdd.getInstance().updateWindow(source);
	}

}
