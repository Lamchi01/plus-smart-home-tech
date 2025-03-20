package ru.yandex.practicum.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.repository.ScenarioRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioRemovedEventHandler implements HubEventHandler {
    private final ScenarioRepository scenarioRepository;

    @Override
    public String getEventType() {
        return ScenarioRemovedEventAvro.class.getName();
    }

    @Override
    public void handle(HubEventAvro hubEvent) {
        ScenarioRemovedEventAvro scenarioRemovedEventAvro = (ScenarioRemovedEventAvro) hubEvent.getPayload();
        String scenarioName = scenarioRemovedEventAvro.getName();

        scenarioRepository.deleteByName(scenarioName);
        log.info("Удален сценарий с именем: {}", scenarioName);
    }
}