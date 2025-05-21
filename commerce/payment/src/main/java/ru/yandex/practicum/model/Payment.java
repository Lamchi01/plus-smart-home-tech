package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@ToString
public class Payment {
    @Id
    @UuidGenerator
    private UUID paymentId;

    private UUID orderId;

    private BigDecimal totalPayment;

    private BigDecimal deliveryTotal;

    private BigDecimal feeTotal;

    @Enumerated(EnumType.STRING)
    private PaymentState paymentState;
}