package ru.yandex.practicum.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.repository.SensorRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceRemovedEventHandler implements HubEventHandler {
    private final SensorRepository sensorRepository;

    @Override
    public String getEventType() {
        return DeviceRemovedEventAvro.class.getName();
    }

    @Override
    public void handle(HubEventAvro hubEvent) {
        DeviceRemovedEventAvro deviceRemovedEventAvro = (DeviceRemovedEventAvro) hubEvent.getPayload();
        String deviceId = deviceRemovedEventAvro.getId();

        sensorRepository.deleteById(deviceId);
        log.info("Удален датчик: {}", deviceId);
    }
}