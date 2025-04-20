package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.ShoppingCartOperations;
import ru.yandex.practicum.model.ShoppingCartDto;
import ru.yandex.practicum.request.ChangeProductQuantityRequest;
import ru.yandex.practicum.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ShoppingCartController implements ShoppingCartOperations {
    private final ShoppingCartService shoppingCartService;

    @Override
    public ShoppingCartDto getShoppingCarts(String username) {
        log.info("Поступил запрос на получение корзин пользователя: {}", username);
        return shoppingCartService.getShoppingCarts(username);
    }

    @Override
    public ShoppingCartDto addProductToShoppingCart(String username, @RequestBody Map<UUID, Integer> products) {
        log.info("Поступил запрос на добавление продуктов в корзину: {}", products);
        return shoppingCartService.addProductToShoppingCart(username, products);
    }

    @Override
    public void deactivateShoppingCart(String username) {
        log.info("Поступил запрос на деактивацию корзины пользователя: {}", username);
        shoppingCartService.deactivateShoppingCart(username);
    }

    @Override
    public ShoppingCartDto removeProductFromShoppingCart(String username, @RequestBody List<UUID> productIds) {
        log.info("Поступил запрос на удаление продуктов из корзины: {}", productIds);
        return shoppingCartService.removeProductFromShoppingCart(username, productIds);
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, @RequestBody ChangeProductQuantityRequest request) {
        log.info("Поступил запрос на изменение количества продукта в корзине: {}", request);
        return shoppingCartService.changeProductQuantity(username, request);
    }
}