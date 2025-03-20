package ru.yandex.practicum.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.model.ConditionType;

@Slf4j
@Component
public class ClimateSensorEventHandler implements SensorEventHandler {

    @Override
    public String getSensorType() {
        return ClimateSensorAvro.class.getName();
    }

    @Override
    public Integer getSensorValue(ConditionType conditionType, SensorStateAvro sensorState) {
        ClimateSensorAvro climateSensorAvro = (ClimateSensorAvro) sensorState.getData();

        return switch (conditionType) {
            case TEMPERATURE -> climateSensorAvro.getTemperatureC();
            case HUMIDITY -> climateSensorAvro.getHumidity();
            case CO2LEVEL -> climateSensorAvro.getCo2Level();
            default -> null;
        };
    }
}