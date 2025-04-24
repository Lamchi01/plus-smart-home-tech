package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.PaymentOperations;
import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.model.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentController implements PaymentOperations {
    private final PaymentService paymentService;

    @Override
    public PaymentDto payment(OrderDto order) {
        log.info("Оплата заказа: {}", order);
        return paymentService.payment(order);
    }

    @Override
    public BigDecimal totalCost(OrderDto order) {
        log.info("Расчет стоимости заказа: {}", order);
        return paymentService.totalCost(order);
    }

    @Override
    public void refund(UUID paymentId) {
        log.info("Возврат платежа: {}", paymentId);
        paymentService.refund(paymentId);
    }

    @Override
    public BigDecimal productCost(OrderDto order) {
        log.info("Расчет стоимости продуктов: {}", order);
        return paymentService.productCost(order);
    }

    @Override
    public void paymentFailed(UUID paymentId) {
        log.info("Платеж провален: {}", paymentId);
        paymentService.paymentFailed(paymentId);
    }
}