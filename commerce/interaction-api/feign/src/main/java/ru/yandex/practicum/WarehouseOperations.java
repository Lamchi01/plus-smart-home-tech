package ru.yandex.practicum;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.model.AddressDto;
import ru.yandex.practicum.model.BookedProductsDto;
import ru.yandex.practicum.model.ShoppingCartDto;
import ru.yandex.practicum.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.request.ShippedToDeliveryRequest;

import java.util.Map;
import java.util.UUID;

public interface WarehouseOperations {
    @PutMapping("/api/v1/warehouse")
    void addProduct(@RequestBody @Valid NewProductInWarehouseRequest request);

    @PostMapping("/api/v1/warehouse/shipped")
    void shipped(@Valid @RequestBody ShippedToDeliveryRequest request);

    @PostMapping("/api/v1/warehouse/return")
    void returnProduct(@RequestBody Map<UUID, Integer> products);

    @PostMapping("/api/v1/warehouse/check")
    BookedProductsDto checkProduct(@RequestBody ShoppingCartDto cart);

    @PostMapping("/api/v1/warehouse/assemble")
    BookedProductsDto assembleProduct(@Valid @RequestBody AssemblyProductsForOrderRequest request);

    @PostMapping("/api/v1/warehouse/add")
    void addProductToWarehouse(@RequestBody @Valid AddProductToWarehouseRequest request);

    @GetMapping("/api/v1/warehouse/address")
    AddressDto getAddress();
}