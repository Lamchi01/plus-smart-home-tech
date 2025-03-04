package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.sensors.SensorEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.service.EventService;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
public class CollectController {
    private final EventService eventService;

    private static final String SENSOR_TOPIC = "telemetry.sensors.v1";
    private static final String HUB_TOPIC = "telemetry.hubs.v1";

    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent sensorEvent) {
        log.info("Запрос на добавление события датчика {}", sensorEvent);
        eventService.collectSensorEvent(sensorEvent, SENSOR_TOPIC);
        log.info("Событие датчика {} добавлено успешно", sensorEvent);
    }

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent hubEvent) {
        log.info("Запрос на добавление события хаба {}", hubEvent);
        eventService.collectHubEvent(hubEvent, HUB_TOPIC);
        log.info("Событие хаба {} добавлено успешно", hubEvent);
    }
}