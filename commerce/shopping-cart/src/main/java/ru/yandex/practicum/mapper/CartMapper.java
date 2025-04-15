package ru.yandex.practicum.mapper;

import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.model.ShoppingCartDto;

public class CartMapper {
    public static ShoppingCartDto toShoppingCartDto(ShoppingCart cart) {
        return new ShoppingCartDto(cart.getShoppingCartId(), cart.getProducts());
    }
}