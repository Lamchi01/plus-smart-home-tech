package ru.yandex.practicum.service;

import ru.yandex.practicum.model.AddressDto;
import ru.yandex.practicum.model.BookedProductsDto;
import ru.yandex.practicum.model.ShoppingCartDto;
import ru.yandex.practicum.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.request.ShippedToDeliveryRequest;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {
    void addProduct(NewProductInWarehouseRequest request);

    void shipped(ShippedToDeliveryRequest request);

    void returnProduct(Map<UUID, Integer> products);

    BookedProductsDto checkProduct(ShoppingCartDto cart);

    BookedProductsDto assembleProduct(AssemblyProductsForOrderRequest request);

    void addProductToWarehouse(AddProductToWarehouseRequest request);

    AddressDto getAddress();
}