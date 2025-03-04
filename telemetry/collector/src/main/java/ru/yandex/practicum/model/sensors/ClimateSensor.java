package ru.yandex.practicum.model.sensors;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.model.sensors.enums.SensorEventType;

@Getter
@Setter
@ToString(callSuper = true)
public class ClimateSensor extends SensorEvent {
    @NotNull
    private int temperatureC;
    @NotNull
    private int humidity;
    @NotNull
    private int co2Level;

    @Override
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}