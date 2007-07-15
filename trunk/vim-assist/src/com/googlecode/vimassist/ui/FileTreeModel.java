package com.googlecode.vimassist.ui;

import java.io.File;
import java.io.FileFilter;

import javax.swing.tree.DefaultTreeModel;

public class FileTreeModel extends DefaultTreeModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FileTreeModel(File file) {
		super(new FileTreeNode(null, file));
	}
	
	public FileTreeModel(File file, FileFilter excludedFilter) {
		super(new FileTreeNode(null, file, excludedFilter));
	}
	

}
