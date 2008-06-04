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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;

public class FileTreeModel extends DefaultTreeModel implements TreeModelListener {

	private static final long serialVersionUID = 1L;

	public FileTreeModel(File file) {
		super(new FileTreeNode(null, file));

		addTreeModelListener(this);
	}

	public FileTreeModel(File file, FileFilter excludedFilter) {
		super(new FileTreeNode(null, file, new InvertFileFilter(excludedFilter)));

		addTreeModelListener(this);
	}

	public void setExcludedFileFilter(FileFilter excludedFilter) {
		File rootFile = ((FileTreeNode)getRoot()).getFile();
		setRoot(new FileTreeNode(null, rootFile, new InvertFileFilter(excludedFilter)));
	}

	private static class InvertFileFilter implements FileFilter {

		FileFilter filter;

		public InvertFileFilter(FileFilter filter) {
			this.filter = filter;
		}

		@Override
		public boolean accept(File pathname) {
			return !filter.accept(pathname);
		}
	}

	public void refresh() {
		((FileTreeNode)getRoot()).refresh();
		reload();
	}

	public boolean createFile(File parentDirectory, String fileName) throws IOException {
		if (parentDirectory.isDirectory()) {
			File newFile = new File(fileName);
			return newFile.createNewFile();
		}

		return false;
	}

	// TreeModelListener ------------------------------------

	@Override
	public void treeNodesChanged(TreeModelEvent e) {
		System.out.println("nodes changed");
		refresh();
	}

	@Override
	public void treeNodesInserted(TreeModelEvent e) {
		System.out.println("nodes inserted");
	}

	@Override
	public void treeNodesRemoved(TreeModelEvent e) {
		System.out.println("nodes removed");
	}

	@Override
	public void treeStructureChanged(TreeModelEvent e) {
		System.out.println("tree structure changed");

	}

}
