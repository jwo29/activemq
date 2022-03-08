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

public class ActiveMQMessageReceiver {
	// URL of the JMS server
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	// default broker URL is: tcp://localhost:61616
	
	// The Queue receiver message from
	private static String jmsQueue = "jiwoo_Queue";
	
	public static void main(String[] args) throws JMSException {
		// Getting JMS connection from the server adn starting it
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();
		
		// Creating a session to send/receive JMS message.
		Session session = connection.createSession(false, 
				Session.AUTO_ACKNOWLEDGE);
		
		// The queue will be created automatically on the server.
		Destination destination = session.createQueue(jmsQueue);
		
		// MessageConsumer is used for receiving (consuming) messages.
		MessageConsumer consumer = session.createConsumer(destination);
		
		// Here we receive the message.
		Message message = consumer.receive();
		
		// We will be using TextMessage in our example. MessageProducer sent us a TextMessage.
		if (message instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) message;
			System.out.println("JMS Message Received successfully:: '" + textMessage.getText() + "'");
		}
		
		connection.close();
	}
}
