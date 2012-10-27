package main.ui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;

public class GuiTrayIcon extends UI {

	protected ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	public void runUI(){
		final TrayIcon trayIcon;
		
		if (SystemTray.isSupported()) {

		    SystemTray tray = SystemTray.getSystemTray();
		    Image image = createImageIcon("icon.png","").getImage();
		    
		    //Toolkit.getDefaultToolkit().getImage("computer.gif");

		    MouseListener mouseListener = new MouseListener() {
		                
		        public void mouseClicked(MouseEvent e) {
		            System.out.println("Tray Icon - Mouse clicked!");
		            //GuiQuickAdd.getInstance().runUI();
		        }

		        public void mouseEntered(MouseEvent e) {
		            System.out.println("Tray Icon - Mouse entered!");                 
		        }

		        public void mouseExited(MouseEvent e) {
		            System.out.println("Tray Icon - Mouse exited!");                 
		        }

		        public void mousePressed(MouseEvent e) {
		            System.out.println("Tray Icon - Mouse pressed!");                 
		        }

		        public void mouseReleased(MouseEvent e) {
		            System.out.println("Tray Icon - Mouse released!");                 
		        }
		    };

		    ActionListener exitListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	System.out.println(e.getActionCommand());
		            System.out.println("Exiting...");
		            System.exit(0);
		        }
		    };
		    
		    ActionListener mainWindowListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	GuiMain.getInstance().runUI();
		        }
		    };
		    
		    ActionListener mainWindow2Listener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	GuiMain2.getInstance().runUI();
		        }
		    };
		    
		    ActionListener quickAddWindowListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	GuiQuickAdd.getInstance().runUI();
		        }
		    };
		            
		    PopupMenu popup = new PopupMenu();
		    MenuItem mainWindowItem = new MenuItem("Main Window");
		    mainWindowItem.addActionListener(mainWindowListener);
		    MenuItem mainWindow2Item = new MenuItem("Main Window ver 2");
		    mainWindow2Item.addActionListener(mainWindow2Listener);
		    MenuItem quickAddWindowItem = new MenuItem("Quick Add");
		    quickAddWindowItem.addActionListener(quickAddWindowListener);
		    MenuItem exitItem = new MenuItem("Exit");
		    exitItem.addActionListener(exitListener);
		    popup.add(mainWindowItem);
		    popup.add(mainWindow2Item);
		    popup.add(quickAddWindowItem);
		    popup.add(exitItem);

		    trayIcon = new TrayIcon(image, "DoIt!", popup);

		    ActionListener actionListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		            trayIcon.displayMessage("Action Event", 
		                "An Action Event Has Been Performed!",
		                TrayIcon.MessageType.INFO);
		        }
		    };
		            
		    trayIcon.setImageAutoSize(true);
		    trayIcon.addActionListener(actionListener);
		    trayIcon.addMouseListener(mouseListener);

		    try {
		        tray.add(trayIcon);
		    } catch (AWTException e) {
		        System.err.println("TrayIcon could not be added.");
		    }
		    
		    GuiMain.getInstance().runUI();

		} else {

		    //  System Tray is not supported
			GuiMain.getInstance().runUI();

		}
		
	}
	
	public static void main(String[] args){
		new GuiTrayIcon().runUI();
	}
}
