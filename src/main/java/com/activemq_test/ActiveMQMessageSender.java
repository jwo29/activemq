package com.activemq_test;

import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveMQMessageSender {
	// URL of the JMS server.
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	
	// default broker URL is: tcp://localhost:61616
	private static String jmsQueue = "jiwoo_Queue";

	private static TextMessage message;
	
	public static void main(String[] agrs) throws JMSException, Exception {
	
		// Getting JMS connection from the server and  starting it
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		// Creating a session to send/receive JMS message.
		Session session = connection.createSession(false, 
				Session.CLIENT_ACKNOWLEDGE);
		
		// The queue will be created automatically on the server.
		Destination destination = session.createQueue(jmsQueue);
		
		// MessageProducer is used for sending message to the queue.
		MessageProducer producer = session.createProducer(destination);

		// Enter a message
		Scanner sc = new Scanner(System.in);
		while(sc.hasNextLine()) {
			String my_msg = sc.nextLine();
			
			message = session.createTextMessage(my_msg);
			producer.send(message);
			System.out.println("JMS Message Sent successfully:: " + message.getText());
			Thread.sleep(1000);
		}
		
		
		// close
		sc.close();
		producer.close();
		session.close();
		connection.close();
	}
}
