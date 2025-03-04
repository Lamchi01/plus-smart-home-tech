package ru.yandex.practicum.model.hub;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.model.hub.enums.EventType;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceRemovedEvent extends HubEvent {
    @NotBlank
    private String id;

    public EventType getType() {
        return EventType.DEVICE_REMOVED;
    }
}