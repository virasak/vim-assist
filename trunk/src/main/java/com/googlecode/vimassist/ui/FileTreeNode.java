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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;



public class FileTreeNode implements MutableTreeNode {

	private FileTreeNode parentNode;

	private File file;

	private List<FileTreeNode> childrenNode = null;

	private static final FileFilter INCLUDED_ALL_FILE_FILTER = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			return true;
		}
	};

	private FileFilter includedFilter;

	private LabelDecorator labelDecorator;

	private Comparator<? super FileTreeNode> FILE_COMPARATOR =  new Comparator<FileTreeNode>() {
		@Override
		public int compare(FileTreeNode o1, FileTreeNode o2) {
			if ((o1.file.isDirectory() && o2.file.isDirectory()) ||
				(!o1.file.isDirectory() && !o2.file.isDirectory())) {

				return o1.file.compareTo(o2.file);

			} else if (o1.file.isDirectory()) {
				return -1;
			} else {
				return 1;
			}
		}

	};

	public FileTreeNode(FileTreeNode parentNode, File file) {
		this(parentNode, file, INCLUDED_ALL_FILE_FILTER);
	}

	public FileTreeNode(FileTreeNode parentNode, File file, FileFilter includedFilter) {
		this.parentNode = parentNode;
		this.file = file;
		this.includedFilter = includedFilter;
	}

	public boolean getAllowsChildren() {
		return file.isDirectory();
	}

	private void populateChildren() {
		if (file.isDirectory()) {
			childrenNode = new ArrayList<FileTreeNode>();

			for (File child : file.listFiles(includedFilter)) {
				FileTreeNode childNode = new FileTreeNode(this, child, includedFilter);
				if (child.isDirectory()) {
					childrenNode.add(childNode);
				} else {
					childrenNode.add(childNode);
				}
			}

			Collections.sort(childrenNode, FILE_COMPARATOR);
		}

	}

	public void refresh() {
		if (childrenNode == null) {
			populateChildren();
		} else {
			List<FileTreeNode> removeNodes = new LinkedList<FileTreeNode>();

			List<File> newFiles = new ArrayList<File>();
			Collections.addAll(newFiles, file.listFiles(includedFilter));

			for (FileTreeNode childNode : childrenNode) {
				if (!childNode.file.exists()) {
					removeNodes.add(childNode);
				} else {
					childNode.refresh();

					newFiles.remove(childNode.getFile());
				}
			}

			childrenNode.removeAll(removeNodes);
			for (File newFile : newFiles) {
				FileTreeNode node = new FileTreeNode(this, newFile, includedFilter);
				childrenNode.add(node);
			}

			Collections.sort(childrenNode, FILE_COMPARATOR );

		}
	}


	@SuppressWarnings("unchecked")
	public Enumeration children() {
		if (getAllowsChildren()) {
			if (childrenNode == null) {
				populateChildren();
			}
			return Collections.enumeration(childrenNode);
		} else {
			return null;
		}
	}

	public TreeNode getChildAt(int index) {
		if (getChildCount() > 0) {
			return childrenNode.get(index);
		} else {
			return null;
		}
	}

	public int getChildCount() {
		if (getAllowsChildren()) {
			if (childrenNode == null) {
				populateChildren();
			}

			return childrenNode.size();

		} else {
			return 0;
		}
	}

	public int getIndex(TreeNode node) {
		if(getAllowsChildren()) {
			if (childrenNode == null) {
				populateChildren();
			}

			return childrenNode.indexOf(node);

		} else {
			return -1;
		}
	}

	public TreeNode getParent() {
		return parentNode;
	}

	public boolean isLeaf() {
		return !file.isDirectory();
	}

	public File getFile() {
		return file;
	}


	public LabelDecorator getLabelDecorator() {
		return labelDecorator;
	}

	public void setLabelDecorator(LabelDecorator labelDecorator) {
		this.labelDecorator = labelDecorator;
	}

	@Override
	public String toString() {

		if (labelDecorator != null) {
			return labelDecorator.decorate(file);
		} else {
			return parentNode == null? "/" : file.getName();
		}
	}

	public interface LabelDecorator {
		String decorate(File file);
	}

	@Override
	public void insert(MutableTreeNode child, int index) {
		childrenNode.add((FileTreeNode)child);
	}

	@Override
	public void remove(int index) {
		System.out.println("remove(int)");
		childrenNode.remove(index);
	}

	@Override
	public void remove(MutableTreeNode node) {
		System.out.println("remove(MutableTreeNode)");
		childrenNode.remove(node);
	}

	@Override
	public void removeFromParent() {
		System.out.println("removeFromParent()");
		parentNode.remove(this);
		parentNode = null;
	}

	@Override
	public void setParent(MutableTreeNode newParent) {
		System.out.println("setParent(MutableTreeNode)");
		parentNode = (FileTreeNode)newParent;
		parentNode.insert(this, 0);
	}

	@Override
	public void setUserObject(Object object) {
		String fileName = (String)object;
		File newFile = new File(file.getParentFile(), fileName);
		if (!newFile.exists()) {
			file.renameTo(newFile);
		}
	}

}
