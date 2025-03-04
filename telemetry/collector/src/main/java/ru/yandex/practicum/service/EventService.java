package ru.yandex.practicum.service;

import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.sensors.SensorEvent;

public interface EventService {
    void collectHubEvent(HubEvent hubEvent, String topic);

    void collectSensorEvent(SensorEvent sensorEvent, String topic);
}