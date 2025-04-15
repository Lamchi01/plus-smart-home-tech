package ru.yandex.practicum;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.ProductDto;
import ru.yandex.practicum.request.SetProductQuantityStateRequest;

import java.util.List;
import java.util.UUID;

public interface ShoppingStoreOperations {
    @GetMapping("/api/v1/shopping-store")
    List<ProductDto> getProducts(String category, @RequestParam Integer page,
                                 @RequestParam Integer size,
                                 @RequestParam List<String> sort);

    @PutMapping("/api/v1/shopping-store")
    ProductDto addProduct(@Valid @RequestBody ProductDto productDto);

    @PostMapping("/api/v1/shopping-store")
    ProductDto updateProduct(@Valid @RequestBody ProductDto productDto);

    @PostMapping("/api/v1/shopping-store/removeProductFromStore")
    void removeProduct(@RequestBody UUID productId);

    @PostMapping("/api/v1/shopping-store/quantityState")
    void setProductState(@RequestBody SetProductQuantityStateRequest request);

    @GetMapping("/api/v1/shopping-store/{productId}")
    ProductDto getProductById(@PathVariable UUID productId);
}