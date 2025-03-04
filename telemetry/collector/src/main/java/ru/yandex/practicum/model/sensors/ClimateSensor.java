package ru.yandex.practicum.model.sensors;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.model.sensors.enums.SensorType;

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

    public SensorType getType() {
        return SensorType.CLIMATE_SENSOR_EVENT;
    }
}