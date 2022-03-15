package com.activemq_test;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.tika.Tika;

public class DetectFileType {
	
	private static String dirPath = "C:\\prj_jiwoo\\java\\activemq-test\\src\\main\\resources\\static\\files";
	
	public static void main(String[] args) throws IOException {
		
		BufferedReader bufferedReader = new BufferedReader(new FileReader(dirPath + "\\_fileList.txt"));
	
		String fileName;
		
		
		while ((fileName = bufferedReader.readLine()) != null) {
			System.out.println(fileName);
			
			String filePath = dirPath + "\\" + fileName;
			
			Tika tika = new Tika();
			
			String type = tika.detect(filePath);
			System.out.println("Detected file type of " + fileName + " is " + type);
			
			OutputStream outStream = null;
			if (type.startsWith("media")) { // media check
				
				System.out.println("Appended " + fileName + " to _mediaFileList.txt");
			} else if(type.startsWith("image")) { // image check
				System.out.println("Appended " + fileName + " to _imageFileList.txt");
			} else { // document check
				outStream = new FileOutputStream(dirPath + "\\_documentFileList.txt", true); // boolean append -> true;
				outStream.write((fileName + "\n").getBytes());
				System.out.println("Appended " + fileName + " to _documentFileList.txt");
			} // need to add processing for other file type
			
			outStream.close();
		}
		
		
		bufferedReader.close();
	}
}
