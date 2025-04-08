package ru.yandex.practucum.mapper;

import ru.yandex.practicum.model.ShoppingCartDto;
import ru.yandex.practucum.model.ShoppingCart;

public class CartMapper {
    public static ShoppingCartDto toShoppingCartDto(ShoppingCart cart) {
        return new ShoppingCartDto(cart.getShoppingCartId(), cart.getProducts());
    }
}