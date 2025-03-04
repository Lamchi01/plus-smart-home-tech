package ru.yandex.practicum.model.sensors;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.model.sensors.enums.SensorType;

@Getter
@Setter
@ToString(callSuper = true)
public class TemperatureSensor extends SensorEvent {
    @NotNull
    private int temperatureC;
    @NotNull
    private int temperatureF;

    public SensorType getType() {
        return SensorType.TEMPERATURE_SENSOR_EVENT;
    }
}