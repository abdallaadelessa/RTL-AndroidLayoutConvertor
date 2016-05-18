package com.abdallaadelessa.rtl.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * 
 * @author Abdalla
 * 
 */
public class Helper {

	public static void logError(Exception e) {
		e.printStackTrace();
	}

	public static boolean isStringEmpty(String convertedLine) {
		return convertedLine == null || convertedLine.isEmpty();
	}

	public static void copyFile(File sourceFile, File destFile)
			throws IOException {
		if (!destFile.exists()) {
			destFile.createNewFile();
		}

		FileChannel source = null;
		FileChannel destination = null;

		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		} finally {
			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
		}
	}

	static File getDestFile(File srcFile, File destFolder)
			throws IOException {
		String dirPath = destFolder.getPath() + File.separator;
		if (!destFolder.exists()) {
			destFolder.mkdirs();
		}
		String destPath = dirPath + srcFile.getName();
		File destFile = new File(destPath);
		if (!destFile.exists()) {
			destFile.createNewFile();
		}
		return destFile;
	}

}
