package com.activemq_test;

import java.util.ArrayList;
import java.util.Arrays;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.json.simple.JSONObject;

public class ActiveMQJsonMessageReceiver {
	// URL of the JMS server
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	// default broker URL is: tcp://localhost:61616
	
	// The queue receiver message from
	private static String jmsQueue = "document_queue";
	
	public static void main(String[] args) throws JMSException {
		// Getting JMS connection from the server and starting it
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		//connectionFactory.setTrustedPackages(new ArrayList(Arrays.asList("org.json.simple.JSONObject".split(","))));
		connectionFactory.setTrustAllPackages(true);
		Connection connection = connectionFactory.createConnection();
		connection.start();
		
		// Creating a session to send/receive JMS message
		Session session = connection.createSession(false,
				Session.CLIENT_ACKNOWLEDGE);
		
		// The queue will be created automatically on the server
		Destination destination = session.createQueue(jmsQueue);
		
		// MessageConsumer is used for receiving message
		MessageConsumer consumer = session.createConsumer(destination);
		
		Message message = null;
		while((message = consumer.receive()) instanceof ObjectMessage) {
			
			message.acknowledge();
			
			ObjectMessage objectMessage = (ObjectMessage) message;
			JSONObject jsonObject = (JSONObject) objectMessage.getObject();
			
			System.out.println("JSON Message Received successfully:: " + jsonObject.toString());
		}
	}
}
