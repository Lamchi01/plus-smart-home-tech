package ru.yandex.practicum.handler.sensor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.service.EventService;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class SwitchSensorEventHandler implements SensorEventHandler {
    private final EventService eventService;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto sensorEvent) {
        eventService.collectSensorEvent(toSensorEventAvro(sensorEvent));
    }

    private SensorEventAvro toSensorEventAvro(SensorEventProto sensorEvent) {
        SwitchSensorProto switchProto = sensorEvent.getSwitchSensorEvent();
        SwitchSensorAvro switchAvro = SwitchSensorAvro.newBuilder()
                .setState(switchProto.getState())
                .build();

        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(Instant.ofEpochSecond(sensorEvent.getTimestamp().getSeconds(), sensorEvent.getTimestamp().getNanos()))
                .setPayload(switchAvro)
                .build();
    }
}