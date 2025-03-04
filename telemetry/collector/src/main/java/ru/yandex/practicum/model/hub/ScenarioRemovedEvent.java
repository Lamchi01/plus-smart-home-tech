package ru.yandex.practicum.model.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.model.hub.enums.EventType;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioRemovedEvent extends HubEvent {
    @NotBlank
    @Size(min = 3, message = "Название должно содержать не меньше 3 символов")
    private String name;

    public EventType getType() {
        return EventType.SCENARIO_REMOVED;
    }
}