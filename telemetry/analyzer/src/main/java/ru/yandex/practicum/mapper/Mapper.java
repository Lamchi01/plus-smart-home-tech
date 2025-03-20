package ru.yandex.practicum.mapper;

import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.model.*;

import java.util.List;

public class Mapper {
    public static Condition toCondition(ScenarioConditionAvro conditionAvro, Scenario scenario) {
        Condition condition = new Condition();
        condition.setSensor(toSensor(conditionAvro.getSensorId(), scenario.getHubId()));
        condition.setType(ConditionType.valueOf(conditionAvro.getType().name()));
        condition.setOperation(ConditionOperation.valueOf(conditionAvro.getOperation().name()));
        condition.setValue(getSensorValue(conditionAvro.getValue()));
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

    private static Integer getSensorValue(Object value) {
        if (value == null) {
            return null;
        }
        return switch (value) {
            case Integer i -> i;
            case Boolean b -> b ? 1 : 0;
            default -> throw new IllegalArgumentException("Неверное значение датчика");
        };
    }

    private static Sensor toSensor(String sensorId, String hubId) {
        Sensor sensor = new Sensor();
        sensor.setId(sensorId);
        sensor.setHubId(hubId);
        return sensor;
    }
}