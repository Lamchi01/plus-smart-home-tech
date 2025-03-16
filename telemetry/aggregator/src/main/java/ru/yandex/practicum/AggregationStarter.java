package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka_client.KafkaClient;
import ru.yandex.practicum.service.AggregatorServiceImpl;

import java.time.Duration;
import java.util.List;

/**
 * Класс ru.yandex.practicum.AggregationStarter, ответственный за запуск агрегации данных.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {
    private final KafkaClient kafkaClient;
    private final AggregatorServiceImpl aggregatorService;
    @Value("${kafka.topics.sensors-events}")
    private String sensorTopic;

    /**
     * Метод для начала процесса агрегации данных.
     * Подписывается на топики для получения событий от датчиков,
     * формирует снимок их состояния и записывает в кафку.
     */
    public void start() {
        Consumer<String, SensorEventAvro> consumer = kafkaClient.getConsumer();
        Producer<String, SpecificRecordBase> producer = kafkaClient.getProducer();
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            consumer.subscribe(List.of(sensorTopic));

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(Duration.ofSeconds(10));

                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    SensorEventAvro sensorEvent = record.value();
                    aggregatorService.sendSnapshot(producer, sensorEvent);
                }
            }
        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {

            try {
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
}