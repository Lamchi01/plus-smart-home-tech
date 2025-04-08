package ru.yandex.practicum.model;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DimensionDto {
    @DecimalMin(value = "1")
    private Double width;

    @DecimalMin(value = "1")
    private Double height;

    @DecimalMin(value = "1")
    private Double depth;
}