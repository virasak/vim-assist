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
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import com.googlecode.vimassist.vim.VimClient;

/**
 * Sample Application for test core classes.
 * @author virasak
 *
 */
public class VimAssist {
	//Optionally set the look and feel.
	private static boolean useSystemLookAndFeel = true;

	private static void createAndShowGUI(final Properties properties) {
		if (useSystemLookAndFeel) {
			try {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				System.err.println("Couldn't use system look and feel.");
			}
		}

		final String projectName = properties.getProperty("projectName", "Vim Assist");
		//Create and set up the window.
		JFrame frame = new JFrame(projectName);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.getContentPane().setLayout(new BorderLayout());
		
		//Create and set up the content pane.
		FileTreeModel fileTreeModel = new FileTreeModel(new File(properties.getProperty("location", ".")), new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().matches(properties.getProperty("excludedFile", ""));
			}
			
		});
		
		final VimClient client = new VimClient(projectName);
		
		FileTree fileTree = new FileTree(fileTreeModel);
		fileTree.setFileSelectProcessor(new FileSelectProcessor() {
			@Override
			public void process(File file) {
				if (!file.isDirectory()) {
					client.openFile(file);
				}	
			}
		});
		frame.getContentPane().add(new JScrollPane(fileTree), BorderLayout.CENTER);

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(final String[] args) {
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					Properties projectProperties = new Properties();
					if (args.length > 0) {
						File projectFile = new File(args[args.length - 1]);
						if (projectFile.exists() && projectFile.canRead() && projectFile.isFile()) {
							projectProperties.load(new FileInputStream(projectFile));
						}
					}
					createAndShowGUI(projectProperties);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

}
