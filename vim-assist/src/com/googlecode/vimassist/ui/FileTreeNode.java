package com.googlecode.vimassist.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;



public class FileTreeNode implements TreeNode {

	private FileTreeNode parentNode;

	private File file;

	private List<FileTreeNode> childrenNode = null;

	public FileTreeNode(FileTreeNode parentNode, File file) {
		this.parentNode = parentNode;
		this.file = file;
	}

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
					FileTreeNode childNode = new FileTreeNode(this, child);
					childrenNode.add(childNode);
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
		return file.getName();
	}

}
