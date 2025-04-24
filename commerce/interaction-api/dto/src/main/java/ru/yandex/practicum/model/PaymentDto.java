package ru.yandex.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private String paymentId;
    private BigDecimal totalPrice;
    private BigDecimal deliveryTotal;
    private BigDecimal feeTotal;
}