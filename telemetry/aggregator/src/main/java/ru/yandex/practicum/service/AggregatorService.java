package ru.yandex.practicum.service;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public interface AggregatorService {
    void sendSnapshot(Producer<String, SpecificRecordBase> producer, SensorEventAvro sensorEvent);
}