package org.iptime.iothome.rabbitMQ.rpc;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class RPCServer {

	private static final String RPC_QUENE_NAME = "rpc_queue";

	public static void main(String[] args) {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("iothome.iptime.org");
		factory.setUsername("sensor");
		factory.setPassword("sensor");

		Connection connection = null;
		
		try {
			connection = factory.newConnection();
			Channel channel = connection.createChannel();

			channel.queueDeclare(RPC_QUENE_NAME, false, false, false, null);
			channel.queuePurge(RPC_QUENE_NAME);

			channel.basicQos(1);

			System.out.println(" [x] Awaiting RPC requests");

			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
						byte[] body) throws IOException {

					AMQP.BasicProperties replyProps = new BasicProperties.Builder()
							.correlationId(properties.getCorrelationId()).build();

					String response = "";
					String message = new String(body, "utf-8");
					int n = Integer.parseInt(message);

					System.out.println(" [.] fib(" + message + ")");
					response += fib(n);

					channel.basicPublish("", properties.getReplyTo(), replyProps, response.getBytes());
					channel.basicAck(envelope.getDeliveryTag(), false);

					synchronized (this) {
						this.notify();
					}
				}
			};

			channel.basicConsume(RPC_QUENE_NAME, false, consumer);

			while (true) {
				synchronized (consumer) {
					try {
						consumer.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException | TimeoutException e) {

		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (IOException _ignore) {
				}
		}

	}

	private static int fib(int n) {
		if (n == 0)
			return 0;
		if (n == 1)
			return 1;
		return fib(n - 1) + fib(n - 2);
	}
}
