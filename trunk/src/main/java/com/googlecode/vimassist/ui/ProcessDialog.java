package com.googlecode.vimassist.ui;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ProcessDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Process process;
	private JTextArea inputTextArea = new JTextArea();
	private JTextField outputTextField = new JTextField();
	private OutputStreamWriter outputStreamWriter;
	
	public ProcessDialog(Process process) {
		
		this.process = process;
		this.outputStreamWriter = new OutputStreamWriter(process.getOutputStream());
		
		initGUI();
		
		new Thread() {
			@Override
			public void run() {
				InputStreamReader in = new InputStreamReader(ProcessDialog.this.process.getInputStream());
				char[] charArray = new char[1024];
				while (true) {
					try {
						sleep(100);
					} catch (InterruptedException e1) {
						break;
					}
					try {
						if (in.ready()) {

							int result = in.read(charArray);
							if (result == -1) {
                                setTitle("Terminated");
								break;
							} 
							inputTextArea.append(String.copyValueOf(charArray, 0, result));
							inputTextArea.setCaretPosition(inputTextArea.getText().length());
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
		
		setSize(new Dimension(600, 200));
	}
	
	private void initGUI() {
		getContentPane().setLayout(new BorderLayout());
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		getContentPane().add(new JScrollPane(inputTextArea), BorderLayout.CENTER);
		inputTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		inputTextArea.setEditable(false);
		
		getContentPane().add(outputTextField, BorderLayout.SOUTH);
		outputTextField.requestFocusInWindow();
		outputTextField.addActionListener(this);
	}

	private void actionPerformedForOutputTextField(ActionEvent e) {
		try {
			String outputText = outputTextField.getText() + "\n";
			inputTextArea.append(outputText);
			outputStreamWriter.append(outputText);
			outputStreamWriter.flush();
			outputTextField.setText(null);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == outputTextField) {
			actionPerformedForOutputTextField(e);
		}
	}
	
}
