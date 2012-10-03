package ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenu;
import javax.swing.JEditorPane;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.NoSuchElementException;

public class GuiMain extends UI{

	private JFrame frmDoit;
	private JTextField textCmd;
	private JScrollPane scrollPane;
	private JTable table;
	private JPopupMenu popupCmdHint;
	private JPopupMenu popupStatus;
	private JMenu mnSomeMenu;
	private JEditorPane txtCmdHint;
	private JEditorPane txtStatus;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiMain window = new GuiMain();
					window.frmDoit.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GuiMain() {
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
		frmDoit = new JFrame();
		frmDoit.setTitle("DoIt!");
		frmDoit.setBounds(100, 100, 450, 300);
		frmDoit.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frmDoit.setJMenuBar(menuBar);
		
		mnSomeMenu = new JMenu("Some Menu");
		menuBar.add(mnSomeMenu);
		
		
		
		textCmd = new JTextField();
		textCmd.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				if (arg0.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
					String strTaskName;
					
					String[] words = textCmd.getText().split("\\W");
					strTaskName = textCmd.getText().substring(words[0].length()+1);
					
					int popupWidth = 300;
					int popupHeight = 60;
					
					txtStatus.setText("<html><table align=\"center\"><tr><td valign=\"middle\" align=\"center\" height=\""+ (popupHeight-10 /*TODO:*/) +"\"><font size=\"4\">Task <b>"+strTaskName+"</b> added &nbsp;&nbsp;&nbsp;<a href=\"http://doit/undo\">undo</a> &nbsp;<a href=\"http://doit/close\">close</a></font></td></tr></table></html>");
					
					popupStatus.setPopupSize(popupWidth, popupHeight);
					popupStatus.show(frmDoit, (frmDoit.getWidth() - popupWidth)/2, frmDoit.getHeight() - popupHeight + 5);
					textCmd.setText("");
				} else if (textCmd.getText().startsWith("add")) {
					txtCmdHint.setText("<html>\r\n<font face=\"Tahoma, Arial, Sans-serif\">\r\n<font size=\"4\">\r\n<b>add</b><br>\r\nAdds a new task<br>\r\n<br>\r\nExamples:<br>\r\n</font>\r\n<font size=\"3\">\r\n<b>add</b> Meeting <b>from</b> 2pm 25/9 <b>to</b> 3pm 25/9<br>\r\n<b>add</b> Complete report <b>by</b> 5pm 25/9<br>\r\n<b>add</b> Search for document<br>\r\n</font>\r\n</font>\r\n</html>");
					popupCmdHint.setPopupSize(200, 120);
					popupCmdHint.show(textCmd, 5, textCmd.getHeight());
					textCmd.requestFocus();
				} else if (textCmd.getText().startsWith("list")) {
					txtCmdHint.setText("<html>\r\n<font face=\"Tahoma, Arial, Sans-serif\">\r\n<font size=\"4\">\r\n<b>list</b><br>\r\nList tasks<br>\r\n<br>\r\nExamples:<br>\r\n</font>\r\n<font size=\"3\">\r\n<b>list</b><br><b>list all</b><br><b>list done</b><br><b>list etc.</b>\r\n</font>\r\n</font>\r\n</html>");
					popupCmdHint.setPopupSize(200, 140);
					popupCmdHint.show(textCmd, 5, textCmd.getHeight());
					textCmd.requestFocus();
				} else {
					popupCmdHint.setVisible(false);
				}
			}
		});
		frmDoit.getContentPane().add(textCmd, BorderLayout.SOUTH);
		textCmd.setColumns(10);
		
		popupCmdHint = new JPopupMenu();
		addPopup(textCmd, popupCmdHint);
		
		txtCmdHint = new JEditorPane();
		txtCmdHint.setContentType("text/html");
		popupCmdHint.add(txtCmdHint);
		
		scrollPane = new JScrollPane();
		frmDoit.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
				{new Integer(1), null, "Test", "Test", "Test"},
			},
			new String[] {
				"Idx", "Done?", "Start/Deadline", "End", "What to do?"
			}
		) {
			Class[] columnTypes = new Class[] {
				Integer.class, Boolean.class, Object.class, Object.class, String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		table.getColumnModel().getColumn(0).setPreferredWidth(26);
		table.getColumnModel().getColumn(1).setPreferredWidth(20);
		table.getColumnModel().getColumn(2).setPreferredWidth(101);
		table.getColumnModel().getColumn(3).setPreferredWidth(126);
		table.getColumnModel().getColumn(4).setPreferredWidth(159);
		
		popupStatus = new JPopupMenu();
		addPopup(scrollPane, popupStatus);
		scrollPane.setViewportView(table);
		
		txtStatus = new JEditorPane();
		popupStatus.add(txtStatus);
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
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	@Override
	public void runUI() {
		initialize();
		// TODO: Initialize Logic
	}

	@Override
	public int indexToSerial(int index) throws NoSuchElementException {
		// TODO Auto-generated method stub
		return 0;
	}
}
