package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "delivery")
@Getter
@Setter
@ToString
public class Delivery {
    @Id
    @UuidGenerator
    private UUID deliveryId;

    @ManyToOne
    @JoinColumn(name = "from_address_id")
    private Address fromAddress;

    @ManyToOne
    @JoinColumn(name = "to_address_id")
    private Address toAddress;

    private UUID orderId;

    @Enumerated(EnumType.STRING)
    private DeliveryState deliveryState;
}