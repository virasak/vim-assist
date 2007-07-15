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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

/**
 * File Explorer
 * @author virasak
 *
 */
public class FileTree extends JPanel implements MouseListener {
	
	private static final long serialVersionUID = 1L;
	
	private FileSelectProcessor DEFAULT_FILE_SELECT_PROCESSOR = new FileSelectProcessor() {
		@Override
		public void process(File file) {
			// do nothing
		}
	};
	private FileSelectProcessor fileSelectProcessor = DEFAULT_FILE_SELECT_PROCESSOR;

	
	private JTree tree;

	public FileTree(File rootFile) {
		super(new BorderLayout());

		FileTreeNode top = new FileTreeNode(null, rootFile);

		tree = new JTree(top);
		tree.addMouseListener(this);

		JScrollPane treeView = new JScrollPane(tree);

		add(treeView, BorderLayout.CENTER);

	}

	public void mouseClicked(MouseEvent event) {
		if (event.getClickCount() == 2) {
			FileTreeNode node = (FileTreeNode) tree
					.getLastSelectedPathComponent();

			if (node == null)
				return;

			File file = node.getFile();

			fileSelectProcessor.process(file);
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

	public FileSelectProcessor getFileSelectProcessor() {
		return fileSelectProcessor;
	}

	public void setFileSelectProcessor(FileSelectProcessor fileSelectProcessor) {
		this.fileSelectProcessor = fileSelectProcessor == null? DEFAULT_FILE_SELECT_PROCESSOR : fileSelectProcessor;
	}
}
