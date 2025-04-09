package ru.yandex.practicum.service;

import ru.yandex.practicum.model.Pageable;
import ru.yandex.practicum.model.ProductDto;
import ru.yandex.practicum.request.SetProductQuantityStateRequest;

import java.util.List;
import java.util.UUID;

public interface ShoppingStoreService {
    ProductDto getProductById(UUID productId);

    ProductDto addProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    void removeProduct(UUID productId);

    void setProductState(SetProductQuantityStateRequest request);

    List<ProductDto> getProducts(String category, Pageable pageable);
}