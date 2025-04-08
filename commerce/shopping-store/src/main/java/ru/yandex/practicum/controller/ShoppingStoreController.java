package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.Pageable;
import ru.yandex.practicum.model.ProductDto;
import ru.yandex.practicum.request.SetProductQuantityStateRequest;
import ru.yandex.practicum.service.ShoppingStoreService;

import java.util.Collection;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-store")
public class ShoppingStoreController {
    private final ShoppingStoreService shoppingStoreService;

    @GetMapping
    public Collection<ProductDto> getProducts(String category, Pageable pageable) {
        log.info("Поступил запрос на получение списка продуктов по категории: {}", category);
        return shoppingStoreService.getProducts(category, pageable);
    }

    @PutMapping
    public ProductDto addProduct(@Valid @RequestBody ProductDto productDto) {
        log.info("Поступил запрос на добавление продукта: {}", productDto);
        return shoppingStoreService.addProduct(productDto);
    }

    @PostMapping
    public ProductDto updateProduct(@Valid @RequestBody ProductDto productDto) {
        log.info("Поступил запрос на обновление продукта: {}", productDto);
        return shoppingStoreService.updateProduct(productDto);
    }

    @PostMapping("/removeProductFromStore")
    public void removeProduct(@RequestBody UUID productId) {
        log.info("Поступил запрос на удаление продукта: {}", productId);
        shoppingStoreService.removeProduct(productId);
    }

    @PostMapping("/quantityState")
    public void setProductState(@RequestBody @Valid SetProductQuantityStateRequest request) {
        log.info("Поступил запрос на изменение состояния продукта: {}", request);
        shoppingStoreService.setProductState(request);
    }

    @GetMapping("/{productId}")
    public ProductDto getProductById(@PathVariable UUID productId) {
        log.info("Поступил запрос на получение продукта по id: {}", productId);
        return shoppingStoreService.getProductById(productId);
    }
}