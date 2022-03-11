package com.activemq_test;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.jms.core.SessionCallback;

public class ActiveMQMessageReceiver {
	// URL of the JMS server
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	// default broker URL is: tcp://localhost:61616
	
	// The Queue receiver message from
	private static String jmsQueue = "jiwoo_Queue";
	
	public static void main(String[] agrs) throws JMSException, Exception {
		// Getting JMS connection from the server and starting it
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();
		
		// Creating a session to send/receive JMS message.
//		Session session = connection.createSession(false, 
//				Session.AUTO_ACKNOWLEDGE);
		
		Session session = connection.createSession(false, 
				Session.CLIENT_ACKNOWLEDGE);
		
		// The queue will be created automatically on the server.
		Destination destination = session.createQueue(jmsQueue);
		
		// MessageConsumer is used for receiving (consuming) messages.
		MessageConsumer consumer = session.createConsumer(destination);
		
		Message message = null;
		while ((message = consumer.receive()) instanceof TextMessage) { // polling. interval = 5s
			
			message.acknowledge(); // needed when acknowledgement mode is CLIENT_ACKNOWLEDGE;
			
			TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            System.out.println("Received: " + text);
			
			Thread.sleep(5000);
		}
		
		System.out.println("Message is not instance of TextMessage");
		System.out.println("Received: " + message + "-------end");
		
		
		// close
		consumer.close();
		session.close();
		connection.close();	
	}
}
