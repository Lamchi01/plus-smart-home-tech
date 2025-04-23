package ru.yandex.practicum.service;

import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.model.PaymentDto;

import java.util.UUID;

public interface PaymentService {
    PaymentDto payment(OrderDto order);

    Double totalCost(OrderDto order);

    void refund(UUID paymentId);

    Double productCost(OrderDto order);

    void paymentFailed(UUID paymentId);
}