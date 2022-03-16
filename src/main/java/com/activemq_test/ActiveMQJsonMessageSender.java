package com.work;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ActiveMQJsonMessageSender {
	// URL of the JMS server
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	
	// default broker URL is: tcp://localhost:61616
	private static String jmsQueue = "document_queue";

	private static String dirPath = "C:\\prj_jiwoo\\java\\fileProcess\\fileProcess\\src\\main\\resources\\static\\files";
	
	public static void main(String[] args) throws JMSException, IOException, ParseException, InterruptedException {
		
		// Getting JMS connection from the server and starting it
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();
		
		// Creating a session to send/receive JMS message
		Session session = connection.createSession(false, 
				Session.AUTO_ACKNOWLEDGE);
		
		// The queue will be created automatically on the server
		Destination destination = session.createQueue(jmsQueue);
		
		// MessageProducer is used for sending message to the queue.
		MessageProducer producer = session.createProducer(destination);

		// Send JSON message
		
		// Get file list
		String jsonFileDirectoryName = dirPath + "\\_documentFileExtractions";
		File jsonFileDirectory = new File(jsonFileDirectoryName);
		File[] fileList = jsonFileDirectory.listFiles();
		

		JSONParser parser = new JSONParser();
		for(File file : fileList) {
			// read json file
			FileReader reader = new FileReader(file.getAbsolutePath());
			Object obj = parser.parse(reader);
			JSONObject jsonObject = (JSONObject) obj;
			
			reader.close();
			
			System.out.println(jsonObject.toString());
		
			// exchange json object to text object
			TextMessage textMessage = session.createTextMessage();
			textMessage.setText(jsonObject.toJSONString());
			
			// send json object message
			producer.send(textMessage);
			
			System.out.println("Text Message Sent successfully:: " + textMessage.getText());
			Thread.sleep(1000);
		}
		
		producer.close();
		session.close();
		connection.close();
	}
}
