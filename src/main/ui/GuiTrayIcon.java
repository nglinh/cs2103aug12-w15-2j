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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

import main.LogHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class GuiTrayIcon extends UI {
	
	Logger log = LogHandler.getLogInstance();
	File dllFile;

	protected ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			log.severe("Couldn't find file: " + path);
			return null;
		}
	}

	public void runUI(){
		final TrayIcon trayIcon;
		
		if(checkFilePermissions().contains("lock")){
			setUiLookAndFeel();
			JOptionPane.showMessageDialog(null, "DoIt! is already running! Check your system tray for DoIt's icon.");
			exit();
		}
		
		if (SystemTray.isSupported()) {

		    SystemTray tray = SystemTray.getSystemTray();
		    Image image = createImageIcon("/resource/icon.png","").getImage();
		    
		    //Toolkit.getDefaultToolkit().getImage("computer.gif");

		    MouseListener mouseListener = new MouseListener() {
		                
		        public void mouseClicked(MouseEvent e) {
		            log.info("Tray Icon - Mouse clicked!");
		            //GuiQuickAdd.getInstance().runUI();
		        }

		        public void mouseEntered(MouseEvent e) {
		        	log.info("Tray Icon - Mouse entered!");                 
		        }

		        public void mouseExited(MouseEvent e) {
		        	log.info("Tray Icon - Mouse exited!");                 
		        }

		        public void mousePressed(MouseEvent e) {
		        	log.info("Tray Icon - Mouse pressed!");                 
		        }

		        public void mouseReleased(MouseEvent e) {
		        	log.info("Tray Icon - Mouse released!");                 
		        }
		    };

		    ActionListener exitListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	log.info(e.getActionCommand());
		        	log.info("Exiting...");
		        	
		        	try{
		        		JIntellitype.getInstance().unregisterHotKey(1);
				    }catch(Exception e1){
				    }
		            exit();
		        }
		    };
		    
		    ActionListener mainWindowListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	GuiMain2.getInstance().runUI(GuiMain2.CARD_LIST);
		        }
		    };
		    
		    ActionListener mainWindow2Listener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	GuiMain2.getInstance().runUI(GuiMain2.CARD_AGENDA);
		        }
		    };
		    
		    ActionListener quickAddWindowListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	GuiQuickAdd.getInstance().runUI();
		        }
		    };
		            
		    PopupMenu popup = new PopupMenu();
		    MenuItem mainWindowItem = new MenuItem("List View");
		    mainWindowItem.addActionListener(mainWindowListener);
		    MenuItem mainWindow2Item = new MenuItem("Agenda View");
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
		        	/*
		            trayIcon.displayMessage("Action Event", 
		                "An Action Event Has Been Performed!",
		                TrayIcon.MessageType.INFO); */
		        	GuiQuickAdd.getInstance().runUI();
		        }
		    };
		            
		    trayIcon.setImageAutoSize(true);
		    trayIcon.addActionListener(actionListener);
		    trayIcon.addMouseListener(mouseListener);

		    try {
		        tray.add(trayIcon);
		    } catch (AWTException e) {
		    	log.severe("TrayIcon could not be added.");
		    }
		    
		    // Extract DLL to temporary folder
			try {
				dllFile = File.createTempFile("Doit-JIntellitype-", ".dll");			
			    FileOutputStream dllFileWriter = new FileOutputStream(dllFile);
			    InputStream dllStream;
			    if(System.getProperty("os.arch").equals("amd64")){
			    	dllStream = this.getClass().getResourceAsStream("/resource/JIntellitype64.dll");
			    }else{
			    	dllStream = this.getClass().getResourceAsStream("/resource/JIntellitype.dll");
			    }
			    byte[] b = new byte[1024];
			    int len;
			    while((len = dllStream.read(b)) >= 0){
			    	dllFileWriter.write(b, 0, len);
			    }
			    dllFileWriter.close();
			    dllStream.close();
			    JIntellitype.setLibraryLocation(dllFile.getAbsolutePath());
			    dllFile.deleteOnExit();
			} catch (IOException e1) {
				log.log(Level.WARNING, "Unable to extract JIntelliType DLL", e1);
			}
		    
		    try{
		    	JIntellitype.getInstance().registerHotKey(1, JIntellitype.MOD_WIN,
						(int) 'A');
		    	JIntellitype.getInstance().addHotKeyListener(new HotkeyListener() {
		           	// listen for hotkey
			    	public void onHotKey(int aIdentifier) {
			    	    if (aIdentifier == 1){
			    	       GuiQuickAdd.getInstance().showOrHideUI();
			    	    }
			    	}
			    });
		    }catch(Exception e){
		    	e.printStackTrace();
		    }
		    
		    GuiMain2.getInstance().runUI();

		} else {

		    //  System Tray is not supported
			GuiMain2.getInstance().runUI();

		}
		
	}
	
	public static void main(String[] args){
		new GuiTrayIcon().runUI();
	}
}
