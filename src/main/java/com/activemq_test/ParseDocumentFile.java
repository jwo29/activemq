package com.work;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
//import org.xml.sax.SAXException;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

@Service("parseDocumentFile")
public class ParseDocumentFile implements JavaDelegate {
//public class ParseDocumentFile {
	
	private static String ROOT_ABS_PATH = "C:\\prj_jiwoo\\java\\fileProcess\\fileProcess\\src\\main\\resources\\static\\files";
	private static String DOC_FILE_DIR_ABS_PATH = ROOT_ABS_PATH + File.separator + "_documentFileList";
	private static String DOC_EXTR_DIR_ABS_PATH = ROOT_ABS_PATH + File.separator + "_documentFileExtractions";

	//	public static void main(String[] args) throws Exception {
	public void execute(DelegateExecution execution) throws Exception {
		
		makeDirectory(DOC_EXTR_DIR_ABS_PATH);
		
		File documentFileDirectory = new File(DOC_FILE_DIR_ABS_PATH);
		File[] fileList = documentFileDirectory.listFiles();
		
		for(File file : fileList) {
			
			Parser parser = new AutoDetectParser();
			BodyContentHandler handler = new BodyContentHandler();
			Metadata metadata = new Metadata();
			ParseContext pcontext = new ParseContext();	

			// Parse file
			InputStream stream = new FileInputStream(file.getAbsolutePath());
			parser.parse(stream, handler, metadata, pcontext);
			
			// Save parsed data as JSON format
			JSONObject jsonObject = new JSONObject();
			String[] metaNames = metadata.names();
			for(String name : metaNames) {
				jsonObject.put(name, metadata.get(name));
				System.out.println(name + " : " + metadata.get(name));
			}
			jsonObject.put("Content", handler.toString());
			System.out.println(handler.toString());
		
			System.out.println();
			
			// Create JSON file of parsed data
			makeJSON(DOC_EXTR_DIR_ABS_PATH, file, jsonObject);
			
			stream.close();
		}
		
	}
	
	private static void makeJSON(String dirPath, File file, Object object) throws IOException {
		FileWriter newJsonFile = new FileWriter(dirPath + File.separator + file.getName() + ".json");
		newJsonFile.write(((JSONObject) object).toJSONString());
		newJsonFile.flush();
		newJsonFile.close();
	}
	
	private static void makeDirectory(String dirPath) {
		File dir = new File(dirPath);
		if(!dir.exists()) {
			dir.mkdir();
		}
	}
}
