package ru.yandex.practicum.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ShippedToDeliveryRequest {
    @NotNull
    private UUID orderId;

    @NotNull
    private UUID deliveryId;
}