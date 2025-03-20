package ru.yandex.practicum.kafka_client;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;

@Configuration
public class ClientKafkaConfiguration {

    @Bean(name = "kafkaHubConsumerConfiguration")
    @ConfigurationProperties(prefix = "kafka.hub.properties")
    public Properties kafkaHubConsumerConfiguration() {
        return new Properties();
    }

    @Bean()
    @ConfigurationProperties(value = "kafka.snapshot.properties")
    public Properties kafkaSnapshotConsumerConfiguration() {
        return new Properties();
    }

    @Bean
    KafkaClient kafkaClient(Properties kafkaHubConsumerConfiguration, Properties kafkaSnapshotConsumerConfiguration) {
        return new KafkaClient() {
            private Consumer<String, HubEventAvro> hubConsumer;
            private Consumer<String, SensorsSnapshotAvro> snapshotConsumer;

            @Override
            public Consumer<String, HubEventAvro> getHubConsumer() {
                if (hubConsumer == null) {
                    initConsumer(kafkaHubConsumerConfiguration);
                }
                return hubConsumer;
            }

            @Override
            public Consumer<String, SensorsSnapshotAvro> getSnapshotConsumer() {
                if (snapshotConsumer == null) {
                    initSnapshotConsumer(kafkaSnapshotConsumerConfiguration);
                }
                return snapshotConsumer;
            }

            private void initConsumer(Properties kafkaHubConsumerConfiguration) {
                hubConsumer = new KafkaConsumer<>(kafkaHubConsumerConfiguration);
            }

            private void initSnapshotConsumer(Properties kafkaSnapshotConsumerConfiguration) {
                snapshotConsumer = new KafkaConsumer<>(kafkaSnapshotConsumerConfiguration);
            }
        };
    }
}