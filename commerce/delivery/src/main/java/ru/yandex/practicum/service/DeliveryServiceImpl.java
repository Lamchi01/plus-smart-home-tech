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

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    private static final BigDecimal BASE_RATE = BigDecimal.valueOf(5);
    private static final BigDecimal WAREHOUSE_1_ADDRESS_MULTIPLIER = BigDecimal.valueOf(1.0);
    private static final BigDecimal WAREHOUSE_2_ADDRESS_MULTIPLIER = BigDecimal.valueOf(2.0);
    private static final BigDecimal FRAGILE_MULTIPLIER = BigDecimal.valueOf(0.2);
    private static final BigDecimal WEIGHT_MULTIPLIER = BigDecimal.valueOf(0.3);
    private static final BigDecimal VOLUME_MULTIPLIER = BigDecimal.valueOf(0.2);
    private static final BigDecimal STREET_MULTIPLIER = BigDecimal.valueOf(0.2);

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
    public BigDecimal calculateDeliveryCost(OrderDto orderDto) {
        Delivery delivery = getDelivery(orderDto.getDeliveryId());
        Address warehouseAddress = delivery.getFromAddress();
        Address customerAddress = delivery.getToAddress();

        BigDecimal totalCost = BASE_RATE;

        BigDecimal warehouseMultiplier = warehouseAddress.getCity().equals("ADDRESS_1")
                ? WAREHOUSE_1_ADDRESS_MULTIPLIER : WAREHOUSE_2_ADDRESS_MULTIPLIER;
        totalCost = totalCost.add(totalCost.multiply(warehouseMultiplier));

        if (orderDto.getFragile()) {
            totalCost = totalCost.add(totalCost.multiply(FRAGILE_MULTIPLIER));
        }

        BigDecimal weight = orderDto.getDeliveryWeight().multiply(WEIGHT_MULTIPLIER);
        totalCost = totalCost.add(weight);

        BigDecimal volume = orderDto.getDeliveryVolume().multiply(VOLUME_MULTIPLIER);
        totalCost = totalCost.add(volume);

        if (!warehouseAddress.getCity().equals(customerAddress.getCity())) {
            totalCost = totalCost.add(totalCost.multiply(STREET_MULTIPLIER));
        }

        log.info("Расчет стоимости доставки: {}", totalCost);
        return totalCost;
    }

    private Delivery getDelivery(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException("Доставка с id " + deliveryId + " не найдена"));
    }
}