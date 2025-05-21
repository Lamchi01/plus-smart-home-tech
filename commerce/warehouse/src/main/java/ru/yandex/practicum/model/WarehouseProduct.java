package ru.yandex.practicum.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "warehouse_products")
@Getter
@Setter
@ToString
public class WarehouseProduct {
    @Id
    private UUID productId;

    private Boolean fragile;

    private BigDecimal width;

    private BigDecimal height;

    private BigDecimal depth;

    private BigDecimal weight;

    private Integer quantity;
}