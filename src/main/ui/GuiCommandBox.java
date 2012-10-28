package main.ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
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

	private JFrame dummyFrame;
	protected JTextField txtCmd;
	protected JEditorPane txtStatus;
	protected JPopupMenu popupCmdHint;
	protected JEditorPane txtCmdHint;
	
	protected List<String> commandHistory;
	protected ListIterator<String> commandHistoryIterator;
	
	private enum HintPosEnum {ABOVE, BELOW, UNDEFINED};
	private HintPosEnum hintPos = HintPosEnum.UNDEFINED;
	private Rectangle previousWindowRect;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiCommandBox window = new GuiCommandBox();
					window.initialize();
					window.dummyFrame.setVisible(true);
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
		//initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	protected void initialize() {
		
		// Window
		dummyFrame = new JFrame();
		dummyFrame.setBounds(100, 100, 450, 300);
		dummyFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Text box for commands
		txtCmd = new JTextField();
		dummyFrame.getContentPane().add(txtCmd, BorderLayout.NORTH);
				
		// Popup for hints
		popupCmdHint = new JPopupMenu();
		addPopup(txtCmd, popupCmdHint);
		
		// Text box in popup for hints
		txtCmdHint = new JEditorPane();
		popupCmdHint.add(txtCmdHint);
		
        // Text box for status
		txtStatus = new JEditorPane();
				
		dummyFrame.getContentPane().add(txtStatus, BorderLayout.CENTER);
		
		configureWidgets(txtCmd, txtStatus, txtCmdHint, popupCmdHint);
	}
	
	protected void configureWidgets(final JTextField txtCmd, JEditorPane txtStatus, final JEditorPane txtCmdHint, final JPopupMenu popupCmdHint){
		this.txtCmd = txtCmd;
		this.txtStatus = txtStatus;
		this.txtCmdHint = txtCmdHint;
		this.popupCmdHint = popupCmdHint;
		
		txtCmd.setColumns(10);
		
		txtCmdHint.setEditable(false);
		txtCmdHint.setContentType("text/html");
		txtCmdHint.setBackground(new Color(0, 0, 0, 0));
		txtCmdHint.setOpaque(false);		
		
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
				JFrame frame = (JFrame) SwingUtilities.getRoot(txtCmd);
				Rectangle currentWindowRect = new Rectangle(frame.getLocationOnScreen(), frame.getSize());
				if(!currentWindowRect.equals(previousWindowRect)){
					hintPos = HintPosEnum.UNDEFINED;
					previousWindowRect = currentWindowRect;
				}
				

				boolean isCommand = false;
				for (String command : commandList){
					if (txtCmd.getText().startsWith(command)) {
						System.out.println(Hint.getInstance().helpForThisCommandHTML(command));
						txtCmdHint.setText("<html>"+Hint.getInstance().helpForThisCommandHTML(command)+"</html>");
						System.out.println(popupCmdHint.getSize());
						
						popupCmdHint.setPopupSize(500, (int) txtCmdHint.getPreferredSize().getHeight() + 20);
						if(hintPos == HintPosEnum.BELOW){							
							popupCmdHint.show(txtCmd, 5, txtCmd.getHeight());
						}else if(hintPos == HintPosEnum.ABOVE){
							popupCmdHint.show(txtCmd, 5, -1 * popupCmdHint.getHeight());
						}else{ // undefined
							popupCmdHint.show(txtCmd, 5, txtCmd.getHeight());
							hintPos = HintPosEnum.BELOW;
							if(popupCmdHint.getLocationOnScreen().getY() < txtCmd.getLocationOnScreen().getY()){
								popupCmdHint.setVisible(false);
								popupCmdHint.show(txtCmd, 5, -1 * popupCmdHint.getHeight());
								hintPos = HintPosEnum.ABOVE;
							}
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
		
		// Use HTMLEditorKit to style the HTML
		HTMLEditorKit kit = new HTMLEditorKit();
		txtCmdHint.setEditorKit(kit);

		StyleSheet hintStyleSheet = kit.getStyleSheet();
		hintStyleSheet.addRule("body, p {font-family:Segoe UI;}");
		hintStyleSheet
				.addRule("h1 {font-family:Segoe UI; margin:0px 0px 0px 0px; padding:0px 0px 0px 0px;}");
		hintStyleSheet
				.addRule("h2 {font-family:Segoe UI; margin:10px 0px 0px 0px; padding:0px 0px 0px 0px;}");
		hintStyleSheet.addRule("p {margin-top:5px;}");

		Document doc = kit.createDefaultDocument();
		txtCmdHint.setDocument(doc);
		
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
	}
	
	public void executeCommand(String text) {
		
		// Call command parser
		LogicToUi returnValue = sendCommandToLogic(text);

		// Set command text box to empty
		txtCmd.setText("");
		
		update(returnValue);
	}

	protected void showStatus(String status) {
		txtStatus
				.setText("<html><table align=\"center\"><tr><td valign=\"middle\" align=\"center\"><font size=\"4\">"
						+ HTMLEncoder.encode(status)
						+ " &nbsp;&nbsp;&nbsp;<a href=\"http://doit/undo\">undo</a></font></td></tr></table></html>");
	}
	
	public void update(LogicToUi returnValue){
		showStatus(returnValue.getString());
	}

	@Override
	public void runUI() {
		initialize();		
	}

	protected static void addPopup(Component component, final JPopupMenu popup) {
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
