package ru.yandex.practicum.mapper;

import ru.yandex.practicum.model.hub.*;
import ru.yandex.practicum.model.sensors.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;

public class EventMapper {
    public static SensorEventAvro toSensorEventAvro(SensorEvent sensorEvent) {

        Object payload;

        switch (sensorEvent) {
            case MotionSensor event -> payload = MotionSensorAvro.newBuilder()
                    .setLinkQuality(event.getLinkQuality())
                    .setMotion(event.isMotion())
                    .setVoltage(event.getVoltage())
                    .build();

            case LightSensor event -> payload = LightSensorAvro.newBuilder()
                    .setLinkQuality(event.getLinkQuality())
                    .setLuminosity(event.getLuminosity())
                    .build();

            case TemperatureSensor event -> payload = TemperatureSensorAvro.newBuilder()
                    .setTemperatureC(event.getTemperatureC())
                    .setTemperatureF(event.getTemperatureF())
                    .build();

            case ClimateSensor event -> payload = ClimateSensorAvro.newBuilder()
                    .setTemperatureC(event.getTemperatureC())
                    .setHumidity(event.getHumidity())
                    .setCo2Level(event.getCo2Level())
                    .build();

            case SwitchSensor event -> payload = SwitchSensorAvro.newBuilder()
                    .setState(event.isState())
                    .build();

            default -> throw new IllegalStateException("Unexpected value: " + sensorEvent);
        }
        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(sensorEvent.getTimestamp())
                .setPayload(payload)
                .build();
    }

    public static HubEventAvro toHubEventAvro(HubEvent hubEvent) {

        Object payload;

        switch (hubEvent) {
            case DeviceAddedEvent event ->
                    payload = DeviceAddedEventAvro.newBuilder()
                    .setId(event.getId())
                    .setDeviceType(DeviceTypeAvro.valueOf(event.getDeviceType().name()))
                    .build();

            case DeviceRemovedEvent event ->
                    payload = DeviceRemovedEventAvro.newBuilder()
                    .setId(event.getId())
                    .build();

            case ScenarioAddedEvent event ->
                    payload = ScenarioAddedEventAvro.newBuilder()
                    .setActions(toDeviceActionAvro(event.getActions()))
                    .setConditions(toScenarioConditionAvro(event.getConditions()))
                    .build();

            case ScenarioRemovedEvent event ->
                    payload = ScenarioRemovedEventAvro.newBuilder()
                    .setName(event.getName())
                    .build();

            default -> throw new IllegalStateException("Unexpected value: " + hubEvent);
        }
        return HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(hubEvent.getTimestamp())
                .setPayload(payload)
                .build();
    }

    private static List<DeviceActionAvro> toDeviceActionAvro(List<DeviceAction> actions) {
        return actions.stream()
                .map(action -> DeviceActionAvro.newBuilder()
                        .setSensorId(action.getSensorId())
                        .setType(ActionTypeAvro.valueOf(action.getType().name()))
                        .setValue(action.getValue())
                        .build())
                .toList();
    }

    private static List<ScenarioConditionAvro> toScenarioConditionAvro(List<ScenarioCondition> conditions) {
        return conditions.stream()
                .map(condition -> ScenarioConditionAvro.newBuilder()
                        .setSensorId(condition.getSensorId())
                        .setType(ConditionTypeAvro.valueOf(condition.getType().name()))
                        .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()))
                        .setValue(condition.getValue())
                        .build())
                .toList();
    }
}