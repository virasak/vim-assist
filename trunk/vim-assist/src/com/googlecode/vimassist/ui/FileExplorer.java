/*
 * Copyright (c) 2007 virasak dungsrikaew

 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:

 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package com.googlecode.vimassist.ui;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

public class FileExplorer extends JPanel implements MouseListener, KeyListener, ActionListener, TreeModelListener {

	private static final long serialVersionUID = 1L;

	private File rootFile;
	
	private FileTree fileTree;
	
	private FileTreeModel fileTreeModel;
	
	private JTextField commandField;

	private SelectedFileProcessor DEFAULT_FILE_SELECT_PROCESSOR = new SelectedFileProcessor() {
		@Override
		public void process(File file) {
			// do nothing
		}
	};
	
	private SelectedFileProcessor selectedFileProcessor = DEFAULT_FILE_SELECT_PROCESSOR;

	public FileExplorer(File file) {
		super(new BorderLayout());
		
		this.rootFile = file;
		
		initGUI();
	}
	
	private void initGUI() {
		/* file model */ 
		fileTreeModel = new FileTreeModel(rootFile);
		fileTreeModel.addTreeModelListener(this);

		/* file tree */
		fileTree = new FileTree(fileTreeModel);
		fileTree.setEditable(true);
		fileTree.setDragEnabled(true);
		add(new JScrollPane(fileTree), BorderLayout.CENTER);
		fileTree.addMouseListener(this);
		fileTree.addKeyListener(this);
		
		commandField = new JTextField();
		commandField.setEnabled(false);
		commandField.addActionListener(this);
		commandField.addKeyListener(this);
		add(commandField, BorderLayout.SOUTH);
	}

	public SelectedFileProcessor getFileSelectProcessor() {
		return selectedFileProcessor;
	}


	public void setFileSelectProcessor(SelectedFileProcessor selectedFileProcessor) {
		if (selectedFileProcessor == null) {
			this.selectedFileProcessor = DEFAULT_FILE_SELECT_PROCESSOR;
		} else {
			this.selectedFileProcessor = selectedFileProcessor;
		}
	}
	
	protected void processFile() {
		FileTreeNode node = (FileTreeNode)fileTree.getLastSelectedPathComponent();

		if (node != null) {
			selectedFileProcessor.process(node.getFile());
		}
		
	}

	public void setExcludedFileFilter(FileFilter fileFilter) {
		((FileTreeModel)fileTree.getModel()).setExcludedFileFilter(fileFilter);
		invalidate();
	}
	
	private void keyTypedForFileTree(KeyEvent event) {
		switch (event.getKeyChar()) {
		
		/* emulate double click */
		case KeyEvent.VK_ENTER:
			processFile();
			break;
			
		/* emulate down arrow key */
		case 'j': 
			if (fileTree.isSelectionEmpty()) {
				fileTree.setSelectionRow(0);
			} else {
				int[] rows = fileTree.getSelectionRows();
				int lastRow = rows[rows.length - 1];
				if (lastRow < fileTree.getRowCount() - 1) {
					fileTree.setSelectionRow(lastRow + 1);
				}
			}
			break;
			
		/* emulate up arrow key */
		case 'k':
			if (fileTree.isSelectionEmpty()) {
				fileTree.setSelectionRow(fileTree.getRowCount() - 1);
			} else {
				int[] rows = fileTree.getSelectionRows();
				int firstRow = rows[0];
				if (firstRow > 0) {
					fileTree.setSelectionRow(firstRow - 1);
				}
				
			}
			break;
			
		/* emulate right arrow key */
		case 'l':
			if (fileTree.isSelectionEmpty()) {
				fileTree.setSelectionRow(0);
			} else {
				int[] rows = fileTree.getSelectionRows();
				if (rows.length == 1) {
					int row = rows[0];
					if (!fileTree.isExpanded(row)) {
						fileTree.expandRow(row);
					} else {
						if (row < fileTree.getRowCount() - 1) {
							fileTree.setSelectionRow(row + 1);
						}
					}
				}
			}
			break;
			
		/* emulate left arrow key */
		case 'h':
			if (fileTree.isSelectionEmpty()) {
				fileTree.setSelectionRow(fileTree.getRowCount() - 1);
			} else {
				int[] rows = fileTree.getSelectionRows();
				if (rows.length == 1) {
					int row = rows[0];
					if (fileTree.isExpanded(row)) {
						fileTree.collapseRow(row);
					} else {
						if (row > 0) {
							fileTree.setSelectionRow(row - 1);
						}
					}
				}
			}
			break;

		/* command mode */
		case ':':
			commandField.setEnabled(true);
			commandField.requestFocus();
			commandField.setText(":");
			break;
			
		/* delete file or directory */
		case KeyEvent.VK_DELETE:
			File selectedFile = fileTree.getSelectedFile();
			String promptString =  "Are you really need to delete this file: " + selectedFile.getName();
			if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, promptString, "Delete Files", JOptionPane.YES_NO_OPTION)) {
				selectedFile.delete();
				fileTreeModel.refresh();
				validate();
			}
			break;
					
		/* rename directory or file */
		case 'r': 
			{
				String fileName = JOptionPane.showInputDialog(this, "Rename to");
				if (!fileName.isEmpty()) {
					fileTree.getSelectedFile().renameTo(new File(fileName));
				}
				fileTreeModel.refresh();
				validate();
				break;
			}
		
		/* create new file */
		case 'n': 
			{
				File parentFile = fileTree.getSelectedFile();
				if (!parentFile.isDirectory()) {
					parentFile = parentFile.getParentFile();
				}
				
				String fileName = JOptionPane.showInputDialog(this, "File name");
				if (!fileName.isEmpty()) {
					File newFile = new File(parentFile, fileName);
					try {
						if (!newFile.createNewFile()) {
							JOptionPane.showMessageDialog(this, "Failed on create new file", "Failed", JOptionPane.ERROR_MESSAGE);
						}
					} catch (HeadlessException e) {
						JOptionPane.showMessageDialog(this, "Failed on create new file", "Exception", JOptionPane.ERROR_MESSAGE);
					} catch (IOException e) {
						JOptionPane.showMessageDialog(this, "Failed on create new file", "Exception", JOptionPane.ERROR_MESSAGE);
					}
				}
					
				fileTreeModel.refresh();
				validate();
				
				break;
			}

		}
	}

	private void keyTypedForCommandField(KeyEvent e) {
		switch (e.getKeyChar()) {
		case KeyEvent.VK_ESCAPE:
			commandField.setText(null);
			fileTree.requestFocus();
			break;
		}
	}
	
	private void keyPressedForFileTree(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_F5:
			fileTreeModel.refresh();
			validate();
			break;
		}
	}
	
	private void actionPerformedForCommandField(ActionEvent e) {

		if (commandField.getText().startsWith(":!")) {
			final String command = commandField.getText().substring(2);
			try {
				ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", command);
				Process process = processBuilder.redirectErrorStream(true).directory(rootFile).start();
				ProcessDialog processDialog = new ProcessDialog(process);
				processDialog.setModalityType(ModalityType.APPLICATION_MODAL);
				processDialog.setVisible(true);
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		commandField.setEnabled(false);
		commandField.setText(null);
		fileTree.requestFocus();
	}	

	
	/* Event ----------------------------------------------------- */
	
	/* MouseListener --------------------------------------------- */
	public void mouseClicked(MouseEvent event) {
		if (event.getClickCount() == 2) {
			selectedFileProcessor.process(fileTree.getSelectedFile());
		}
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/* KeyListener ------------------------------------------ */

	@Override
	public void keyPressed(KeyEvent event) {
		if (event.getSource() == fileTree) {
			keyPressedForFileTree(event);
		}
		
	}


	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void keyTyped(KeyEvent event) {
		if (event.getSource() == fileTree) {
			keyTypedForFileTree(event);
		} else if (event.getSource() == commandField) {
			keyTypedForCommandField(event);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == commandField) {
			actionPerformedForCommandField(e);
		}
	}

	// TreeModelListener ------------------------------------
	
	@Override
	public void treeNodesChanged(TreeModelEvent e) {
		int i = 1;
		System.out.println(i);
		
	}

	@Override
	public void treeNodesInserted(TreeModelEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void treeNodesRemoved(TreeModelEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void treeStructureChanged(TreeModelEvent e) {
		// TODO Auto-generated method stub
		
	}

}
