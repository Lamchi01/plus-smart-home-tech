package ru.yandex.practicum;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.request.CreateNewOrderRequest;
import ru.yandex.practicum.request.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

public interface OrderOperations {
    @GetMapping("/api/v1/order")
    List<OrderDto> getOrdersUser(@RequestParam String username);

    @PutMapping("/api/v1/order")
    OrderDto createOrder(@Valid @RequestBody CreateNewOrderRequest request);

    @PostMapping("/api/v1/order/return")
    OrderDto returnOrder(@RequestBody @Valid ProductReturnRequest request);

    @PostMapping("/api/v1/order/payment")
    OrderDto paymentOrder(@RequestBody UUID orderId);

    @PostMapping("/api/v1/order/failed")
    OrderDto failedPaymentOrder(@RequestBody UUID orderId);

    @PostMapping("/api/v1/order/delivery")
    OrderDto deliveryOrder(@RequestBody UUID orderId);

    @PostMapping("/api/v1/order/delivery/failed")
    OrderDto deliveryOrderFailed(@RequestBody UUID orderId);

    @PostMapping("/api/v1/order/completed")
    OrderDto completeOrder(@RequestBody UUID orderId);

    @PostMapping("/api/v1/order/calculate/total")
    OrderDto calculateTotalCost(@RequestBody UUID orderId);

    @PostMapping("/api/v1/order/calculate/delivery")
    OrderDto calculateDeliveryCost(@RequestBody UUID orderId);

    @PostMapping("/api/v1/order/assembly")
    OrderDto assembly(@RequestBody UUID orderId);

    @PostMapping("/api/v1/order/assembly/failed")
    OrderDto assemblyFailed(@RequestBody UUID orderId);
}