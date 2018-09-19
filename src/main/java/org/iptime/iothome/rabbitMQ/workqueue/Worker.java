package org.iptime.iothome.rabbitMQ.workqueue;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class Worker {
	private final static String QUEUE_NAME = "task_queue";
	private final static String host = "iothome.iptime.org";
	
	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);
		factory.setUsername("sensor");
		factory.setPassword("sensor");
		
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		
		channel.queueDeclareNoWait(QUEUE_NAME, true, false, false, null);
		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
		channel.basicQos(1);
		
		Consumer consumer = new DefaultConsumer(channel) {
			public void handleDelivery(String consumerTag, Envelope envelope,
                    AMQP.BasicProperties properties, byte[] body)throws IOException{
				
				String message = new String(body, "utf-8");
				System.out.println(" [x] Received '" + message + "'");
				
				try {
					doWork(message);
				}finally {
					System.out.println(" [x] done");
					channel.basicAck(envelope.getDeliveryTag(), false);
				}
			}
		};
		
		channel.basicConsume(QUEUE_NAME, false, consumer);
	}
	
	private static void doWork(String message){
		for(char ch : message.toCharArray()) {
			if(ch == '.') {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
