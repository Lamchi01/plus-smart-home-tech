package ru.yandex.practicum.model.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.model.hub.enums.EventType;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioAddedEvent extends HubEvent {
    @NotBlank
    @Size(min = 3, message = "Название должно содержать не меньше 3 символов")
    private String name;
    @NotNull
    @NotEmpty
    private List<ScenarioCondition> conditions;
    @NotNull
    @NotEmpty
    private List<DeviceAction> actions;

    public EventType getType() {
        return EventType.SCENARIO_ADDED;
    }
}