package ru.yandex.practicum.service;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public interface EventService {
    void collectHubEvent(HubEventAvro hubEvent);

    void collectSensorEvent(SensorEventAvro sensorEvent);
}