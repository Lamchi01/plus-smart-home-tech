package ru.yandex.practicum.aggregator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka_client.KafkaClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Класс AggregationStarter, ответственный за запуск агрегации данных.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {
    private final KafkaClient kafkaClient;
    @Value("${kafka.topics.snapshots-events}")
    private String snapshotTopic;
    @Value("${kafka.topics.sensors-events}")
    private String sensorTopic;
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>(); // Key - HubId

    /**
     * Метод для начала процесса агрегации данных.
     * Подписывается на топики для получения событий от датчиков,
     * формирует снимок их состояния и записывает в кафку.
     */
    public void start() {
        Consumer<String, SensorEventAvro> consumer = kafkaClient.getConsumer();
        Producer<String, SpecificRecordBase> producer = kafkaClient.getProducer();
        try {
            // ... подготовка к обработке данных ...
            // ... например, подписка на топик ...
            consumer.subscribe(List.of(sensorTopic));
            log.info("Подписались на топик {}", sensorTopic);
            // Цикл обработки событий
            while (true) {
                // ... реализация цикла опроса ...
                // ... и обработка полученных данных ...
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(Duration.ofSeconds(10));

                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    SensorEventAvro sensorEvent = record.value();
                    Optional<SensorsSnapshotAvro> updatedSnapshot = updateState(sensorEvent);
                    if (updatedSnapshot.isPresent()) {
                        SensorsSnapshotAvro getSnapshot = updatedSnapshot.get();
                        producer.send(new ProducerRecord<>(
                                snapshotTopic,
                                null,
                                getSnapshot.getTimestamp().toEpochMilli(),
                                getSnapshot.getHubId(),
                                getSnapshot
                        ));
                    }
                }
            }
        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {

            try {
                // Перед тем, как закрыть продюсер и консьюмер, нужно убедится,
                // что все сообщения, лежащие в буффере, отправлены и
                // все оффсеты обработанных сообщений зафиксированы

                // здесь нужно вызвать метод продюсера для сброса данных в буффере
                // здесь нужно вызвать метод консьюмера для фиксиции смещений
                producer.flush();
                log.info("Выполнена команда flush() в Producer");
                consumer.commitSync();
                log.info("Фиксируем смещения в консьюмере");
            } finally {
                consumer.close();
                log.info("Закрываем консьюмер");
                producer.close();
                log.info("Закрываем продюсер");
            }
        }
    }

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        if (!snapshots.containsKey(event.getHubId())) {
            SensorsSnapshotAvro newSnapshot = new SensorsSnapshotAvro();
            newSnapshot.setSensorsState(new HashMap<>());
            snapshots.put(event.getHubId(), newSnapshot);
        }

        SensorsSnapshotAvro snapshot = snapshots.get(event.getHubId());
        if (snapshot.getSensorsState().containsKey(event.getId())) {
            SensorStateAvro oldState = snapshot.getSensorsState().get(event.getId());
            if (oldState.getTimestamp().isAfter(event.getTimestamp()) || oldState.getData().equals(event.getPayload())) {
                return Optional.empty();
            }
        }

        SensorStateAvro newState = new SensorStateAvro();
        newState.setTimestamp(event.getTimestamp());
        newState.setData(event.getPayload());

        snapshot.setHubId(event.getHubId());
        snapshot.setTimestamp(event.getTimestamp());
        snapshot.getSensorsState().put(event.getId(), newState);
        snapshots.put(event.getHubId(), snapshot);
        return Optional.of(snapshot);
    }
}