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
package com.googlecode.vimassist.vim;

import java.io.File;
import java.io.IOException;

public class VimClient {
	private static final String SERVER_NAME = "--servername";
	private static final String REMOTE_TAB_SILENT = "--remote-tab-silent";
	private String vimPath;
	private String serverName;
	private File directory;

	public VimClient() {
		this(new File("."));
	}
	
	public VimClient(File directory) {
		this("SERVER", directory);
	}
	
	public VimClient(String serverName, File directory) {
		this("gvim", serverName, directory);
	}
	
	public VimClient(String vimPath, String serverName, File directory) {
		this.vimPath = vimPath;
		this.serverName = serverName;
		this.directory = directory;
	}
	
	public void open() {
		try {
			ProcessBuilder builder = new ProcessBuilder(vimPath, SERVER_NAME, serverName);
			builder.directory(directory);
			builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void openFile(File file) {
		if (!file.isDirectory()) {
			try {
				ProcessBuilder builder = new ProcessBuilder(vimPath, SERVER_NAME, serverName, REMOTE_TAB_SILENT, file.getAbsolutePath());
				builder.directory(directory);
				builder.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}
	
	public void quit() {
		try {
			ProcessBuilder builder = new ProcessBuilder(vimPath, SERVER_NAME, "--remote-send", ":q!");
			builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
}
