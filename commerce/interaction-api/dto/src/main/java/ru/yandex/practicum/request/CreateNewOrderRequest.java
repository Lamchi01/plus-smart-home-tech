package ru.yandex.practicum.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.model.AddressDto;
import ru.yandex.practicum.model.ShoppingCartDto;

@Data
@NoArgsConstructor
public class CreateNewOrderRequest {
    ShoppingCartDto shoppingCart;

    AddressDto deliveryAddress;
}