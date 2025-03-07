package ru.yandex.practicum.kafka_client;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class ClientKafkaConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "kafka.producer.properties")
    public Properties kafkaProducerConfiguration() {
        return new Properties();
    }

    @Bean
    KafkaClient kafkaClient(Properties kafkaProducerConfiguration) {
        return new KafkaClient() {
            private Producer<String, SpecificRecordBase> producer;

            @Override
            public Producer<String, SpecificRecordBase> getProducer() {
                if (producer == null) {
                    initProducer(kafkaProducerConfiguration);
                }
                return producer;
            }

            private void initProducer(Properties kafkaProducerConfiguration) {
                producer = new KafkaProducer<>(kafkaProducerConfiguration);
            }
        };
    }
}