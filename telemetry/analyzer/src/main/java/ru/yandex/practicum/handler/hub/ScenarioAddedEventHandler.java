package ru.yandex.practicum.handler.hub;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.mapper.Mapper;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioAddedEventHandler implements HubEventHandler {
    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;
    private final ActionRepository actionRepository;
    private final ConditionRepository conditionRepository;

    @Override
    public String getEventType() {
        return ScenarioAddedEventAvro.class.getName();
    }

    @Transactional
    @Override
    public void handle(HubEventAvro hubEvent) {
        ScenarioAddedEventAvro scenarioAddedEventAvro = (ScenarioAddedEventAvro) hubEvent.getPayload();
        validateSensors(scenarioAddedEventAvro.getConditions(), scenarioAddedEventAvro.getActions(), hubEvent.getHubId());

        Optional<Scenario> scenarioOptional = scenarioRepository.findByHubIdAndName(hubEvent.getHubId(), scenarioAddedEventAvro.getName());
        Scenario scenario;
        List<Long> oldConditionIds = null;
        List<Long> oldActionIds = null;

        if (scenarioOptional.isEmpty()) {
            scenario = Mapper.toScenario(scenarioAddedEventAvro, hubEvent);
        } else {
            scenario = scenarioOptional.get();
            oldConditionIds = scenario.getConditions().stream().map(Condition::getId).toList();
            oldActionIds = scenario.getActions().stream().map(Action::getId).toList();

            scenario.setConditions(scenarioAddedEventAvro.getConditions().stream()
                    .map(scenarioConditionAvro -> Mapper.toCondition(scenarioConditionAvro, scenario))
                    .toList());
            scenario.setActions(scenarioAddedEventAvro.getActions().stream()
                    .map(deviceActionAvro -> Mapper.toAction(deviceActionAvro, scenario))
                    .toList());
        }
        scenarioRepository.save(scenario);
        log.info("Сценарий сохранен: {}", scenario);
        cleanUnusedActions(oldActionIds);
        cleanUnusedConditions(oldConditionIds);
    }

    private void validateSensors(List<ScenarioConditionAvro> conditions, List<DeviceActionAvro> actions, String hubId) {
        List<String> conditionSensorIds = conditions.stream().map(ScenarioConditionAvro::getSensorId).toList();
        List<String> actionSensorIds = actions.stream().map(DeviceActionAvro::getSensorId).toList();

        if (!sensorRepository.existsByIdInAndHubId(conditionSensorIds, hubId)) {
            throw new IllegalArgumentException("Датчика для условий по сценарию не найден в хабе");
        }

        if (!sensorRepository.existsByIdInAndHubId(actionSensorIds, hubId)) {
            throw new IllegalArgumentException("Датчики для действий по сценарию не найдены в хабе");
        }
    }

    private void cleanUnusedActions(List<Long> oldActionIds) {
        if (oldActionIds != null && !oldActionIds.isEmpty()) {
            actionRepository.deleteAllById(oldActionIds);
        }
    }

    private void cleanUnusedConditions(List<Long> oldConditionIds) {
        if (oldConditionIds != null && !oldConditionIds.isEmpty()) {
            conditionRepository.deleteAllById(oldConditionIds);
        }
    }
}