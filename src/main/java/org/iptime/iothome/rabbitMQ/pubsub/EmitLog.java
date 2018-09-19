package org.iptime.iothome.rabbitMQ.pubsub;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class EmitLog {
	
	private static final String EXCHANGE_NAME = "logs";
	
	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("iothome.iptime.org");
		factory.setUsername("sensor");
		factory.setPassword("sensor");
		
		Connection connection = factory.newConnection();
		
		Channel channel = connection.createChannel();
		
		channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
		
		String message = getMessage(args);
		
		channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
		System.out.println(" [x] Sent '" + message + "'");
		
		channel.close();
		connection.close();
		
	}
	
	private static String getMessage(String[] strings) {
		if(strings.length < 1)
			return "Hello, World";
		
		return joinStrings(strings, " ");
	}

	private static String joinStrings(String[] strings, String delimiter) {
		if(strings == null || strings.length < 0)
			return "";
		
		StringBuffer stringBuffer = new StringBuffer(strings[0]);
		for(String str : strings) 
			stringBuffer.append(delimiter).append(str);
		
		return stringBuffer.toString();
	}
	
}
