package ru.yandex.practicum.model.sensors;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.model.sensors.enums.SensorType;

@Getter
@Setter
@ToString(callSuper = true)
public class SwitchSensor extends SensorEvent {
    @NotNull
    private boolean state;

    public SensorType getType() {
        return SensorType.SWITCH_SENSOR_EVENT;
    }
}