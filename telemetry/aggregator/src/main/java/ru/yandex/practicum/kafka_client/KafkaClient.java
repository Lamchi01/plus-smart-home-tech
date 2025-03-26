package ru.yandex.practicum.kafka_client;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public interface KafkaClient {
    Producer<String, SpecificRecordBase> getProducer();

    Consumer<String, SensorEventAvro> getConsumer();
}