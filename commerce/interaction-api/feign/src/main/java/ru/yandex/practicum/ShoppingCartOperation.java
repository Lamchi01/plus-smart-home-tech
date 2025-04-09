package ru.yandex.practicum;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.ShoppingCartDto;
import ru.yandex.practicum.request.ChangeProductQuantityRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartOperation {
    @GetMapping("/api/v1/shopping-cart")
    ShoppingCartDto getShoppingCarts(@RequestParam String username);

    @PutMapping("/api/v1/shopping-cart")
    ShoppingCartDto addProductToShoppingCart(@RequestParam String username, @RequestBody Map<UUID, Integer> products);

    @DeleteMapping("/api/v1/shopping-cart")
    void deactivateShoppingCart(@RequestParam String username);

    @PostMapping("/api/v1/shopping-cart/remove")
    ShoppingCartDto removeProductFromShoppingCart(@RequestParam String username, @RequestBody List<UUID> productIds);

    @PostMapping("/api/v1/shopping-cart/change-quantity")
    ShoppingCartDto changeProductQuantity(@RequestParam String username, @RequestBody ChangeProductQuantityRequest request);
}