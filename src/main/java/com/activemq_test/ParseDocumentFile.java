package com.activemq_test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
//import org.xml.sax.SAXException;
import org.json.simple.JSONObject;

public class ParseDocumentFile {
	
	private static String dirPath = "C:\\prj_jiwoo\\java\\activemq-test\\src\\main\\resources\\static\\files\\";
	
	public static void main(String[] args) throws Exception {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(dirPath + "\\_documentFileList.txt"));
		
		String fileName;
		
		while((fileName = bufferedReader.readLine()) != null) {
		
			Parser parser = new AutoDetectParser();
			BodyContentHandler handler = new BodyContentHandler();
			Metadata metadata = new Metadata();
			ParseContext pcontext = new ParseContext();	
			
			JSONObject jsonObject = new JSONObject();
			
			InputStream stream = new FileInputStream(dirPath + "\\" + fileName);
			parser.parse(stream, handler, metadata, pcontext);
			
			String[] metaNames = metadata.names();
			for(String name : metaNames) {
				jsonObject.put(name, metadata.get(name));
				System.out.println(name + " : " + metadata.get(name));
			}
			jsonObject.put("Content", handler.toString());
			System.out.println(handler.toString());
		
			System.out.println();
			
			// save as .json
			FileWriter newJsonFile = new FileWriter(dirPath + "\\_documentFileExtractions\\" + fileName + ".json");
			newJsonFile.write(jsonObject.toJSONString());
			newJsonFile.flush();
			newJsonFile.close();
			
			stream.close();
		}
		
		bufferedReader.close();
		
	}
}
