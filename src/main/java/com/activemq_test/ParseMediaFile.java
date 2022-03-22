package com.work;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.json.simple.JSONObject;

public class ParseMediaFile {

	private static String ROOT_ABS_PATH = "C:\\prj_jiwoo\\java\\fileProcess\\fileProcess\\src\\main\\resources\\static\\files";
	private static String MEDIA_FILE_DIR_ABS_PATH = ROOT_ABS_PATH + File.separator + "_mediaFileList";
	private static String MEDIA_EXTR_DIR_ABS_PATH = ROOT_ABS_PATH + File.separator + "_mediaFileExtractions";
	
	// Directory path of still-cut image file
	private static String MULTI_EXTR_DIR_ABS_PATH = ROOT_ABS_PATH + File.separator + "_extractions";
	
//	private static String FFMPEG_EXE_ABS_PATH = "D:\\ffmpeg-4.4.1-essentials_build\\ffmpeg-4.4.1-essentials_build\\bin\\ffmpeg";
//	private static String FFPROBE_EXE_ABS_PATH = "D:\\ffmpeg-4.4.1-essentials_build\\ffmpeg-4.4.1-essentials_build\\bin\\ffprobe";
	
	public static void main(String[] args) throws IOException, Exception{
		
		makeDirectory(MEDIA_EXTR_DIR_ABS_PATH);
		makeDirectory(MULTI_EXTR_DIR_ABS_PATH);
		
		File mediaFileDirectory = new File(MEDIA_FILE_DIR_ABS_PATH);
		File[] fileList = mediaFileDirectory.listFiles();
		
		
		for(File file : fileList) {
	
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
			
			// Create still-cut of the video
			JSONObject stillcut = (JSONObject) makeStillCut(MULTI_EXTR_DIR_ABS_PATH, file);
			jsonObject.put("stillcut", stillcut);
			
			// Create JSON file of parsed data
			makeJSON(MEDIA_EXTR_DIR_ABS_PATH, file, jsonObject);
			
			
			stream.close();
		}
		
	}
	
	private static void makeJSON(String dirPath, File file, Object object) throws IOException {
		FileWriter newJsonFile = new FileWriter(MEDIA_EXTR_DIR_ABS_PATH + File.separator + file.getName() + ".json");
		newJsonFile.write(((JSONObject) object).toJSONString());
		newJsonFile.flush();
		newJsonFile.close();
		
		System.out.println("Created JSON File of the Image successfully :: " + file.getName());
	}
	
	private static Object makeStillCut(String dirPath, File file) {
		// transform media(frame image) using ffmpeg
//		FFmpeg ffmpeg = new FFmpeg(FFMPEG_EXE_ABS_PATH);
//		FFprobe ffprobe = new FFprobe(FFPROBE_EXE_ABS_PATH);
		
		FFmpegFrameGrabber frameGrabber = null;
		
		frameGrabber = new FFmpegFrameGrabber(file.getAbsoluteFile());
		try {
			frameGrabber.start();
		} catch (org.bytedeco.javacv.FFmpegFrameGrabber.Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Frame i;
		Java2DFrameConverter converter = null;
		JSONObject jsonObject = null;
		try {
			converter = new Java2DFrameConverter();
			
			i = frameGrabber.grab();
			BufferedImage bufferedImage = converter.convert(i);
			
			// Craete UUID
			final String uuid = makeUUID();
			
			String stillCutPath = dirPath + File.separator + uuid + ".png";
			
			jsonObject = new JSONObject();
			jsonObject.put("uuid", uuid);
			jsonObject.put("path", stillCutPath);
			/*
			 *  jsonObject.put( stillcut info );
			 */
			
			ImageIO.write(bufferedImage, "png", new File(stillCutPath));
			System.out.println("Created Still-Cut success :: " + file.getName());
			
			frameGrabber.stop();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (converter != null)
				converter.close();
		}
		
		return jsonObject;
		
		/*
		FFmpegBuilder builder = new FFmpegBuilder().setInput(file.getAbsolutePath()) // media file path
				.overrideOutputFiles(true)
				.addOutput(FFPROBE_EXE_ABS_PATH + File.separator + "STILL_CUT_" + file.getName())
				.done();
		
		FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
		executor.createJob(builder).run();
		*/
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
