package com.activemq_test;

import javax.jms.ConnectionFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@SpringBootApplication
@EnableJms
public class App {
	@Bean
	public JmsListenerContainerFactory<?> myFactory(ConnectionFactory connectionFactory,
			DefaultJmsListenerContainerFactoryConfigurer configurer) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		
		// This provides all boot's default to this factory, including the message converter
		// factory.setPubSubDomain(true); // when setting pubsub
		configurer.configure(factory, connectionFactory);
		
		// You could still override some of Boot's default if necessary
		return factory;
	}
	
	
	@Bean // Serialize message content to json using TextMessage
	public MessageConverter jacksonJmsMessageConverter() {
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		return converter;
	}
	
	public static void main(String[] args) {
		// Launch the application
	    //ConfigurableApplicationContext context = SpringApplication.run(App.class, args);
	    ApplicationContext context = SpringApplication.run(App.class, args);

	    JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);

	    // Send a message with a POJO - the template reuse the message converter	    
	    String message = "hello activemq - spring";
	    
	    jmsTemplate.convertAndSend("spring_queue", message);
	    System.out.println("JMS Message Sent successfully(main):: " + message);
	}
}