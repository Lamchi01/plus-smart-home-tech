package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

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

    private Double totalPayment;

    private Double deliveryTotal;

    private Double feeTotal;

    @Enumerated(EnumType.STRING)
    private PaymentState paymentState;
}