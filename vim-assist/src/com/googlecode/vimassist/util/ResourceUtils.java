package com.googlecode.vimassist.util;

import javax.swing.ImageIcon;

public class ResourceUtils {
	
	public static ImageIcon createImageIcon(String resourcePath) {
		return new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(resourcePath));
	}

}
