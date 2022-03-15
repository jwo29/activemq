package com.activemq_test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ExtractFileList {
	
	private static String dirPath = "C:\\prj_jiwoo\\java\\activemq-test\\src\\main\\resources\\static\\files";
	
	public static void main(String[] args) throws IOException {
		
		// Extract file list
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		
		OutputStream outStream = new FileOutputStream(dirPath + "\\_fileList.txt");
	
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			String fileName = file.getName() + "\n";
			outStream.write(fileName.getBytes());
			
			System.out.println("File name wrote success :: " + fileName);
		}
		
		System.out.println("File list extraction success :: " + dir.getName());

		outStream.close();
	}
}
