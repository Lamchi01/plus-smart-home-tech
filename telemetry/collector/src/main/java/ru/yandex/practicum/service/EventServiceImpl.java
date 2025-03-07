package ru.yandex.practicum.service;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.kafka_client.KafkaClient;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.mapper.EventMapper;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.sensors.SensorEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final KafkaClient kafkaClient;

    @Override
    public void collectHubEvent(HubEvent hubEvent, String topic) {
        kafkaClient.getProducer().send(new ProducerRecord<>(
                topic,
                null,
                hubEvent.getTimestamp().toEpochMilli(),
                hubEvent.getHubId(),
                EventMapper.toHubEventAvro(hubEvent)
        ));
    }

    @Override
    public void collectSensorEvent(SensorEvent sensorEvent, String topic) {
        kafkaClient.getProducer().send(new ProducerRecord<>(
                topic,
                null,
                sensorEvent.getTimestamp().toEpochMilli(),
                sensorEvent.getHubId(),
                EventMapper.toSensorEventAvro(sensorEvent)
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