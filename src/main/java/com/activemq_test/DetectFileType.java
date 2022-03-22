package com.work;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service("detectFileType")
//public class DetectFileType implements JavaDelegate {
public class DetectFileType {	
	
	private static String ROOT_ABS_PATH = "C:\\prj_jiwoo\\java\\fileProcess\\fileProcess\\src\\main\\resources\\static\\files";
	private static String MEDIA_FILE_DIR_ABS_PATH = ROOT_ABS_PATH + File.separator + "_mediaFileList";
	private static String IMG_FILE_DIR_ABS_PATH = ROOT_ABS_PATH + File.separator + "_imageFileList";
	private static String DOC_FILE_DIR_ABS_PATH = ROOT_ABS_PATH + File.separator + "_documentFileList";
	
	
//	public void execute(DelegateExecution execution) throws IOException {
	public static void main(String[] args) throws Exception {
		
		File targetDir = new File(ROOT_ABS_PATH);
		File[] fileList = targetDir.listFiles();
			
		for(File file : fileList) {
			
			// update process status for a file
			/*
			 * update DB 
			 */
			
			makeDirectory(MEDIA_FILE_DIR_ABS_PATH);
			makeDirectory(IMG_FILE_DIR_ABS_PATH);
			makeDirectory(DOC_FILE_DIR_ABS_PATH);
			
			
			System.out.println(file.getName());
			
			// Check is it a directory
			if (file.isDirectory()) {
				continue;
			}
			
			String fileName = file.getName();
			
			Tika tika = new Tika();
			
			String type = tika.detect(file.getAbsolutePath()); // type = TYPE/SUBTYPE
			System.out.println("Detected file type of " + fileName + " is " + type);
			
			File src = null;
			File dst = null;
			
			//OutputStream outStream = null;
			if (type.startsWith("video")) { // media check(what audio?)
				
				// create file list of document files
				src = new File(ROOT_ABS_PATH + File.separator + fileName);
				dst = new File(MEDIA_FILE_DIR_ABS_PATH + File.separator + fileName);
				
				FileUtils.copyFile(src, dst); // move
				
				System.out.println("Move " + fileName + " to _mediaFileList");
				
				
			} else if(type.startsWith("image")) { // image check
				
				// create file list of document files
				src = new File(ROOT_ABS_PATH + File.separator + fileName);
				dst = new File(IMG_FILE_DIR_ABS_PATH + File.separator + fileName);
				
				FileUtils.copyFile(src, dst); // move				
				
				System.out.println("Move " + fileName + " to _imageFileList");
			
			} else { // document check

				// create file list of document files
				src = new File(ROOT_ABS_PATH + File.separator + fileName);
				dst = new File(DOC_FILE_DIR_ABS_PATH + File.separator + fileName);
				
				FileUtils.copyFile(src, dst); // move
				
				System.out.println("Move " + fileName + " to _documentFileList");
				//outStream.close();
			} // need to add processing for other file type
			
		}
	
	}
	
	/*
	 * When there is no directory, create a directory
	 */
	private static void makeDirectory(String dirPath) {
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdir();
		}
	}
}
