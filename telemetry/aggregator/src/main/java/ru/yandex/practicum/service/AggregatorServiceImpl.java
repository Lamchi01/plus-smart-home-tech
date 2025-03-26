package ru.yandex.practicum.service;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AggregatorServiceImpl implements AggregatorService {
    @Value("${kafka.topics.snapshots-events}")
    private String snapshotTopic;
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>(); // Key - HubId

    public void sendSnapshot(Producer<String, SpecificRecordBase> producer,
                            SensorEventAvro sensorEvent) {
        Optional<SensorsSnapshotAvro> updatedSnapshot = updateState(sensorEvent);
        if (updatedSnapshot.isPresent()) {
            SensorsSnapshotAvro getSnapshot = updatedSnapshot.get();
            producer.send(new ProducerRecord<>(
                    snapshotTopic,
                    null,
                    getSnapshot.getTimestamp().toEpochMilli(),
                    getSnapshot.getHubId(),
                    getSnapshot
            ));
        }
    }

    private Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        if (!snapshots.containsKey(event.getHubId())) {
            SensorsSnapshotAvro newSnapshot = new SensorsSnapshotAvro();
            newSnapshot.setSensorsState(new HashMap<>());
            snapshots.put(event.getHubId(), newSnapshot);
        }

        SensorsSnapshotAvro snapshot = snapshots.get(event.getHubId());
        if (snapshot.getSensorsState().containsKey(event.getId())) {
            SensorStateAvro oldState = snapshot.getSensorsState().get(event.getId());
            if (oldState.getTimestamp().isAfter(event.getTimestamp()) || oldState.getData().equals(event.getPayload())) {
                return Optional.empty();
            }
        }

        SensorStateAvro newState = new SensorStateAvro();
        newState.setTimestamp(event.getTimestamp());
        newState.setData(event.getPayload());

        snapshot.setHubId(event.getHubId());
        snapshot.setTimestamp(event.getTimestamp());
        snapshot.getSensorsState().put(event.getId(), newState);
        snapshots.put(event.getHubId(), snapshot);
        return Optional.of(snapshot);
    }
}