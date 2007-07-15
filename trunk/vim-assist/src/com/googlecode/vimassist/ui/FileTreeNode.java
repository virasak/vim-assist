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
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;



public class FileTreeNode implements TreeNode {

	private FileTreeNode parentNode;

	private File file;

	private List<FileTreeNode> childrenNode = null;
	
	private static final FileFilter INCLUDED_ALL_FILE_FILTER = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			return false;
		}
	};
	
	private FileFilter excludedFilter;

	public FileTreeNode(FileTreeNode parentNode, File file) {
		this(parentNode, file, INCLUDED_ALL_FILE_FILTER);
	}
	
	public FileTreeNode(FileTreeNode parentNode, File file, FileFilter excludedFilter) {
		this.parentNode = parentNode;
		this.file = file;
		this.excludedFilter = excludedFilter;
	}

	@SuppressWarnings("unchecked")
	public Enumeration children() {
		if (getAllowsChildren()) {
			return Collections.enumeration(childrenNode);
		} else {
			return null;
		}
	}

	public boolean getAllowsChildren() {
		if(!file.isDirectory()) {
			return false;
		} else {
			if (childrenNode == null) {
				childrenNode = new ArrayList<FileTreeNode>();
				for (File child : file.listFiles()) {
					if (!excludedFilter.accept(child)) {
						FileTreeNode childNode = new FileTreeNode(this, child, excludedFilter);
						childrenNode.add(childNode);
					}
				}
			}
			return true;
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
			return childrenNode.size();
		} else {
			return 0;
		}
	}

	public int getIndex(TreeNode node) {
		if(getAllowsChildren()) {
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

	@Override
	public String toString() {
		if (parentNode == null) {
			return "/";
		} else {
			return file.getName();
		}
	}

}
