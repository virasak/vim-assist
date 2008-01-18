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
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.text.Position;

import com.googlecode.vimassist.vim.VimServer;

/**
 * Sample Application for test core classes.
 * @author virasak
 *
 */
public class VimAssist  implements FileFilter, SelectedFileProcessor, HierarchyBoundsListener {
	//Optionally set the look and feel.
	private static boolean useSystemLookAndFeel = true;

	private VimServer vimServer;

	private String projectName;
	
	private File projectDirectory;
	
	private Pattern excludedFilePattern;
	
	public VimAssist(Properties properties) {
		projectName = properties.getProperty("projectName", "Vim Assist");
		projectDirectory = new File(properties.getProperty("location", "."));
		excludedFilePattern = Pattern.compile(properties.getProperty("excludedFile", ""));

		vimServer = new VimServer(projectName, projectDirectory);
	}
	
	@Override
	public boolean accept(File pathname) {
		return excludedFilePattern.matcher(pathname.getName()).matches();
	}

	@Override
	public void process(File file) {
		if (!file.isDirectory()) {
			vimServer.openFile(file);
		}
	}
	public VimServer getVimServer() {
		return vimServer;
	}
	public void setVimServer(VimServer server) {
		this.vimServer = server;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public File getProjectDirectory() {
		return projectDirectory;
	}
	public void setProjectDirectory(File projectDirectory) {
		this.projectDirectory = projectDirectory;
	}

	private static void createAndShowGUI(VimAssist vimAssist) {
		if (useSystemLookAndFeel) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				System.err.println("Couldn't use system look and feel.");
			}
		}

		//Create and set up the window.
		JFrame frame = new JFrame(vimAssist.getProjectName());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.getContentPane().setLayout(new BorderLayout());
				
		FileExplorer fileExplorer = new FileExplorer(vimAssist.getProjectDirectory());
		fileExplorer.setExcludedFileFilter(vimAssist);

		fileExplorer.setFileSelectProcessor(vimAssist);
		
		frame.getContentPane().add(fileExplorer, BorderLayout.CENTER);
		
		frame.getRootPane().addHierarchyBoundsListener(vimAssist);
		
		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(final String[] args) {

		Properties projectProperties = new Properties();
		if (args.length > 0) {
			File projectFile = new File(args[args.length - 1]);
			if (projectFile.exists() && projectFile.canRead() && projectFile.isFile()) {
				try {
					projectProperties.load(new FileInputStream(projectFile));
					
				} catch (FileNotFoundException e) {
					System.out.println("File not found: " + projectFile.getName());
				} catch (IOException e) {
					System.out.println("Loading properties is failed");
				}
			}
		}			
		final VimAssist vimAssist = new VimAssist(projectProperties);
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI(vimAssist);
			}
		});
		vimAssist.vimServer.open();
	}


	@Override
	public void ancestorMoved(HierarchyEvent e) {
		try {
			int x = e.getChanged().getX() + e.getChanged().getWidth();
			vimServer.sendRemoteCommand(":winpos " + x + " " + e.getChanged().getY() + "<CR>");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void ancestorResized(HierarchyEvent e) {
		ancestorMoved(e);
		
	}


}
