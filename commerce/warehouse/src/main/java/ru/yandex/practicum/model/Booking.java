package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@ToString
public class Booking {
    @Id
    @UuidGenerator
    private UUID bookingId;

    private Boolean fragile;

    private Double deliveryVolume;

    private Double deliveryWeight;

    @ElementCollection
    @CollectionTable(
            name = "booking_products",
            joinColumns = @JoinColumn(name = "booking_id"))
    @MapKeyColumn(name = "product_id")
    private Map<UUID, Integer> products;

    private UUID deliveryId;

    private UUID orderId;
}