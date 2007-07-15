package com.googlecode.vimassist.ui;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;


public class FileTree extends JPanel implements MouseListener {
	
	private static final long serialVersionUID = 1L;
	
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

			if (!file.isDirectory()) {
				final String cmd = "C:\\Program Files\\Vim\\vim71\\gvim.exe --servername TextMate --remote-tab-silent \""
						+ file.getAbsolutePath() + "\"";
				try {
					Runtime.getRuntime().exec(cmd);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

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
