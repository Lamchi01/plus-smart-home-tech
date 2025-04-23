package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.WarehouseOperations;
import ru.yandex.practicum.model.AddressDto;
import ru.yandex.practicum.model.BookedProductsDto;
import ru.yandex.practicum.model.ShoppingCartDto;
import ru.yandex.practicum.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.request.ShippedToDeliveryRequest;
import ru.yandex.practicum.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WarehouseController implements WarehouseOperations {
    private final WarehouseService warehouseService;

    @Override
    public void addProduct(NewProductInWarehouseRequest request) {
        log.info("Поступил запрос на добавление продукта в склад: {}", request);
        warehouseService.addProduct(request);
    }

    @Override
    public void shipped(ShippedToDeliveryRequest request) {

    }

    @Override
    public void returnProduct(Map<UUID, Integer> products) {

    }

    @Override
    public BookedProductsDto checkProduct(ShoppingCartDto cart) {
        log.info("Поступил запрос на проверку продуктов в корзине: {}", cart);
        return warehouseService.checkProduct(cart);
    }

    @Override
    public BookedProductsDto assembleProduct(AssemblyProductsForOrderRequest request) {
        return null;
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        log.info("Поступил запрос на приёмку продуктов в склад: {}", request);
        warehouseService.addProductToWarehouse(request);
    }

    @Override
    public AddressDto getAddress() {
        log.info("Поступил запрос на получение адреса склада");
        return warehouseService.getAddress();
    }
}