package com.work;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
//import org.xml.sax.SAXException;
import org.imgscalr.Scalr;
import org.json.simple.JSONObject;

public class ParseImageFile {
	private static String ROOT_ABS_PATH = "C:\\prj_jiwoo\\java\\fileProcess\\fileProcess\\src\\main\\resources\\static\\files";
	private static String IMG_FILE_DIR_ABS_PATH = ROOT_ABS_PATH + File.separator + "_imageFileList";
	private static String IMG_EXTR_DIR_ABS_PATH = ROOT_ABS_PATH + File.separator + "_imageFileExtractions";

	// Directory path of thumbnail image file
	private static String MULTI_EXTR_DIR_ABS_PATH = ROOT_ABS_PATH + File.separator + "_extractions";
	
	private static int THUMBNAIL_HEIGHT_SIZE = 150;
	private static int THUMBNAIL_WIDTH_SIZE = 250;
	
	public static void main(String[] args) throws IOException, Exception, TikaException {
		
		makeDirectory(IMG_EXTR_DIR_ABS_PATH);
		makeDirectory(MULTI_EXTR_DIR_ABS_PATH);
		
		File imgFileDirectory = new File(IMG_FILE_DIR_ABS_PATH);
		File[] fileList = imgFileDirectory.listFiles();
		
		for(File file : fileList) {
			
			// Extract metadata
			Parser parser = new AutoDetectParser();
			BodyContentHandler handler = new BodyContentHandler();
			Metadata metadata = new Metadata();
			ParseContext pcontext = new ParseContext();
			
			InputStream stream = new FileInputStream(file.getAbsolutePath());
			parser.parse(stream, handler, metadata, pcontext);
			
			// Save parsed data as JSON format
			JSONObject jsonObject = new JSONObject();
			String[] metaNames = metadata.names();
			for(String name : metaNames) {
				jsonObject.put(name, metadata.get(name));
				System.out.println(name + " : " + metadata.get(name));
			}
			
			System.out.println("Pared Metadata of File successfully :: " + file.getName());
			
			// Create thumbnail of the image
			JSONObject thumbnail = (JSONObject) makeThumbnail(MULTI_EXTR_DIR_ABS_PATH, file);
			jsonObject.put("thumbnail", thumbnail);
			
			// Create JSON file of parsed data
			makeJSON(IMG_EXTR_DIR_ABS_PATH, file, jsonObject);
			
			stream.close();
		}
	}
	
	private static void makeJSON(String dirPath, File file, Object object) throws IOException {
		FileWriter newJsonFile = new FileWriter(dirPath + File.separator + file.getName() + ".json");
		newJsonFile.write(((JSONObject) object).toJSONString());
		newJsonFile.flush();
		newJsonFile.close();
		
		System.out.println("Created JSON File of the Image successfully :: " + file.getName());
	}
	
	private static Object makeThumbnail(String dirPath, File file) throws IOException {
		// Create thumbnail
		BufferedImage srcImg = ImageIO.read(file.getAbsoluteFile());
		int originalWidth = srcImg.getWidth();
		int originalHeight = srcImg.getHeight();
		
		
		int newWidth = originalWidth;
		int newHeight = (originalWidth * THUMBNAIL_HEIGHT_SIZE) / THUMBNAIL_WIDTH_SIZE;
		
		if (newHeight > originalHeight) {
			newWidth = (originalHeight * THUMBNAIL_WIDTH_SIZE) / THUMBNAIL_HEIGHT_SIZE;
			newHeight = originalHeight;
		}
			
					
		// Crop image
		BufferedImage cropImg = Scalr.crop(srcImg, (originalWidth-newWidth)/2, (originalHeight-newHeight)/2, newWidth, newHeight);
		
		// Destination image
		BufferedImage dstImg = Scalr.resize(cropImg, THUMBNAIL_WIDTH_SIZE, THUMBNAIL_HEIGHT_SIZE);

		
		// Create UUID
		final String uuid = makeUUID();
		
		// Save thumbnail to _extractions Directory
		String thumbnailPath = dirPath + File.separator + uuid + ".png";

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("uuid", uuid);
		jsonObject.put("path", thumbnailPath);
		/*
		 *  jsonObject.put( thumbnail info );
		 */
		
		File thumbnail = new File(thumbnailPath);
		ImageIO.write(dstImg, "png", thumbnail);
	
		System.out.println("Created Thumbnail of Image successfully :: " + file.getName());
	
		return jsonObject;
	}
	
	private static String makeUUID() {
		String uuid = UUID.randomUUID().toString();
		
		return uuid;
	}
	
	private static void makeDirectory(String dirPath) {
		File dir = new File(dirPath);
		if(!dir.exists()) {
			dir.mkdir();
		}
	}
}
