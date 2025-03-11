package ru.yandex.practicum.service;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka_client.KafkaClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final KafkaClient kafkaClient;
    @Value("${kafka.topics.sensors-events}")
    private String sensorTopic;
    @Value("${kafka.topics.hubs-events}")
    private String hubTopic;

    @Override
    public void collectHubEvent(HubEventAvro hubEvent) {
        kafkaClient.getProducer().send(new ProducerRecord<>(
                hubTopic,
                null,
                hubEvent.getTimestamp().toEpochMilli(),
                hubEvent.getHubId(),
                hubEvent
        ));
    }

    @Override
    public void collectSensorEvent(SensorEventAvro sensorEvent) {
        kafkaClient.getProducer().send(new ProducerRecord<>(
                sensorTopic,
                null,
                sensorEvent.getTimestamp().toEpochMilli(),
                sensorEvent.getHubId(),
                sensorEvent
        ));
    }

    @PreDestroy
    public void destroy() {
        try {
            kafkaClient.getProducer().flush();
            log.info("Выполнена команда flush() в Producer");
        } finally {
            kafkaClient.getProducer().close();
            log.info("Выполнена команда close() в Producer");
        }
    }
}