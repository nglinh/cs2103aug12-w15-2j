package main.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.joda.time.DateTime;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import main.shared.LogicToUi;
import main.shared.Task;
import main.shared.Task.TaskType;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GuiMain extends GuiCommandBox{

	private static final String TABLE_EMPTY_DATE_FIELD = "";
	private JFrame frmDoit;
	private JTextField txtCmd;
	private JScrollPane scrollPane;
	private JTable table;
	private JPopupMenu popupCmdHint;
	private JMenu mnSomeMenu;
	private JEditorPane txtCmdHint;
	private JEditorPane txtStatus;
	private JPanel panel;
	
	
	private static GuiMain theOne = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		new GuiMain().runUI();
	}
	
	public static GuiMain getInstance() {
		if (theOne == null) {
			theOne = new GuiMain();
		}
		return theOne;
	}
	
	/**
	 * Create the application.
	 */
	private GuiMain() {
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
	protected void initialize() {
		
		frmDoit = new JFrame();
		frmDoit.setTitle("DoIt!");
		frmDoit.setBounds(100, 100, 700, 400);

		JMenuBar menuBar = new JMenuBar();
		frmDoit.setJMenuBar(menuBar);

		mnSomeMenu = new JMenu("Some Menu");
		mnSomeMenu.setMnemonic('s');
		menuBar.add(mnSomeMenu);
		
		panel = new JPanel();
		frmDoit.getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));

		txtCmd = new JTextField();
		panel.add(txtCmd);
		
		popupCmdHint = new JPopupMenu();
		addPopup(txtCmd, popupCmdHint);

		txtCmdHint = new JEditorPane();
		popupCmdHint.add(txtCmdHint);

		txtStatus = new JEditorPane();
		panel.add(txtStatus, BorderLayout.SOUTH);
		
		configureWidgets(txtCmd, txtStatus, txtCmdHint, popupCmdHint);

		scrollPane = new JScrollPane();
		frmDoit.getContentPane().add(scrollPane, BorderLayout.CENTER);

		table = new JTable();
		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode() == java.awt.event.KeyEvent.VK_DELETE){
					// Note: With every deletion, the index number changes!
					// Therefore we must delete from the largest number backwards.
					int[] rowsToDelete = table.getSelectedRows();
					Arrays.sort(rowsToDelete);
					System.out.println(Arrays.toString(rowsToDelete));
					for(int i = (rowsToDelete.length-1); i>=0; i--){
						executeCommand("delete " + (rowsToDelete[i] + 1));
					}
				}
			}
		});
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

		scrollPane.setViewportView(table);
		
		String fileStatus = checkFilePermissions();		
		executeCommand("list");
		showStatus(fileStatus);
	}

	@Override
	public void runUI() {
		/*EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiMain window = new GuiMain();
					window.frmDoit.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});*/
		frmDoit.setVisible(true);
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
				start = dateTimeToLongerString(task.getStartDate());
			} else if(task.getType().equals(TaskType.DEADLINE)) {
				start = dateTimeToLongerString(task.getDeadline());
			} else {
				start = TABLE_EMPTY_DATE_FIELD;
			}

			if(task.getType().equals(TaskType.TIMED)) {
				end = dateTimeToLongerString(task.getEndDate());
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
			if (col == COL_INDEX) {
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

			Task task = data.get(row);
			
			Parser parser = null;
			List<DateGroup> groups = null;
			DateTime date;
			
			try{

			switch (col) {
			case COL_INDEX:
				assert false;
				break;
			case COL_DONE:
				task.done((boolean) value);
				break;
			case COL_START:
				parser = new Parser();
				groups = parser.parse(((String) value).replace("-", " "));
				date = null;

				if (groups != null && !groups.isEmpty()) {
					date = new DateTime(groups.get(0).getDates().get(0));
				}

				if (date != null) {
					// Managed to parse out a date
					if (task.getType() == TaskType.TIMED) {
						task.changeStartAndEndDate(date, task.getEndDate());
					} else if (task.getType() == TaskType.DEADLINE) {
						task.changeDeadline(date);
					} else if (task.getType() == TaskType.FLOATING) {
						task.changetoDeadline(date);
					}

				} else {
					// Did not manage to parse out a date
					if (task.getType() == TaskType.TIMED) {
						task.changetoDeadline(task.getEndDate());
					} else if (task.getType() == TaskType.DEADLINE) {
						task.changetoFloating();
					}
				}
				break;
			case COL_END:
				parser = new Parser();
				groups = parser.parse(((String) value).replace("-", " "));
				date = null;

				if (groups != null && !groups.isEmpty()) {
					date = new DateTime(groups.get(0).getDates().get(0));
				}

				if (date != null) {
					// Managed to parse out a date
					if (task.getType() == TaskType.TIMED) {
						task.changeStartAndEndDate(task.getStartDate(), date);
					} else if (task.getType() == TaskType.DEADLINE) {
						task.changetoTimed(task.getDeadline(), date);
					} else if (task.getType() == TaskType.FLOATING) {
						task.changetoDeadline(date);
					}
				} else {
					// Did not manage to parse out a date
					if (task.getType() == TaskType.TIMED) {
						task.changetoDeadline(task.getStartDate());
					}
				}

				break;
			case COL_TASKNAME:
				task.changeName((String) value);
				break;
			default:
				assert false;
			}
			
			}catch(Exception e){
				e.printStackTrace();
			}

			fireTableCellUpdated(row, col);
		}
	}
	
	public int getContentWidth(String content) {
	    JLabel dummylabel = new JLabel();
	    //dummyEditorPane.setSize(1, Short.MAX_VALUE);
	    dummylabel.setText(content);
	    return dummylabel.getPreferredSize().width;
	}

	public void showTasksList(List<Task> taskList){
		
		int indexNumberColumnWidth = getContentWidth("9999") + 10;
		int checkboxColumnWidth = 20;
		int dateColumnWidth = getContentWidth("WMW 00 MWM 0000 23:59pm") + 10;

		table.setModel(new MyTableModel(taskList));
		table.getColumnModel().getColumn(0).setMinWidth(indexNumberColumnWidth);
		table.getColumnModel().getColumn(0).setMaxWidth(indexNumberColumnWidth);
		table.getColumnModel().getColumn(1).setMinWidth(checkboxColumnWidth);
		table.getColumnModel().getColumn(1).setMaxWidth(checkboxColumnWidth);
		// Give more space to date: Test with 27 May 2009 10am to 27 May 2009 10pm
		table.getColumnModel().getColumn(2).setMinWidth(dateColumnWidth);
		table.getColumnModel().getColumn(2).setMaxWidth(dateColumnWidth);
		table.getColumnModel().getColumn(3).setMinWidth(dateColumnWidth);
		table.getColumnModel().getColumn(3).setMaxWidth(dateColumnWidth);
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
				if (column == MyTableModel.COL_START) {
					String startTime = (String) model.getValueAt(row, column);
					startTime = startTime.trim();
					String endTime = (String) model.getValueAt(row, MyTableModel.COL_END);
					endTime = endTime.trim();
					int index = row + 1;
					
					try{
						if(startTime.isEmpty() && endTime.isEmpty()){
							// Floating task
							executeCommand("update " + index +" -tofloating");
						}else if(startTime.isEmpty() || endTime.isEmpty()){
							// Deadline task
							String deadline;
							if(startTime.isEmpty()){
								deadline = endTime;
							}else{
								deadline = startTime;
							}
							
							executeCommand("update " + index +" -deadline " + deadline.replace("-", " "));
						}else{
							// Timed task
							executeCommand("update " + index +" -begintime " + startTime.replace("-", " ") + " -endtime " + endTime.replace("-", " "));
						}
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
				if (column == MyTableModel.COL_END) {
					String endTime = (String) model.getValueAt(row, column);
					String startTime = (String) model.getValueAt(row, MyTableModel.COL_START);
					int index = row + 1;
					
					try{
						if(startTime.isEmpty() && endTime.isEmpty()){
							// Floating task
							executeCommand("update " + index +" -tofloating");
						}else if(startTime.isEmpty() || endTime.isEmpty()){
							// Deadline task
							String deadline;
							if(startTime.isEmpty()){
								deadline = endTime;
							}else{
								deadline = startTime;
							}
							
							executeCommand("update " + index +" -deadline " + deadline.replace("-", " "));
						} else {
							// Timed task
							executeCommand("update " + index + " -begintime " + startTime.replace("-", " ") + " -endtime " + endTime.replace("-", " "));
						}
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
				if (column == MyTableModel.COL_TASKNAME) {
					String newTaskName = (String) model.getValueAt(row, column);
					int index = row + 1;
					executeCommand("update " + index +" -name " + newTaskName);
				}
			}

		});
	}

	public void update(LogicToUi returnValue){
		// Call command to refresh the table
		showTasksList(sendCommandToLogic("refresh").getList());

		if (returnValue.containsList()) {
			showTasksList(returnValue.getList());
		}
		
		showStatus(returnValue.getString());
		
		// Update other windows
		//GuiMain2.getInstance().updateWindow(this);
		//GuiQuickAdd.getInstance().updateWindow(this);
	}

	public void updateWindow(Object source) {
		if(source != this){
			showTasksList(sendCommandToLogic("refresh").getList());
		}
		//executeCommand("refresh");
		
	}

}
