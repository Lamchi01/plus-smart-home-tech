package ru.yandex.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.handler.snapshot.SnapshotEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka_client.KafkaClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {
    @Value("${kafka.topics.snapshots-events}")
    private String snapshotTopic;
    private final KafkaClient kafkaClient;
    private final SnapshotEventHandler snapshotEventHandler;

    public void start() {
        Consumer<String, SensorsSnapshotAvro> consumer = kafkaClient.getSnapshotConsumer();
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            consumer.subscribe(List.of(snapshotTopic));
            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(1000);
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    handleRecord(record.value());
                }
            }
        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки снапшотов", e);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }

    private void handleRecord(SensorsSnapshotAvro snapshot) {
        log.info("Обработка снапшота: {}", snapshot);
        snapshotEventHandler.handle(snapshot);
        log.info("Снапшот обработан: {}", snapshot);
    }
}