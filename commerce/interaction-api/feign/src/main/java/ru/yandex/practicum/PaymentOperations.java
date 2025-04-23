package ru.yandex.practicum;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.model.PaymentDto;

import java.util.UUID;

public interface PaymentOperations {
    @PostMapping("/api/v1/payment")
    PaymentDto payment(@Valid @RequestBody OrderDto order);

    @PostMapping("/api/v1/payment/totalCost")
    Double totalCost(@Valid @RequestBody OrderDto order);

    @PostMapping("/api/v1/payment/refund")
    void refund(@RequestBody UUID paymentId);

    @PostMapping("/api/v1/payment/productCost")
    Double productCost(@Valid @RequestBody OrderDto order);

    @PostMapping("/api/v1/payment/failed")
    void paymentFailed(@RequestBody UUID paymentId);
}