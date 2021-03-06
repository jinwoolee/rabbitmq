package org.iptime.iothome.rabbitMQ.pubsub;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

public class ReceiveLogs {
	
	private static final String EXCHANGE_NAME = "logs";
	
	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("iothome.iptime.org");
		factory.setUsername("sensor");
		factory.setPassword("sensor");
		
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		
		channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
		String queueName = channel.queueDeclare().getQueue();
		
		channel.queueBind(queueName, EXCHANGE_NAME, "");
		
		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
		
		
		Consumer counsumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
			
				String message = new String(body, "utf-8");
				System.out.println(" [x] Received '" + message + "'");
			}
		};
		
		channel.basicConsume(queueName, true, counsumer);
		
	}
}
