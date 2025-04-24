package ru.yandex.practicum.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookedProductsDto {
    @NotNull
    private BigDecimal deliveryWeight;

    @NotNull
    private BigDecimal deliveryVolume;

    @NotNull
    private Boolean fragile;
}