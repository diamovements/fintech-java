package org.example.jmh2.multipleConsumers;


import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class RabbitTest {

    private Connection connection;
    private Channel producerChannel;
    private Channel[] consumerChannels;
    private static final String EXCHANGE = "test-exchange";
    private static final String QUEUE = "test-queue";
    private static final String ROUTING_KEY = "test-key";
    private static final int CONSUMER_COUNT = 3;

    @Setup(Level.Trial)
    public void setUp() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();

        producerChannel = connection.createChannel();
        producerChannel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.DIRECT, true);
        producerChannel.queueDeclare(QUEUE, true, false, false, null);
        producerChannel.queueBind(QUEUE, EXCHANGE, ROUTING_KEY);

        consumerChannels = new Channel[CONSUMER_COUNT];
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            Channel channel = connection.createChannel();
            channel.basicQos(1);

            channel.basicConsume(QUEUE, true, (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                // System.out.println("Полученное сообщение: " + message);
            }, consumerTag -> {});
            consumerChannels[i] = channel;
        }
    }

    @Benchmark
    public void testProducerConsumer() throws IOException {
        String message = "MULTIPLE CONSUMERS";
        producerChannel.basicPublish(EXCHANGE, ROUTING_KEY, null, message.getBytes(StandardCharsets.UTF_8));
    }

    @TearDown(Level.Trial)
    public void tearDown() throws IOException, TimeoutException {
        if (producerChannel != null && producerChannel.isOpen()) {
            producerChannel.close();
        }
        for (Channel consumerChannel : consumerChannels) {
            if (consumerChannel != null && consumerChannel.isOpen()) {
                consumerChannel.close();
            }
        }
        if (connection != null && connection.isOpen()) {
            connection.close();
        }
    }
}

