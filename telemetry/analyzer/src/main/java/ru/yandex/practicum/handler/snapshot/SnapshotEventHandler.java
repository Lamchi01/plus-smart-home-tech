package ru.yandex.practicum.handler.snapshot;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.handler.sensor.SensorEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.ConditionOperation;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SnapshotEventHandler {

    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterControllerBlockingStub;

    private final ScenarioRepository scenarioRepository;
    private final Map<String, SensorEventHandler> sensorEventHandlers;

    public SnapshotEventHandler(ScenarioRepository scenarioRepository, Set<SensorEventHandler> sensorEventHandlers) {
        this.scenarioRepository = scenarioRepository;
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getSensorType, Function.identity()));
    }

    public void handle(SensorsSnapshotAvro snapshot) {
        log.info("Обработка снапшота: {}", snapshot);
        List<Scenario> scenarios = scenarioRepository.findByHubId(snapshot.getHubId());
        Map<String, SensorStateAvro> sensorStates = snapshot.getSensorsState();

        scenarios = scenarios.stream()
                .filter(scenario -> checkConditions(scenario.getConditions(), sensorStates))
                .toList();

        for (Scenario scenario : scenarios) {
            List<Action> actions = scenario.getActions();
            for (Action action : actions) {
                DeviceActionProto deviceAction = DeviceActionProto.newBuilder()
                        .setSensorId(action.getSensor().getId())
                        .setType(ActionTypeProto.valueOf(action.getType().name()))
                        .setValue(action.getValue())
                        .build();

                Timestamp timestamp = Timestamp.newBuilder()
                        .setSeconds(Instant.now().getEpochSecond())
                        .setNanos(Instant.now().getNano())
                        .build();

                DeviceActionRequest request = DeviceActionRequest.newBuilder()
                        .setHubId(snapshot.getHubId())
                        .setScenarioName(scenario.getName())
                        .setAction(deviceAction)
                        .setTimestamp(timestamp)
                        .build();

                hubRouterControllerBlockingStub.handleDeviceAction(request);
                log.info("Отправлено действие: {}", request);
            }
        }
    }

    private boolean checkConditions(List<Condition> conditions, Map<String, SensorStateAvro> sensorStates) {
        if (conditions == null || conditions.isEmpty()) {
            log.warn("Снапшот не содержит условий");
            return true;
        }

        if (sensorStates == null || sensorStates.isEmpty()) {
            log.warn("Снапшот не содержит состояний датчиков");
            return false;
        }

        return conditions.stream()
                .allMatch(condition -> checkCondition(condition, sensorStates.get(condition.getSensor().getId())));
    }

    private boolean checkCondition(Condition condition, SensorStateAvro sensorStates) {
        if (condition == null) {
            log.warn("Условие не может быть null");
            return false;
        }

        if (sensorStates == null) {
            log.warn("State состояния датчика не может быть null");
            return false;
        }

        if (sensorStates.getData() == null) {
            log.warn("Data состояния датчика не может быть null");
            return false;
        }
        String type = sensorStates.getData().getClass().getName();

        if (!sensorEventHandlers.containsKey(type)) {
            throw new IllegalArgumentException("Не могу найти обработчик для датчика " + type);
        }

        Integer value = sensorEventHandlers.get(type).getSensorValue(condition.getType(), sensorStates);

        if (value == null) {
            log.warn("Значение датчика не может быть null");
            return false;
        }

        return switch (condition.getOperation()) {
            case ConditionOperation.EQUALS -> value.equals(condition.getValue());
            case ConditionOperation.GREATER_THAN -> value > condition.getValue();
            case ConditionOperation.LOWER_THAN -> value < condition.getValue();
        };
    }
}