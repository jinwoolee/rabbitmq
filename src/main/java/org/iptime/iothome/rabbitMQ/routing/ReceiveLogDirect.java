package org.iptime.iothome.rabbitMQ.routing;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

public class ReceiveLogDirect {
	public static final String EXCHANGE_NAME = "direct_logs";

	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("iothome.iptime.org");
		factory.setUsername("sensor");
		factory.setPassword("sensor");
		
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.exchangeDeclare(EXCHANGE_NAME, "direct");
		String queue_name = channel.queueDeclare().getQueue();

		if (args.length < 1) {
			System.err.println("Usage: ReceiveLogsDirect [info] [warning] [error]");
			System.exit(1);
		}

		for (String severity : args)
			channel.queueBind(queue_name, EXCHANGE_NAME, severity);

		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
		
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				String message = new String(body, "utf-8");
				System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
			}
		};
		
		channel.basicConsume(queue_name, true, consumer);
	}
}
