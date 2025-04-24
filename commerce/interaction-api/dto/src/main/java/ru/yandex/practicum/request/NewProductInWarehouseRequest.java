package ru.yandex.practicum.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.model.DimensionDto;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewProductInWarehouseRequest {
    @NotNull
    private UUID productId;

    private Boolean fragile;

    @NotNull
    private DimensionDto dimension;

    @NotNull
    @DecimalMin(value = "1")
    private BigDecimal weight;
}