package com.googlecode.vimassist.ui;


import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.googlecode.vimassist.util.ResourceUtils;

public class FileTree extends JTree implements MouseListener {

	private static final long serialVersionUID = 1L;

	private FileSelectProcessor DEFAULT_FILE_SELECT_PROCESSOR = new FileSelectProcessor() {
		@Override
		public void process(File file) {
			// do nothing
		}
	};
	
	private FileSelectProcessor fileSelectProcessor = DEFAULT_FILE_SELECT_PROCESSOR;

	
	public FileTree(FileTreeModel fileTreeModel) {
		super(fileTreeModel);
		
		DefaultTreeCellRenderer defaultTreeCellRenderer = new DefaultTreeCellRenderer();
		defaultTreeCellRenderer.setLeafIcon(ResourceUtils.createImageIcon("com/googlecode/vimassist/resource/icon/file.gif"));
		defaultTreeCellRenderer.setOpenIcon(ResourceUtils.createImageIcon("com/googlecode/vimassist/resource/icon/directory.gif"));
		defaultTreeCellRenderer.setClosedIcon(ResourceUtils.createImageIcon("com/googlecode/vimassist/resource/icon/directory.gif"));
		setCellRenderer(defaultTreeCellRenderer);
		
		addMouseListener(this);
	}


	public FileSelectProcessor getFileSelectProcessor() {
		return fileSelectProcessor;
	}


	public void setFileSelectProcessor(FileSelectProcessor fileSelectProcessor) {
		this.fileSelectProcessor = fileSelectProcessor;
	}
	
	public void mouseClicked(MouseEvent event) {
		if (event.getClickCount() == 2) {
			FileTreeNode node = (FileTreeNode)getLastSelectedPathComponent();

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
	
}
