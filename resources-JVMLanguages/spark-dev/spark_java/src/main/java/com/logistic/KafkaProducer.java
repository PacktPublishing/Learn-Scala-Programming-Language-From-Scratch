package com.logistic;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;


import java.util.Properties;

/**
 * Simple producer
 * https://cwiki.apache.org/confluence/display/KAFKA/0.8.0+Producer+Example
 */
public class KafkaProducer {


    public static final String TEST_TOPIC = "test-topic";

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("metadata.broker.list", "localhost:9192");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("request.required.acks", "1");
        ProducerConfig config = new ProducerConfig(props);
        Producer<String, String> producer = new Producer<String, String>(config);

        for (int i=0; i<10; i++){
            KeyedMessage<String, String> data = new KeyedMessage<String, String>(TEST_TOPIC, "test-message" + i);
            producer.send(data);
            System.out.println("sent: " + data.message().toString());
        }
        producer.close();


    }
}
