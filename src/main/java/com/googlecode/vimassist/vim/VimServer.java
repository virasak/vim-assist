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
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class VimServer implements Runnable {
	private static final String SERVER_NAME = "--servername";
	private static final String REMOTE_TAB_SILENT = "--remote-tab-silent";
	private static final String REMOTE_SEND = "--remote-send";
	private String vimPath;
	private String serverName;
	private File directory;
	private Queue<List<String>> commandQueue = new ConcurrentLinkedQueue<List<String>>();

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

		new Thread(this).start();
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

	public void sendCommand(String ...args) throws IOException {
		ArrayList<String> commandList = new ArrayList<String>();
		Collections.addAll(commandList, vimPath, SERVER_NAME, serverName);
		Collections.addAll(commandList, args);
		commandQueue.add(commandList);
	}

	public void sendRemoteCommand(String ...args) throws IOException {
		ArrayList<String> commandList = new ArrayList<String>();
		Collections.addAll(commandList, vimPath, SERVER_NAME, serverName, REMOTE_SEND);
		Collections.addAll(commandList, args);
		commandQueue.add(commandList);
	}

	@Override
	public void run() {
		while (true) {
			List<String> commandList = commandQueue.poll();
			if ( commandList != null) {

				// move vim window too fast may get system error
				// so get only the last test move command in sequence
				if (commandList.get(commandList.size() - 1).startsWith(":winpos")) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					List<String> headCommandList = commandQueue.peek();
					while (headCommandList != null && headCommandList.get(headCommandList.size() - 1).startsWith(":winpos")) {
						commandList = commandQueue.poll();
						headCommandList = commandQueue.peek();
					}
				}
				try {
					new ProcessBuilder(commandList).directory(directory).start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// do nothing
				}
			}
		}
	}


}
