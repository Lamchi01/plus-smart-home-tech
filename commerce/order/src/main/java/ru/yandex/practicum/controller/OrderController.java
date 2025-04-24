package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.OrderOperations;
import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.request.CreateNewOrderRequest;
import ru.yandex.practicum.request.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController implements OrderOperations {
    private final OrderService orderService;

    @Override
    public List<OrderDto> getOrdersUser(String username) {
        log.info("Получение заказов пользователя: {}", username);
        return orderService.getOrdersUser(username);
    }

    @Override
    public OrderDto createOrder(CreateNewOrderRequest request) {
        log.info("Создание заказа: {}", request);
        return orderService.createOrder(request);
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequest request) {
        log.info("Возвращение продуктов: {}", request);
        return orderService.returnOrder(request);
    }

    @Override
    public OrderDto paymentOrder(UUID orderId) {
        log.info("Оплата заказа: {}", orderId);
        return orderService.paymentOrder(orderId);
    }

    @Override
    public OrderDto failedPaymentOrder(UUID orderId) {
        log.info("При оплате заказа произошла ошибка: {}", orderId);
        return orderService.failedPaymentOrder(orderId);
    }

    @Override
    public OrderDto deliveryOrder(UUID orderId) {
        log.info("Заказ доставлен: {}", orderId);
        return orderService.deliveryOrder(orderId);
    }

    @Override
    public OrderDto deliveryOrderFailed(UUID orderId) {
        log.info("При доставке заказа произошла ошибка: {}", orderId);
        return orderService.deliveryOrderFailed(orderId);
    }

    @Override
    public OrderDto completeOrder(UUID orderId) {
        log.info("Заказ завершен: {}", orderId);
        return orderService.completeOrder(orderId);
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        log.info("Стоимость заказа: {}", orderId);
        return orderService.calculateTotalCost(orderId);
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.info("Стоимость доставки заказа: {}", orderId);
        return orderService.calculateDeliveryCost(orderId);
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        log.info("Заказ собран: {}", orderId);
        return orderService.assembly(orderId);
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        log.info("При сборке заказа произошла ошибка: {}", orderId);
        return orderService.assemblyFailed(orderId);
    }
}