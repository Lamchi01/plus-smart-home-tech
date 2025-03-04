package ru.yandex.practicum.model.hub;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.model.hub.enums.DeviceType;
import ru.yandex.practicum.model.hub.enums.EventType;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceAddedEvent extends HubEvent {
    @NotBlank
    private String id;
    @NotBlank
    private DeviceType deviceType;

    public EventType getType() {
        return EventType.DEVICE_ADDED;
    }
}