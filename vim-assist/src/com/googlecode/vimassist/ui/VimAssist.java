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
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.UIManager;

import com.googlecode.vimassist.vim.VimClient;

/**
 * Sample Application for test core classes.
 * @author virasak
 *
 */
public class VimAssist  implements FileFilter, SelectedFileProcessor {
	//Optionally set the look and feel.
	private static boolean useSystemLookAndFeel = true;

	private VimClient client;

	private String projectName;
	
	private File projectDirectory;
	
	private Pattern excludedFilePattern;
	
	public VimAssist(Properties properties) {
		projectName = properties.getProperty("projectName", "Vim Assist");
		projectDirectory = new File(properties.getProperty("location", "."));
		excludedFilePattern = Pattern.compile(properties.getProperty("excludedFile", ""));

		client = new VimClient(projectName, projectDirectory);
	}
	
	@Override
	public boolean accept(File pathname) {
		return excludedFilePattern.matcher(pathname.getName()).matches();
	}

	@Override
	public void process(File file) {
		if (!file.isDirectory()) {
			client.openFile(file);
		}
	}
	public VimClient getClient() {
		return client;
	}
	public void setClient(VimClient client) {
		this.client = client;
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
					final VimAssist vimAssist = new VimAssist(projectProperties);
					javax.swing.SwingUtilities.invokeLater(new Runnable() {
						public void run() {
								createAndShowGUI(vimAssist);
						}
					});
					vimAssist.client.open();
					
				} catch (FileNotFoundException e) {
					System.out.println("File not found: " + projectFile.getName());
				} catch (IOException e) {
					System.out.println("Loading properties is failed");
				}
			}
		}
	}


}
