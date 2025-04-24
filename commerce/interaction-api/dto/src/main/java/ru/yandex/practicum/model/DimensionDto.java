package ru.yandex.practicum.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DimensionDto {
    @NotNull
    @Min(value = 1)
    private BigDecimal width;

    @NotNull
    @Min(value = 1)
    private BigDecimal height;

    @NotNull
    @Min(value = 1)
    private BigDecimal depth;
}