package ru.yandex.practicum.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.handler.hub.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka_client.KafkaClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component

public class HubEventProcessor implements Runnable {
    @Value("${kafka.topics.hubs-events}")
    private String hubTopic;
    private final KafkaClient kafkaClient;
    private final Map<String, HubEventHandler> handlerMap;

    public HubEventProcessor(KafkaClient kafkaClient, Set<HubEventHandler> hubEventHandlers) {
        this.kafkaClient = kafkaClient;
        this.handlerMap = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getEventType, Function.identity()));
    }

    @Override
    public void run() {
        Consumer<String, HubEventAvro> consumer = kafkaClient.getHubConsumer();
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            consumer.subscribe(List.of(hubTopic));
            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(Duration.ofSeconds(10));
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    HubEventAvro hubEvent = record.value();
                    handleRecord(hubEvent);
                    log.info("Обработано событие от хаба: {}", hubEvent);
                }
            }
        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от хабов", e);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }

    private void handleRecord(HubEventAvro hubEvent) {
        log.info("Обработка события от хаба: {}", hubEvent);
        String eventType = hubEvent.getPayload().getClass().getName();
        if (handlerMap.containsKey(eventType)) {
            HubEventHandler handler = handlerMap.get(eventType);
            log.info("Используем обработчик: {}", handler);
            handler.handle(hubEvent);
        } else {
            throw new IllegalArgumentException("Не найден обработчик для данного сценария");
        }
    }
}