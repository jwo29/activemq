package com.activemq_test;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Receiver {
	@JmsListener(destination = "spring_queue", containerFactory = "myFactory")
	public void receiveMsg(String message) {
		System.out.println("JMS Message Received successfully(Receiver):: " + message);
	}
}
