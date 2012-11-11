package main.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import java.awt.Toolkit;

public class GuiError extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2330108613038206384L;
	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		ShowErrorMessage("Hello", "Hello");
	}
	
	public static void ShowErrorMessage(String preMessage, String message){
		try {
			GuiError dialog = new GuiError(preMessage, message);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public GuiError(String preMessage, String message) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(GuiError.class.getResource("/resource/icon.png")));
		setType(Type.POPUP);
		setTitle("DoIt! Error");
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setBounds(100, 100, 500, 400);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane);
			{
				JTextArea txtMessage = new JTextArea();
				txtMessage.setEditable(false);
				txtMessage.setWrapStyleWord(true);
				txtMessage.setLineWrap(true);
				scrollPane.setViewportView(txtMessage);
				txtMessage.setText(message);
				txtMessage.setCaretPosition(0);
			}
		}
		{
			JTextArea txtPreMessage = new JTextArea();
			txtPreMessage.setBorder(new EmptyBorder(0, 0, 10, 0));
			txtPreMessage.setEditable(false);
			txtPreMessage.setWrapStyleWord(true);
			txtPreMessage.setLineWrap(true);
			txtPreMessage.setText(preMessage);
			txtPreMessage.setBackground(new Color(0, 0, 0, 0));
			txtPreMessage.setOpaque(false);
			contentPanel.add(txtPreMessage, BorderLayout.NORTH);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						((JDialog) contentPanel.getRootPane().getParent()).dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}

}
