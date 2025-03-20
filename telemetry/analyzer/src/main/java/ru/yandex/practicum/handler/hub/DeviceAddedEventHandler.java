package ru.yandex.practicum.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.repository.SensorRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceAddedEventHandler implements HubEventHandler {
    private final SensorRepository sensorRepository;

    @Override
    public String getEventType() {
        return DeviceAddedEventAvro.class.getName();
    }

    @Override
    public void handle(HubEventAvro hubEvent) {
        DeviceAddedEventAvro deviceAddedEventAvro = (DeviceAddedEventAvro) hubEvent.getPayload();
        Sensor sensor = new Sensor();
        sensor.setId(deviceAddedEventAvro.getId());
        sensor.setHubId(hubEvent.getHubId());

        sensorRepository.save(sensor);
        log.info("Добавлен датчик: {}", sensor);
    }
}