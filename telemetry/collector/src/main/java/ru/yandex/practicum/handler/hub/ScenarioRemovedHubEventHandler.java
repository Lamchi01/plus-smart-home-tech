package ru.yandex.practicum.handler.hub;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.service.EventService;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ScenarioRemovedHubEventHandler implements HubEventHandler {
    private final EventService eventService;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    public void handle(HubEventProto hubEvent) {
        eventService.collectHubEvent(toHubEventAvro(hubEvent));
    }

    private HubEventAvro toHubEventAvro(HubEventProto hubEvent) {
        ScenarioRemovedEventProto scenarioProto = hubEvent.getScenarioRemoved();
        ScenarioRemovedEventAvro scenarioAvro = ScenarioRemovedEventAvro.newBuilder()
                .setName(scenarioProto.getName())
                .build();

        return HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(Instant.ofEpochSecond(hubEvent.getTimestamp().getSeconds(), hubEvent.getTimestamp().getNanos()))
                .setPayload(scenarioAvro)
                .build();
    }
}