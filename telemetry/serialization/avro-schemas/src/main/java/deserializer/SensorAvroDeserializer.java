package deserializer;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public class SensorAvroDeserializer extends BaseAvroDeserializer<SensorEventAvro> {
    public SensorAvroDeserializer() {
        super(SensorEventAvro.getClassSchema());
    }
}