package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.controller.OrderClient;
import ru.yandex.practicum.controller.WarehouseClient;
import ru.yandex.practicum.exception.NoDeliveryFoundException;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.DeliveryRepository;
import ru.yandex.practicum.request.ShippedToDeliveryRequest;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    private static final double BASE_RATE = 5.0;
    private static final double WAREHOUSE_1_ADDRESS_MULTIPLIER = 1.0;
    private static final double WAREHOUSE_2_ADDRESS_MULTIPLIER = 2.0;
    private static final double FRAGILE_MULTIPLIER = 0.2;
    private static final double WEIGHT_MULTIPLIER = 0.3;
    private static final double VOLUME_MULTIPLIER = 0.2;
    private static final double STREET_MULTIPLIER = 0.2;

    @Override
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        Delivery delivery = DeliveryMapper.toDelivery(deliveryDto);
        delivery = deliveryRepository.save(delivery);
        log.info("Планирование доставки: {}", delivery);
        return DeliveryMapper.toDeliveryDto(delivery);
    }

    @Override
    @Transactional
    public void deliverySuccessful(UUID deliveryId) {
        Delivery delivery = getDelivery(deliveryId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        delivery = deliveryRepository.save(delivery);
        orderClient.deliveryOrder(delivery.getOrderId());
        log.info("Доставка выполнена: {}", delivery);
    }

    @Override
    @Transactional
    public void deliveryPicked(UUID deliveryId) {
        Delivery delivery = getDelivery(deliveryId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        warehouseClient.shipped(new ShippedToDeliveryRequest(delivery.getOrderId(), deliveryId));
        orderClient.assembly(delivery.getOrderId());
        delivery = deliveryRepository.save(delivery);
        log.info("Доставка взята: {}", delivery);
    }

    @Override
    @Transactional
    public void deliveryFailed(UUID deliveryId) {
        Delivery delivery = getDelivery(deliveryId);
        delivery.setDeliveryState(DeliveryState.FAILED);
        delivery = deliveryRepository.save(delivery);
        orderClient.deliveryOrderFailed(delivery.getOrderId());
        log.info("Доставка не удалась: {}", delivery);
    }

    @Override
    public Double calculateDeliveryCost(OrderDto orderDto) {
        Delivery delivery = getDelivery(orderDto.getDeliveryId());
        Address warehouseAddress = delivery.getFromAddress();
        Address customerAddress = delivery.getToAddress();

        double totalCost = BASE_RATE;

        totalCost += warehouseAddress.getCity().equals("ADDRESS_1")
                ? totalCost * WAREHOUSE_1_ADDRESS_MULTIPLIER : totalCost * WAREHOUSE_2_ADDRESS_MULTIPLIER;

        totalCost += orderDto.getFragile() ? totalCost * FRAGILE_MULTIPLIER : 0;

        totalCost += orderDto.getDeliveryWeight() * WEIGHT_MULTIPLIER;

        totalCost += orderDto.getDeliveryVolume() * VOLUME_MULTIPLIER;

        totalCost += warehouseAddress.getStreet().equals(customerAddress.getStreet())
                ? 0 : totalCost * STREET_MULTIPLIER;

        log.info("Расчет стоимости доставки: {}", totalCost);
        return totalCost;
    }

    private Delivery getDelivery(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException("Доставка с id " + deliveryId + " не найдена"));
    }
}