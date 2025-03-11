package ru.yandex.practicum.handler.hub;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.service.EventService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScenarioAddedHubEventHandler implements HubEventHandler {
    private final EventService eventService;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public void handle(HubEventProto hubEvent) {
        eventService.collectHubEvent(toHubEventAvro(hubEvent));
    }

    private HubEventAvro toHubEventAvro(HubEventProto hubEvent) {
        ScenarioAddedEventProto scenarioProto = hubEvent.getScenarioAdded();
        ScenarioAddedEventAvro scenarioAvro = ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioProto.getName())
                .setConditions(toScenarioConditionAvro(scenarioProto.getConditionsList()))
                .setActions(toDeviceActionAvro(scenarioProto.getActionsList()))
                .build();

        return HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(Instant.ofEpochSecond(hubEvent.getTimestamp().getSeconds(), hubEvent.getTimestamp().getNanos()))
                .setPayload(scenarioAvro)
                .build();

    }

    private List<ScenarioConditionAvro> toScenarioConditionAvro(List<ScenarioConditionProto> conditions) {
        List<ScenarioConditionAvro> result = new ArrayList<>();
        Object value = null;

        for (ScenarioConditionProto condition : conditions) {
            if (condition.getValueCase() == ScenarioConditionProto.ValueCase.INT_VALUE) {
                value = condition.getIntValue();
            } else if (condition.getValueCase() == ScenarioConditionProto.ValueCase.BOOL_VALUE) {
                value = condition.getBoolValue();
            }

            result.add(ScenarioConditionAvro.newBuilder()
                    .setSensorId(condition.getSensorId())
                    .setValue(value)
                    .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()))
                    .setType(ConditionTypeAvro.valueOf(condition.getType().name()))
                    .build());
        }
        return result;
    }

    private List<DeviceActionAvro> toDeviceActionAvro(List<DeviceActionProto> actions) {
        List<DeviceActionAvro> result = new ArrayList<>();
        for (DeviceActionProto action : actions) {
            result.add(DeviceActionAvro.newBuilder()
                    .setSensorId(action.getSensorId())
                    .setType(ActionTypeAvro.valueOf(action.getType().name()))
                    .setValue(action.getValue())
                    .build());
        }
        return result;
    }
}