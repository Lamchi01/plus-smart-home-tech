package ru.yandex.practicum.kafka_client;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.util.Properties;

@Configuration
public class ClientKafkaConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "kafka.producer.properties")
    public Properties kafkaProducerConfiguration() {
        return new Properties();
    }

    @Bean
    @ConfigurationProperties(prefix = "kafka.consumer.properties")
    public Properties kafkaConsumerConfiguration() {
        return new Properties();
    }

    @Bean
    KafkaClient kafkaClient(Properties kafkaProducerConfiguration, Properties kafkaConsumerConfiguration) {
        return new KafkaClient() {
            private Producer<String, SpecificRecordBase> producer;
            private Consumer<String, SensorEventAvro> consumer;

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

            @Override
            public Consumer<String, SensorEventAvro> getConsumer() {
                if (consumer == null) {
                    initConsumer(kafkaConsumerConfiguration);
                }
                return consumer;
            }

            private void initConsumer(Properties kafkaConsumerConfiguration) {
                consumer = new KafkaConsumer<>(kafkaConsumerConfiguration);
            }
        };
    }
}