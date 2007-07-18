package com.googlecode.vimassist.ui;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
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

public class FileExplorer extends JPanel implements MouseListener, KeyListener, ActionListener {

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
	
	private SelectedFileProcessor fileSelectProcessor = DEFAULT_FILE_SELECT_PROCESSOR;

	public FileExplorer(File file) {
		super(new BorderLayout());
		
		this.rootFile = file;
		
		initGUI();
	}
	
	private void initGUI() {
		fileTreeModel = new FileTreeModel(rootFile);
		fileTree = new FileTree(fileTreeModel);
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
		return fileSelectProcessor;
	}


	public void setFileSelectProcessor(SelectedFileProcessor fileSelectProcessor) {
		if (fileSelectProcessor == null) {
			this.fileSelectProcessor = DEFAULT_FILE_SELECT_PROCESSOR;
		} else {
			this.fileSelectProcessor = fileSelectProcessor;
		}
	}
	
	protected void processFile() {
		FileTreeNode node = (FileTreeNode)fileTree.getLastSelectedPathComponent();

		if (node != null) {
			fileSelectProcessor.process(node.getFile());
		}
		
	}

	public void setExcludedFileFilter(FileFilter fileFilter) {
		((FileTreeModel)fileTree.getModel()).setExcludedFileFilter(fileFilter);
		invalidate();
	}
	
	private void keyTypedForFileTree(KeyEvent event) {
		switch (event.getKeyChar()) {
		case KeyEvent.VK_ENTER:
			processFile();
			break;
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

		case ':':
			commandField.setEnabled(true);
			commandField.requestFocus();
			commandField.setText(":");
			break;
			
		case KeyEvent.VK_DELETE:
			File selectedFile = fileTree.getSelectedFile();
			String promptString =  "Are you really need to delete this file: " + selectedFile.getName();
			if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, promptString, "Delete Files", JOptionPane.YES_NO_OPTION)) {
				selectedFile.delete();
				fileTreeModel.refresh();
				validate();
			}
			break;
		
		case 'r':
			fileTreeModel.refresh();
			validate();
			break;
			
		case 'R': 
			{
				String fileName = JOptionPane.showInputDialog(this, "Rename to");
				if (!fileName.isEmpty()) {
					fileTree.getSelectedFile().renameTo(new File(fileName));
				}
				fileTreeModel.refresh();
				validate();
				break;
			}
		
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
	
	private void actionPerformedForCommandField(ActionEvent e) {
		commandField.setEnabled(false);
		commandField.setText(null);
		fileTree.requestFocus();
	}	

	
	/* Event ----------------------------------------------------- */
	public void mouseClicked(MouseEvent event) {
		if (event.getClickCount() == 2) {
			fileSelectProcessor.process(fileTree.getSelectedFile());
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


	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
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

}
