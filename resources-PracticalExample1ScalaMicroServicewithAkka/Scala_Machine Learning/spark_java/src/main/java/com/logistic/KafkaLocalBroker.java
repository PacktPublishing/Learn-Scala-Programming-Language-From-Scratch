package com.logistic;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import kafka.server.KafkaServerStartable;

import java.util.Date;
import java.util.Properties;

/**
 * Simple test Broker
 */
public class KafkaLocalBroker {
    //location of kafka logging file:
    public static final String DEFAULT_LOG_DIR = "/tmp/embedded/kafka/";
    public static final String LOCALHOST_BROKER = "0:localhost:9092";


    public static void main(String[] args) {

        Properties kafkaProperties = new Properties();
        String logFile = System.getProperty("user.dir") + "/build/tmp/kafka-logs-" + new Date().toString().replace(" ", "_");

        kafkaProperties = new Properties();
        kafkaProperties.put("zookeeper.connect", "localhost:2181");
        kafkaProperties.put("broker.id", "0");
        kafkaProperties.put("hostname", "localhost");
        kafkaProperties.put("port", "9192");
        kafkaProperties.put("log.dirs", logFile);
        kafkaProperties.put("log.flush.interval.messages", String.valueOf(1));

        KafkaConfig kafkaConfig = new KafkaConfig(kafkaProperties);

        KafkaServerStartable kafka = new KafkaServerStartable(kafkaConfig);

        kafka.startup();


    }


}
