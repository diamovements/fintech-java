package org.example.jmh2.loadBalancing;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class KafkaTest {

    private Producer<String, String>[] producers;
    private Consumer<String, String> consumer;
    private static final String TOPIC = "test-topic";

    @Setup(Level.Trial)
    public void setUp() {
        producers = new Producer[3];
        for (int i = 0; i < 3; i++) {
            Properties producerProps = new Properties();
            producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            producers[i] = new KafkaProducer<>(producerProps);
        }

        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList(TOPIC));
    }

    @Benchmark
    public void testProducerConsumer() {
        for (Producer<String, String> producer : producers) {
            producer.send(new ProducerRecord<>(TOPIC, "key", "LOAD BALANCING"), (metadata, exception) -> {
                if (exception == null) {
                    //System.out.println("Сообщение отправлено в топик:  " + metadata.topic() + " продюсером " + producer);
                } else {
                    //System.err.println("Ошибка при отправке сообщения: " + exception.getMessage());
                }
            });
        }

        consumer.poll(Duration.ofMillis(100)).forEach(record -> {
            //System.out.println("Полученное сообщение: " + record.value());
        });
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        for (Producer<String, String> producer : producers) {
            producer.close();
        }
        consumer.close();
    }
}
