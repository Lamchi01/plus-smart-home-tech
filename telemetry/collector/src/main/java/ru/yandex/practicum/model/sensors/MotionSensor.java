package ru.yandex.practicum.model.sensors;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.model.sensors.enums.SensorType;

@Getter
@Setter
@ToString(callSuper = true)
public class MotionSensor extends SensorEvent {
    @NotNull
    private int linkQuality;
    @NotNull
    private boolean motion;
    @NotNull
    private int voltage;

    public SensorType getType() {
        return SensorType.MOTION_SENSOR_EVENT;
    }
}