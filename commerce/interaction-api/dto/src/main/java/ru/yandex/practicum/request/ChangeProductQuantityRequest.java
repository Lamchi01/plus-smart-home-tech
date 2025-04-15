package ru.yandex.practicum.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeProductQuantityRequest {
    @NotNull
    private UUID productId;

    @NotNull
    @Min(value = 1)
    private Integer newQuantity;
}