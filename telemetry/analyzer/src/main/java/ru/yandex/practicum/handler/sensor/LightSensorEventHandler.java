package ru.yandex.practicum.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.model.ConditionType;

@Slf4j
@Component
public class LightSensorEventHandler implements SensorEventHandler {

    @Override
    public String getSensorType() {
        return LightSensorAvro.class.getName();
    }

    @Override
    public Integer getSensorValue(ConditionType conditionType, SensorStateAvro sensorState) {
        LightSensorAvro lightSensorAvro = (LightSensorAvro) sensorState.getData();

        return switch (conditionType) {
            case LUMINOSITY -> lightSensorAvro.getLuminosity();
            default -> null;
        };
    }
}