package ui;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenu;
import javax.swing.JEditorPane;

import shared.LogicToUi;
import shared.Task;
import shared.Task.TaskType;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.JPanel;

public class GuiMain extends UI{

	private static final String TABLE_EMPTY_DATE_FIELD = "";
	private JFrame frmDoit;
	private JTextField textCmd;
	private JScrollPane scrollPane;
	private JTable table;
	private JPopupMenu popupCmdHint;
	private JPopupMenu popupStatus;
	private JMenu mnSomeMenu;
	private JEditorPane txtCmdHint;
	private JEditorPane txtStatus;
	private JPanel panel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		new GuiMain().runUI();
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
	@SuppressWarnings("serial")
	private void initialize() {
		frmDoit = new JFrame();
		frmDoit.setTitle("DoIt!");
		frmDoit.setBounds(100, 100, 500, 400);
		frmDoit.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frmDoit.setJMenuBar(menuBar);

		mnSomeMenu = new JMenu("Some Menu");
		mnSomeMenu.setMnemonic('s');
		menuBar.add(mnSomeMenu);
		
		panel = new JPanel();
		frmDoit.getContentPane().add(panel, BorderLayout.SOUTH);
				panel.setLayout(new BorderLayout(0, 0));
		
		
		
				textCmd = new JTextField();
				panel.add(textCmd);
				textCmd.addKeyListener(new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent arg0) {
						if (arg0.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
							executeCommand(textCmd.getText());
							popupCmdHint.setVisible(false);
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
		textCmd.setColumns(10);

		popupCmdHint = new JPopupMenu();
		addPopup(textCmd, popupCmdHint);

		txtCmdHint = new JEditorPane();
		txtCmdHint.setEditable(false);
		txtCmdHint.setContentType("text/html");
		popupCmdHint.add(txtCmdHint);

		txtStatus = new JEditorPane();
		txtStatus.setBackground(new Color(0, 0, 0, 0));
		txtStatus.setOpaque(false);
		panel.add(txtStatus, BorderLayout.SOUTH);
		txtStatus.setEditable(false);
		txtStatus.setContentType("text/html");

		scrollPane = new JScrollPane();
		frmDoit.getContentPane().add(scrollPane, BorderLayout.CENTER);

		table = new JTable();
		// Dummy table for WindowBuilder, the table will be filled up by code below
		table.setModel(new DefaultTableModel(
				new Object[][] {
						{new Integer(1), null, "-", "-", "-"},
				},
				new String[] {
						"Idx", "", "Start/Deadline", "End", "What to do?"
				}
				) {
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] {
				Integer.class, Boolean.class, Object.class, Object.class, String.class
			};
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		table.getColumnModel().getColumn(0).setPreferredWidth(25);
		table.getColumnModel().getColumn(1).setPreferredWidth(20);
		table.getColumnModel().getColumn(2).setPreferredWidth(110);
		table.getColumnModel().getColumn(3).setPreferredWidth(110);
		table.getColumnModel().getColumn(4).setPreferredWidth(160);

		popupStatus = new JPopupMenu();
		addPopup(scrollPane, popupStatus);
		scrollPane.setViewportView(table);
		
		String fileStatus = checkFilePermissions();		
		executeCommand("list");
		showStatus(fileStatus);
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

	class MyTableModel extends AbstractTableModel {
		public static final long serialVersionUID = 8328597110205703514L;
		public static final int COL_INDEX = 0;
		public static final int COL_DONE = 1;
		public static final int COL_START = 2;
		public static final int COL_END = 3;
		public static final int COL_TASKNAME = 4;

		private String[] columnNames;
		private List<Task> data;

		public MyTableModel(List<Task> taskList){
			columnNames = new String[]{"Idx", "", "Start/Deadline", "End", "Task"};
			data = taskList;
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			//return data.length;
			return data.size();
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			Task task = data.get(row);

			String start, end;
			if(task.getType().equals(TaskType.TIMED)) {
				start = dateTimeToLongerString(task.getStartTime());
			} else if(task.getType().equals(TaskType.DEADLINE)) {
				start = dateTimeToLongerString(task.getDeadline());
			} else {
				start = TABLE_EMPTY_DATE_FIELD;
			}

			if(task.getType().equals(TaskType.TIMED)) {
				end = dateTimeToLongerString(task.getEndTime());
			} else {
				end = TABLE_EMPTY_DATE_FIELD;
			}

			switch(col){
			case COL_INDEX:
	    			return row+1;
			case COL_DONE:
				return task.isDone();
			case COL_START:
				return start;
			case COL_END:
				return end;
			case COL_TASKNAME:
				return task.getTaskName();
			default:
				assert false;
			}
			return null;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		public boolean isCellEditable(int row, int col) {
			//Note that the data/cell address is constant,
			//no matter where the cell appears on-screen.
			if (col != COL_DONE) {
				return false;
			} else {
				return true;
			}
		}

		/*
		 * Don't need to implement this method unless your table's
		 * data can change.
		 */
		public void setValueAt(Object value, int row, int col) {
			// TODO: must handle cell updated to tell the logic task has been marked as done!

			Task task = data.get(row);

			switch (col) {
			case COL_INDEX:
				assert false;
				break;
			case COL_DONE:
				task.done((boolean) value);
			case COL_START:
				break;
			case COL_END:
				break;
			case COL_TASKNAME:
				break;
			default:
				assert false;
			}

			fireTableCellUpdated(row, col);
		}
	}

	public void showTasksList(List<Task> taskList){		

		table.setModel(new MyTableModel(taskList));
		table.getColumnModel().getColumn(0).setMinWidth(25);
		table.getColumnModel().getColumn(0).setMaxWidth(25);
		table.getColumnModel().getColumn(1).setMinWidth(20);
		table.getColumnModel().getColumn(1).setMaxWidth(20);
		table.getColumnModel().getColumn(2).setMinWidth(150);
		table.getColumnModel().getColumn(2).setMaxWidth(150);
		table.getColumnModel().getColumn(3).setMinWidth(150);
		table.getColumnModel().getColumn(3).setMaxWidth(150);
		//table.getColumnModel().getColumn(4).setPreferredWidth(160);
		
		table.getModel().addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				int row = e.getFirstRow();
				int column = e.getColumn();
				TableModel model = (TableModel) e.getSource();
				if (column == MyTableModel.COL_DONE) {
					boolean done = (boolean) model.getValueAt(row, column);
					int index = row + 1;
					if (done) {
						executeCommand("done " + index);
					} else {
						executeCommand("undone " + index);
					}
				}
			}

		});
	}

	public void executeCommand(String text) {

		// Call command parser
		LogicToUi returnValue = sendCommandToLogic(text);

		// Set command text box to empty
		textCmd.setText("");



		//int popupWidth = 300;
		//int popupHeight = 60;

		showStatus(returnValue.getString());

		//popupStatus.setPopupSize(popupWidth, popupHeight);
		//popupStatus.show(frmDoit, (frmDoit.getWidth() - popupWidth) / 2,
		//		frmDoit.getHeight() - popupHeight + 5);

		// Call command to refresh the table
			showTasksList(sendCommandToLogic("refresh").getList());


		if (returnValue.containsList()) {
			showTasksList(returnValue.getList());
		}

	}

	private void showStatus(String status) {
		txtStatus
				.setText("<html><table align=\"center\"><tr><td valign=\"middle\" align=\"center\"><font size=\"4\">"
						+ status
						+ " &nbsp;&nbsp;&nbsp;<a href=\"http://doit/undo\">undo</a></font></td></tr></table></html>");
	}

}
