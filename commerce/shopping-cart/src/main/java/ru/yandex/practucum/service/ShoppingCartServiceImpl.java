package ru.yandex.practucum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.WarehouseOperations;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.model.ShoppingCartDto;
import ru.yandex.practicum.request.ChangeProductQuantityRequest;
import ru.yandex.practucum.mapper.CartMapper;
import ru.yandex.practucum.model.ShoppingCart;
import ru.yandex.practucum.model.ShoppingCartState;
import ru.yandex.practucum.repository.ShoppingCartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final WarehouseOperations warehouseOperations;

    @Override
    public ShoppingCartDto getShoppingCarts(String username) {
        log.info("Процесс получения корзины пользователя: {}", username);
        checkUser(username);
        ShoppingCart cart = getShoppingCart(username);
        log.info("Корзина пользователя: {}", cart);
        return CartMapper.toShoppingCartDto(cart);
    }

    @Override
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Integer> products) {
        log.info("Процесс добавления продуктов в корзину: {}", products);
        checkUser(username);
        log.info("Пользователь найден: {}", username);
        ShoppingCart cart = getShoppingCart(username);
        Map<UUID, Integer> oldProducts = cart.getProducts();
        oldProducts.putAll(products);
        cart.setProducts(oldProducts);
        ShoppingCartDto cartDto = CartMapper.toShoppingCartDto(cart);
        log.info("Отправляю запрос на проверку наличия на складе: {}", cartDto);
        warehouseOperations.checkProduct(cartDto);
        log.info("Запрос на проверку наличия на складе прошёл");
        shoppingCartRepository.save(cart);
        log.info("Продукты добавлены в корзину: {}", cartDto);
        return cartDto;
    }

    @Override
    public void deactivateShoppingCart(String username) {
        log.info("Процесс деактивации корзины пользователя: {}", username);
        checkUser(username);
        ShoppingCart cart = getShoppingCart(username);
        cart.setState(ShoppingCartState.DEACTIVATE);
        shoppingCartRepository.save(cart);
        log.info("Корзина деактивирована: {}", cart);
    }

    @Override
    public ShoppingCartDto removeProductFromShoppingCart(String username, List<UUID> productIds) {
        log.info("Процесс удаления продуктов из корзины: {}", productIds);
        checkUser(username);
        ShoppingCart cart = getShoppingCart(username);
        Map<UUID, Integer> products = cart.getProducts();
        productIds.forEach(products::remove);
        shoppingCartRepository.save(cart);
        log.info("Продукты удалены из корзины: {}", productIds);
        return CartMapper.toShoppingCartDto(cart);
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        log.info("Процесс изменения количества продукта в корзине: {}", request);
        checkUser(username);
        ShoppingCart cart = getShoppingCart(username);
        Map<UUID, Integer> products = cart.getProducts();
        products.put(request.getProductId(), request.getNewQuantity());
        ShoppingCartDto cartDto = CartMapper.toShoppingCartDto(cart);
        log.info("Отправляю запрос на проверку наличия на складе: {}", cartDto);
        warehouseOperations.checkProduct(cartDto);
        log.info("Запрос на проверку наличия на складе прошёл");
        shoppingCartRepository.save(cart);
        log.info("Количество продукта в корзине изменено: {}", cartDto);
        return cartDto;
    }

    private void checkUser(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("User " + username + " not found");
        }
    }

    private ShoppingCart getShoppingCart(String username) {
        return shoppingCartRepository.findByUsernameAndState(username, ShoppingCartState.ACTIVE)
                .orElseGet(
                        () -> {
                            ShoppingCart newShoppingCart = new ShoppingCart();
                            newShoppingCart.setUsername(username);
                            newShoppingCart.setState(ShoppingCartState.ACTIVE);
                            newShoppingCart.setProducts(new HashMap<>());
                            return shoppingCartRepository.save(newShoppingCart);
                        }
                );
    }
}