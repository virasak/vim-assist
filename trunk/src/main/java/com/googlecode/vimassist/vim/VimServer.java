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
import java.util.ArrayList;
import java.util.Collections;

public class VimServer {
	private static final String SERVER_NAME = "--servername";
	private static final String REMOTE_TAB_SILENT = "--remote-tab-silent";
	private static final String REMOTE_SEND = "--remote-send";
	private String vimPath;
	private String serverName;
	private File directory;

	public VimServer() {
		this(new File("."));
	}
	
	public VimServer(File directory) {
		this("SERVER", directory);
	}
	
	public VimServer(String serverName, File directory) {
		this("gvim", serverName, directory);
	}
	
	public VimServer(String vimPath, String serverName, File directory) {
		this.vimPath = vimPath;
		this.serverName = serverName + "@" + System.currentTimeMillis();
		this.directory = directory;
	}
	
	public void open() {
		try {
			sendCommand();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void openFile(File file) {
		if (!file.isDirectory()) {
			try {
				sendCommand(REMOTE_TAB_SILENT,file.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}
	
	public void quit() {
		try {
			sendCommand(REMOTE_SEND, ":q!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Process sendCommand(String ...args) throws IOException {
		ArrayList<String> commandList = new ArrayList<String>();
		Collections.addAll(commandList, vimPath, SERVER_NAME, serverName);
		Collections.addAll(commandList, args);
		return new ProcessBuilder(commandList).directory(directory).start();	
	}
	
	public Process sendRemoteCommand(String ...args) throws IOException {
		ArrayList<String> commandList = new ArrayList<String>();
		Collections.addAll(commandList, vimPath, SERVER_NAME, serverName, REMOTE_SEND);
		Collections.addAll(commandList, args);
		return new ProcessBuilder(commandList).directory(directory).start();	
	}
		
}
