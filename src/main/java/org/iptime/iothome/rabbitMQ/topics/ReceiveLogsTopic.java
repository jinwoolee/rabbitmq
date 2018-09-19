package org.iptime.iothome.rabbitMQ.topics;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

public class ReceiveLogsTopic {

	public static final String EXCHANGE_NAME = "topic_logs";

	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("iothome.iptime.org");
		factory.setUsername("sensor");
		factory.setPassword("sensor");

		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

		String queue_name = channel.queueDeclare().getQueue();

		if (args.length < 1) {
			System.err.println("Usage: ReceiveLogsTopic [binding_key]...");
			System.exit(1);
		}

		for (String bindingKey : args)
			channel.queueBind(queue_name, EXCHANGE_NAME, bindingKey);

		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

		Consumer cousumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {

				String message = new String(body, "utf-8");
				System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
			}
		};

		channel.basicConsume(queue_name, true, cousumer);
	}

}
