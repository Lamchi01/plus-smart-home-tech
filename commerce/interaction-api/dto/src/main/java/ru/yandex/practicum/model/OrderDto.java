package ru.yandex.practicum.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    @NotNull
    private UUID orderId;

    private UUID shoppingCartId;

    @NotNull
    private Map<UUID, Integer> products;

    private UUID paymentId;

    private UUID deliveryId;

    private OrderState state;

    private BigDecimal deliveryWeight;

    private BigDecimal deliveryVolume;

    private Boolean fragile;

    private BigDecimal totalPrice;

    private BigDecimal deliveryPrice;

    private BigDecimal productPrice;
}