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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileFilter;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class FileExplorer extends JPanel implements MouseListener, KeyListener {

	private static final long serialVersionUID = 1L;

	private File rootFile;

	private FileTree fileTree;

	private FileTreeModel fileTreeModel;

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

		/* file tree */
		fileTree = new FileTree(fileTreeModel);
		fileTree.setEditable(true);
		fileTree.setDragEnabled(true);
		add(new JScrollPane(fileTree), BorderLayout.CENTER);
		fileTree.addMouseListener(this);
		fileTree.addKeyListener(this);
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

		if (event.getKeyChar() == KeyEvent.VK_ENTER) {
			event.consume();
			processFile();
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
		}
	}

}
