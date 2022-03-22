package com.work;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

@Service("activeMQJsonMessageSender")
//public class ActiveMQJsonMessageSender {
public class ActiveMQJsonMessageSender implements JavaDelegate {
	
	// URL of the JMS server
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	
	// default broker URL is: tcp://localhost:61616
	private static String jmsQueue = "document_queue";

	private static String ROOT_ABS_PATH = "C:\\prj_jiwoo\\java\\fileProcess\\fileProcess\\src\\main\\resources\\static\\files";
//	private static String DOC_FILE_DIR_ABS_PATH = ROOT_ABS_PATH + File.separator + "_documentFileList";
	private static String DOC_EXTR_DIR_ABS_PATH = ROOT_ABS_PATH + File.separator + "_documentFileExtractions";
	
	//public static void main(String[] args) throws JMSException, IOException, ParseException, InterruptedException {
	public void execute(DelegateExecution execution) throws JMSException, IOException, ParseException, InterruptedException {
		
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
		File jsonFileDirectory = new File(DOC_EXTR_DIR_ABS_PATH);
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
