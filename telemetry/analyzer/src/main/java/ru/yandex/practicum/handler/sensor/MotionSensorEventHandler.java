package ru.yandex.practicum.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.model.ConditionType;

@Slf4j
@Component
public class MotionSensorEventHandler implements SensorEventHandler {
    @Override
    public String getSensorType() {
        return MotionSensorAvro.class.getName();
    }

    @Override
    public Integer getSensorValue(ConditionType conditionType, SensorStateAvro sensorState) {
        MotionSensorAvro motionSensorAvro = (MotionSensorAvro) sensorState.getData();

        return switch (conditionType) {
            case MOTION -> motionSensorAvro.getMotion() ? 1 : 0;
            default -> null;
        };
    }
}