package main.ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.BorderLayout;
import javax.swing.JEditorPane;

import main.shared.LogicToUi;

import java.awt.Window.Type;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class GuiQuickAdd extends GuiCommandBox{

	private JFrame frmDoit;
	private JTextField txtCmd;
	private JEditorPane txtStatus;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiQuickAdd window = new GuiQuickAdd();
					window.frmDoit.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private static GuiQuickAdd theOne;

	public static GuiQuickAdd getInstance() {
		if (theOne == null) {
			theOne = new GuiQuickAdd();
		}
		return theOne;
	}
	
	/**
	 * Create the application.
	 */
	private GuiQuickAdd() {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public void initialize() {
		frmDoit = new JFrame();
		frmDoit.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				
			}
		});
		frmDoit.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				txtCmd.requestFocus();
			}
		});
		frmDoit.setTitle("DoIt! Quick Add");
		frmDoit.setType(Type.UTILITY);
		frmDoit.setAlwaysOnTop(true);
		frmDoit.setBounds(100, 100, 220, 180);
		
		txtCmd = new JTextField();
		txtCmd.setText("Type a new task here...");
		txtCmd.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (txtCmd.getText().equals("Type a new task here...")) {
					txtCmd.selectAll();
				}
			}
		});
		frmDoit.getContentPane().add(txtCmd, BorderLayout.SOUTH);
		txtCmd.setColumns(10);
		
		txtStatus = new JEditorPane();
		txtStatus.setEditable(false);
		txtStatus.setBackground(new Color(0, 0, 0, 0));
		txtStatus.setOpaque(false);
		frmDoit.getContentPane().add(txtStatus, BorderLayout.CENTER);
		
		// Popup for hints
		popupCmdHint = new JPopupMenu();
		popupCmdHint.setFocusable(false);
		addPopup(txtCmd, popupCmdHint);

		// Text box in popup for hints
		txtCmdHint = new JEditorPane();
		txtCmdHint.setFocusTraversalKeysEnabled(false);
		txtCmdHint.setFocusCycleRoot(false);
		txtCmdHint.setFocusable(false);
		popupCmdHint.add(txtCmdHint);
		
		configureWidgets(txtCmd, txtStatus, txtCmdHint, popupCmdHint);
		
		// Position window
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle maxWin = ge.getMaximumWindowBounds();
        int x = (int) maxWin.getMaxX() - frmDoit.getWidth();
        int y = (int) maxWin.getMaxY() - frmDoit.getHeight();
        frmDoit.setLocation(x, y);
	}
	
	public void runUI(){
		frmDoit.setVisible(true);
		txtCmd.requestFocus();
	}
	
	public void showOrHideUI(){
		if(frmDoit.isVisible()){
			frmDoit.setVisible(false);
		}else{
			frmDoit.setVisible(true);
			txtCmd.requestFocus();
		}
	}
	
	public void executeCommand(String text) {

		// Call command parser
		LogicToUi returnValue = sendCommandToLogic(text);

		// Set command text box to empty
		txtCmd.setText("Type a new task here...");
		txtCmd.selectAll();

		txtStatus.setText(returnValue.getString());
		
		// Update other windows
		//GuiMain.getInstance().updateWindow(this);
		//GuiMain2.getInstance().updateWindow(this);
		GuiUpdate.update(this);
	}

	public void updateWindow(Object source) {
		// TODO Auto-generated method stub
		
	}

}
