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
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

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
	private int keyIdentifier = 1;
	private static GuiTrayIcon theOne;

	protected ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			log.severe("Couldn't find file: " + path);
			return null;
		}
	}


	
	public void initialize(){
		final TrayIcon trayIcon;
		
		Preferences prefs = Preferences.userNodeForPackage(this.getClass());
		prefs.addPreferenceChangeListener(new PreferenceChangeListener(){

			@Override
			public void preferenceChange(PreferenceChangeEvent arg0) {
				if(arg0.getKey() == GuiPreferences.SHORTCUT_KEY_EXTRA){
					JIntellitype.getInstance().unregisterHotKey(keyIdentifier);
					registerHotKey();
				}
			}
			
		});
		
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
		        		JIntellitype.getInstance().unregisterHotKey(keyIdentifier);
		        		JIntellitype.getInstance().cleanUp();
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
			
			registerHotKey();
			addHotKeyHandler();
		    
		    GuiMain2.getInstance().runUI();

		} else {

		    //  System Tray is not supported
			GuiMain2.getInstance().runUI();

		}
	}

	public void registerHotKey() {
		
		Preferences prefs = Preferences.userNodeForPackage(this.getClass());
		
		int modifierKeys = 0;
		if(prefs.getBoolean(GuiPreferences.SHORTCUT_KEY_CTRL, false)){
			modifierKeys += JIntellitype.MOD_CONTROL;
		}
		if(prefs.getBoolean(GuiPreferences.SHORTCUT_KEY_ALT, false)){
			modifierKeys += JIntellitype.MOD_ALT;
		}
		if(prefs.getBoolean(GuiPreferences.SHORTCUT_KEY_SHIFT, false)){
			modifierKeys += JIntellitype.MOD_SHIFT;
		}
		if(prefs.getBoolean(GuiPreferences.SHORTCUT_KEY_WIN, true)){
			modifierKeys += JIntellitype.MOD_WIN;
		}
		int extraKey = (int) prefs.get(GuiPreferences.SHORTCUT_KEY_EXTRA, "A").charAt(0);
		
		try{
			JIntellitype.getInstance().registerHotKey(keyIdentifier, modifierKeys,
					extraKey);			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void addHotKeyHandler(){
		JIntellitype.getInstance().addHotKeyListener(new HotkeyListener() {
	       	// listen for hotkey
			
	    	public void onHotKey(int aIdentifier) {
	    	    if (aIdentifier == keyIdentifier){
	    	       GuiQuickAdd.getInstance().showOrHideUI();
	    	    }
	    	}
	    });
	}
	
	public static void main(String[] args){
		new GuiTrayIcon().runUI();
	}
	
	private GuiTrayIcon(){
		initialize();
	}
	
	public static GuiTrayIcon getInstance(){
		if(theOne==null){
			theOne = new GuiTrayIcon();
		}
		return theOne;
	}



	@Override
	public void runUI() {
		// TODO Auto-generated method stub
		
	}
}
