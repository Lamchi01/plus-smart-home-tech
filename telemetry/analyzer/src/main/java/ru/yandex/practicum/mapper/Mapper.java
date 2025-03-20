package ru.yandex.practicum.mapper;

import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.model.*;

import java.util.List;

public class Mapper {
    public static Condition toCondition(ScenarioConditionAvro conditionAvro, Scenario scenario) {
        Condition condition = new Condition();
        condition.setSensor(toSensor(conditionAvro.getSensorId(), scenario.getHubId()));
        condition.setType(ConditionType.valueOf(conditionAvro.getType().name()));
        condition.setOperation(ConditionOperation.valueOf(conditionAvro.getOperation().name()));
        condition.setValue(getConditionValue(conditionAvro.getValue()));
        condition.setScenarios(List.of(scenario));
        return condition;
    }

    public static Action toAction(DeviceActionAvro deviceActionAvro, Scenario scenario) {
        Action action = new Action();
        action.setSensor(toSensor(deviceActionAvro.getSensorId(), scenario.getHubId()));
        action.setType(ActionType.valueOf(deviceActionAvro.getType().name()));
        action.setValue(deviceActionAvro.getValue());
        return action;
    }

    private static Integer getConditionValue(Object conditionValue) {
        if (conditionValue == null) {
            return null;
        }
        if (conditionValue instanceof Boolean) {
            return ((Boolean) conditionValue ? 1 : 0);
        }
        if (conditionValue instanceof Integer) {
            return (Integer) conditionValue;
        }
        throw new ClassCastException("Ошибка преобразования значения условия");
    }

    private static Sensor toSensor(String sensorId, String hubId) {
        Sensor sensor = new Sensor();
        sensor.setId(sensorId);
        sensor.setHubId(hubId);
        return sensor;
    }

    public static Scenario toScenario(ScenarioAddedEventAvro scenarioAddedEventAvro, HubEventAvro hubEvent) {
        Scenario scenario = new Scenario();
        scenario.setHubId(hubEvent.getHubId());
        scenario.setName(scenarioAddedEventAvro.getName());
        scenario.setConditions(scenarioAddedEventAvro.getConditions().stream()
                .map(scenarioConditionAvro -> toCondition(scenarioConditionAvro, scenario)).toList());
        scenario.setActions(scenarioAddedEventAvro.getActions().stream()
                .map(deviceActionAvro -> toAction(deviceActionAvro, scenario)).toList());
        return scenario;
    }
}