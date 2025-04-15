package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.ShoppingStoreOperations;
import ru.yandex.practicum.model.Pageable;
import ru.yandex.practicum.model.ProductDto;
import ru.yandex.practicum.request.SetProductQuantityStateRequest;
import ru.yandex.practicum.service.ShoppingStoreService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ShoppingStoreController implements ShoppingStoreOperations {
    private final ShoppingStoreService shoppingStoreService;

    @Override
    public List<ProductDto> getProducts(String category, Integer page, Integer size, List<String> sort) {
        log.info("Поступил запрос на получение списка продуктов по категории: {}", category);
        return shoppingStoreService.getProducts(category, new Pageable(page, size, sort));
    }

    @Override
    public ProductDto addProduct(ProductDto productDto) {
        log.info("Поступил запрос на добавление продукта: {}", productDto);
        return shoppingStoreService.addProduct(productDto);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        log.info("Поступил запрос на обновление продукта: {}", productDto);
        return shoppingStoreService.updateProduct(productDto);
    }

    @Override
    public void removeProduct(UUID productId) {
        log.info("Поступил запрос на удаление продукта: {}", productId);
        shoppingStoreService.removeProduct(productId);
    }

    @Override
    public void setProductState(SetProductQuantityStateRequest request) {
        log.info("Поступил запрос на изменение состояния продукта: {}", request);
        shoppingStoreService.setProductState(request);
    }

    @Override
    public ProductDto getProductById(UUID productId) {
        log.info("Поступил запрос на получение продукта по id: {}", productId);
        return shoppingStoreService.getProductById(productId);
    }
}