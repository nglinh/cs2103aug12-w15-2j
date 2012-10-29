package main.ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.JFrame;
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

public class GuiQuickAdd extends UI{

	private JFrame frmDoit;
	private JTextField textCmd;
	private JEditorPane textStatus;

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
		frmDoit.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textCmd.requestFocus();
			}
		});
		frmDoit.setTitle("DoIt! Quick Add");
		frmDoit.setType(Type.UTILITY);
		frmDoit.setAlwaysOnTop(true);
		frmDoit.setBounds(100, 100, 220, 180);
		
		textCmd = new JTextField();
		textCmd.setText("Type a new task here...");
		textCmd.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (textCmd.getText().equals("Type a new task here...")) {
					textCmd.selectAll();
				}
			}
		});
		textCmd.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				if (arg0.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
					executeCommand(textCmd.getText());
				}
			}
		});
		frmDoit.getContentPane().add(textCmd, BorderLayout.SOUTH);
		textCmd.setColumns(10);
		
		textStatus = new JEditorPane();
		textStatus.setEditable(false);
		textStatus.setBackground(new Color(0, 0, 0, 0));
		textStatus.setOpaque(false);
		frmDoit.getContentPane().add(textStatus, BorderLayout.CENTER);
		
		// Position window
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle maxWin = ge.getMaximumWindowBounds();
        int x = (int) maxWin.getMaxX() - frmDoit.getWidth();
        int y = (int) maxWin.getMaxY() - frmDoit.getHeight();
        frmDoit.setLocation(x, y);
	}
	
	public void runUI(){
		frmDoit.setVisible(true);
	}
	
	public void executeCommand(String text) {

		// Call command parser
		LogicToUi returnValue = sendCommandToLogic("add " +text);

		// Set command text box to empty
		textCmd.setText("Type a new task here...");
		textCmd.selectAll();

		textStatus.setText(returnValue.getString());
		
		// Update other windows
		GuiMain.getInstance().updateWindow(this);
		GuiMain2.getInstance().updateWindow(this);
	}

	public void updateWindow(Object source) {
		// TODO Auto-generated method stub
		
	}

}
