package org.iptime.iothome.rabbitMQ.workqueue;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class NewTask {
	
	private final static String QUEUE_NAME = "task_queue";
	private final static String host = "iothome.iptime.org";
	//private final static String host = "localhost";
	
	public static void main(String[] args) throws IOException, TimeoutException {
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);
		factory.setUsername("sensor");
		factory.setPassword("sensor");
		
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		
		channel.queueDeclare(QUEUE_NAME, true, false, false, null);
		
		String message = getMessage(args);
		channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
		System.out.println("[X] Sent '" + message + "'");
		
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
