package ru.yandex.practicum.handler.sensor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.service.EventService;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class LightSensorEventHandler implements SensorEventHandler {
    private final EventService eventService;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto sensorEvent) {
        eventService.collectSensorEvent(toSensorEventAvro(sensorEvent));
    }

    private SensorEventAvro toSensorEventAvro(SensorEventProto sensorEvent) {
        LightSensorProto lightProto = sensorEvent.getLightSensorEvent();
        LightSensorAvro lightAvro = LightSensorAvro.newBuilder()
                .setLinkQuality(lightProto.getLinkQuality())
                .setLuminosity(lightProto.getLuminosity())
                .build();

        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(Instant.ofEpochSecond(sensorEvent.getTimestamp().getSeconds(), sensorEvent.getTimestamp().getNanos()))
                .setPayload(lightAvro)
                .build();
    }
}