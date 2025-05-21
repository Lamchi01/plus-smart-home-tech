package ru.yandex.practicum.service;

import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.model.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {
    PaymentDto payment(OrderDto order);

    BigDecimal totalCost(OrderDto order);

    void refund(UUID paymentId);

    BigDecimal productCost(OrderDto order);

    void paymentFailed(UUID paymentId);
}