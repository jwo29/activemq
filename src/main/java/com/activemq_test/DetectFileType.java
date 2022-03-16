package com.work;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

//public class DetectFileType implements JavaDelegate {
public class DetectFileType {	
	
	private static String dirPath = "C:\\prj_jiwoo\\java\\fileProcess\\fileProcess\\src\\main\\resources\\static\\files";
	
	//public void execute(DelegateExecution execution) throws IOException {
	public static void main(String[] args) throws IOException {
		
		BufferedReader bufferedReader = new BufferedReader(new FileReader(dirPath + "\\_fileList.txt"));
	
		String fileName;
		
			
		while ((fileName = bufferedReader.readLine()) != null) {
			
			// update process status for a file
			/*
			 * update DB 
			 */
			
			
			System.out.println(fileName);
			
			String filePath = dirPath + "\\" + fileName;
			
			Tika tika = new Tika();
			
			String type = tika.detect(filePath);
			System.out.println("Detected file type of " + fileName + " is " + type);
			
			//OutputStream outStream = null;
			if (type.startsWith("media")) { // media check
				
				System.out.println("Move " + fileName + " to _mediaFileList.txt");
				
				
				
				
				
				
			} else if(type.startsWith("image")) { // image check
				System.out.println("Move " + fileName + " to _imageFileList.txt");
			
			
			
			
			
			
			} else { // document check
				// outStream = new FileOutputStream(dirPath + "\\_documentFileList.txt", true); // boolean append -> true;
				// outStream.write((fileName + "\n").getBytes());
				
				// create file list of document files
				File src = new File(dirPath + "\\" + fileName);
				File dst = new File(dirPath + "\\_documentFileList\\" + fileName);
				
				FileUtils.copyFile(src, dst); // move
				
				System.out.println("Move " + fileName + " to _documentFileList");
				//outStream.close();
			} // need to add processing for other file type
			
			
		}
		
		
		bufferedReader.close();
	}
}
