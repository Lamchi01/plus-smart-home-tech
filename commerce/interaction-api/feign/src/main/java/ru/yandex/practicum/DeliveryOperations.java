package ru.yandex.practicum;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.model.DeliveryDto;
import ru.yandex.practicum.model.OrderDto;

import java.util.UUID;

public interface DeliveryOperations {
    @PutMapping("/api/v1/delivery")
    DeliveryDto planDelivery(@Valid @RequestBody DeliveryDto deliveryDto);

    @PostMapping("/api/v1/delivery/successful")
    void deliverySuccessful(@RequestBody UUID deliveryId);

    @PostMapping("/api/v1/delivery/picked")
    void deliveryPicked(@RequestBody UUID deliveryId);

    @PostMapping("/api/v1/delivery/failed")
    void deliveryFailed(@RequestBody UUID deliveryId);

    @PostMapping("/api/v1/delivery/cost")
    Double calculateDeliveryCost(@Valid @RequestBody OrderDto orderDto);
}