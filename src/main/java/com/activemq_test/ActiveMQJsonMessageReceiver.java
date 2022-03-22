package com.work;

import java.io.FileWriter;
import java.io.IOException;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ActiveMQJsonMessageReceiver {
	// URL of the JMS server
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	// default broker URL is: tcp://localhost:61616
	
	private final static String ACTIVE_MQ_USERNAME = "admin";
	private final static String ACTIVE_MQ_PASSWORD = "admin";
	
	// The queue receiver message from
	private static String jmsQueue = "document_queue";
	
	private static String dirPath = "C:\\prj_jiwoo\\java\\fileProcess\\fileProcess\\src\\main\\resources\\static\\files";
	
	public static void main(String[] args) throws JMSException, ParseException {
		// Getting JMS connection from the server and starting it
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		//connectionFactory.setTrustedPackages(new ArrayList(Arrays.asList("org.json.simple.JSONObject".split(","))));
		//connectionFactory.setTrustAllPackages(true); // to trust org.json.simple.JSONObject
		
		connectionFactory.setUserName(ACTIVE_MQ_USERNAME);
		connectionFactory.setPassword(ACTIVE_MQ_PASSWORD);
		Connection connection = connectionFactory.createConnection();
		connection.start();
		
		// Creating a session to send/receive JMS message
		Session session = connection.createSession(false,
				Session.AUTO_ACKNOWLEDGE);
		
		// The queue will be created automatically on the server
		Destination destination = session.createQueue(jmsQueue);
		
		// MessageConsumer is used for receiving message
		MessageConsumer consumer = session.createConsumer(destination);
		
		Message message = null;
		JSONParser parser = new JSONParser();
		while((message = consumer.receive()) instanceof TextMessage) {
			
			message.acknowledge();
			
			TextMessage textMessage = (TextMessage) message;
			
			System.out.println("Text Message Received successfully:: " + textMessage.getText());
			
			try {
				// exchange text message to json object
				String textString = textMessage.getText();
				Object obj = parser.parse(textString);
				JSONObject jsonObject = (JSONObject) obj;
			
				System.out.println("Exchaged String to JSON successfully:: " + jsonObject.toString());
				
				// System.out.println(jsonObject.get("eviId")); // get value
				// System.out.println(jsonObject.get("fileId"));
				
				// need parse exception
				String fileId = (String) jsonObject.get("fileId");
				System.out.println(fileId);
				
				// save as json 
				try {
					FileWriter newJsonFile = new FileWriter(dirPath + "\\receiver-side\\_documentFileExtractions\\" + fileId + ".json");
					newJsonFile.write(jsonObject.toJSONString());
					
					System.out.println("Saved JSON File successfully:: " + fileId + ".json");
					
					newJsonFile.flush();
					newJsonFile.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					
				}
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
			
		}
	}
}
