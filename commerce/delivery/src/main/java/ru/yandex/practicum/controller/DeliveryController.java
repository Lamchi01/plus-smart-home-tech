package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.DeliveryOperations;
import ru.yandex.practicum.model.DeliveryDto;
import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DeliveryController implements DeliveryOperations {
    private final DeliveryService deliveryService;

    @Override
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        log.info("Планирование доставки: {}", deliveryDto);
        return deliveryService.planDelivery(deliveryDto);
    }

    @Override
    public void deliverySuccessful(UUID deliveryId) {
        log.info("Доставка выполнена: {}", deliveryId);
        deliveryService.deliverySuccessful(deliveryId);
    }

    @Override
    public void deliveryPicked(UUID deliveryId) {
        log.info("Доставка взята: {}", deliveryId);
        deliveryService.deliveryPicked(deliveryId);
    }

    @Override
    public void deliveryFailed(UUID deliveryId) {
        log.info("Доставка не удалась: {}", deliveryId);
        deliveryService.deliveryFailed(deliveryId);
    }

    @Override
    public Double calculateDeliveryCost(OrderDto orderDto) {
        log.info("Расчет стоимости доставки: {}", orderDto);
        return deliveryService.calculateDeliveryCost(orderDto);
    }
}