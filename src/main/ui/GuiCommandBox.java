package main.ui;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.BorderLayout;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import main.shared.LogicToUi;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class GuiCommandBox extends UI{

	private JFrame frame;
	private JTextField txtCmd;
	private JEditorPane txtStatus;
	private JPopupMenu popupCmdHint;
	private JEditorPane txtCmdHint;
	
	private List<String> commandHistory;
	private ListIterator<String> commandHistoryIterator;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiCommandBox window = new GuiCommandBox();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GuiCommandBox() {
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
	private void initialize() {
		
		// Window
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Text box for commands
		txtCmd = new JTextField();
		frame.getContentPane().add(txtCmd, BorderLayout.NORTH);
		txtCmd.setColumns(10);
		
		// Handler for key presses in command box
		commandHistory = new LinkedList<String>();
		commandHistoryIterator = commandHistory.listIterator();		
		txtCmd.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				if (arg0.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
					commandHistory.add(txtCmd.getText());
					commandHistoryIterator = commandHistory.listIterator(commandHistory.size());
					
					executeCommand(txtCmd.getText());
					popupCmdHint.setVisible(false);
				} else if (arg0.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
					if (commandHistoryIterator.hasPrevious()) {
						txtCmd.setText(commandHistoryIterator.previous());
						showHint();
					}
				} else if (arg0.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) {
					if (commandHistoryIterator.hasNext()) {
						txtCmd.setText(commandHistoryIterator.next());
						showHint();
					}
				} else {
					showHint();
				}
			}

			private void showHint() {
				List<String> commands = Hint.getInstance().getCommands();
				boolean isCommand = false;
				for (String command : commands){
					if (txtCmd.getText().startsWith(command)) {
						System.out.println(Hint.getInstance().helpForThisCommandHTML(command));
						txtCmdHint.setText("<html>"+Hint.getInstance().helpForThisCommandHTML(command)+"</html>");
						//popupCmdHint.setPopupSize(500, 300);
						popupCmdHint.setPopupSize(500, (int) txtCmdHint.getPreferredSize().getHeight() + 20);
						popupCmdHint.show(txtCmd, 5, txtCmd.getHeight());
						if(popupCmdHint.getLocationOnScreen().getY() < txtCmd.getLocationOnScreen().getY()){
							popupCmdHint.show(txtCmd, 5, -1 * popupCmdHint.getHeight());
						}
						txtCmd.requestFocus();
						isCommand = true;
					} 
				}
				if(isCommand == false){
					popupCmdHint.setVisible(false);
				}
			}
		});
		
		// Popup for hints
		popupCmdHint = new JPopupMenu();
		addPopup(txtCmd, popupCmdHint);
		
		// Text box in popup for hints
		txtCmdHint = new JEditorPane();
		txtCmdHint.setEditable(false);
		txtCmdHint.setContentType("text/html");
		txtCmdHint.setBackground(new Color(0, 0, 0, 0));
		txtCmdHint.setOpaque(false);
		popupCmdHint.add(txtCmdHint);
		
		// Use HTMLEditorKit to style the HTML
		HTMLEditorKit kit = new HTMLEditorKit();
		txtCmdHint.setEditorKit(kit);

        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body, p {font-family:Segoe UI;}");
        styleSheet.addRule("h1 {font-family:Segoe UI; margin:0px 0px 0px 0px; padding:0px 0px 0px 0px;}");
        styleSheet.addRule("h2 {font-family:Segoe UI; margin:10px 0px 0px 0px; padding:0px 0px 0px 0px;}");
        styleSheet.addRule("p {margin-top:5px;}");
        
        Document doc = kit.createDefaultDocument();
        txtCmdHint.setDocument(doc);
		
        // Text box for status
		txtStatus = new JEditorPane();
		txtStatus.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent event) {
				if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					if (event.getURL().toString().endsWith("/undo")) {
						executeCommand("undo");
					}
				}
			}
		});
		txtStatus.setBackground(new Color(0, 0, 0, 0));
		txtStatus.setOpaque(false);
		txtStatus.setEditable(false);
		txtStatus.setContentType("text/html");
		
		frame.getContentPane().add(txtStatus, BorderLayout.CENTER);
	}
	
	public void executeCommand(String text) {
		
		// Call command parser
		LogicToUi returnValue = sendCommandToLogic(text);

		// Set command text box to empty
		txtCmd.setText("");
		
		update(returnValue);
	}

	private void showStatus(String status) {
		txtStatus
				.setText("<html><table align=\"center\"><tr><td valign=\"middle\" align=\"center\"><font size=\"4\">"
						+ status
						+ " &nbsp;&nbsp;&nbsp;<a href=\"http://doit/undo\">undo</a></font></td></tr></table></html>");
	}
	
	public void update(LogicToUi returnValue){
		showStatus(returnValue.getString());
	}

	@Override
	public void runUI() {
		initialize();		
	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				//popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
