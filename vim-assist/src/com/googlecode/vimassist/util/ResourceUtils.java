package com.googlecode.vimassist.util;

import java.io.Reader;

import javax.swing.ImageIcon;

public class ResourceUtils {
	
	public static ImageIcon createImageIcon(String resourcePath) {
		return new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(resourcePath));
	}

	public static Reader loadInputStream(String string) {
		// TODO Auto-generated method stub
		return null;
	}

}
