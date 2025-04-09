package ru.yandex.practicum.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.controller.ShoppingStoreClient;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.WarehouseRepository;
import ru.yandex.practicum.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.request.SetProductQuantityStateRequest;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final ShoppingStoreClient shoppingStoreClient;
    private static final AddressDto[] ADDRESSES =
            new AddressDto[]{
                    new AddressDto("ADDRESS_1",
                            "ADDRESS_1",
                            "ADDRESS_1",
                            "ADDRESS_1",
                            "ADDRESS_1"),
                    new AddressDto("ADDRESS_2",
                            "ADDRESS_2",
                            "ADDRESS_2",
                            "ADDRESS_2",
                            "ADDRESS_2")};
    private static final AddressDto CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 1)];

    @Override
    public void addProduct(NewProductInWarehouseRequest request) {
        log.info("Процесс добавления продукта в склад: {}", request);
        checkProductInWarehouse(request.getProductId());
        WarehouseProduct product = WarehouseMapper.toWarehouseProduct(request);
        product.setQuantity(0);
        warehouseRepository.save(product);
        log.info("Продукт добавлен в склад: {}", product);
    }

    @Override
    public BookedProductsDto checkProduct(ShoppingCartDto cart) {
        log.info("Процесс проверки продуктов в корзине: {}", cart);
        UUID shoppingCartId = cart.getShoppingCartId();
        Map<UUID, Integer> products = cart.getProducts();
        Supplier<Stream<WarehouseProduct>> warehouseProducts =
                () -> warehouseRepository.findAllById(products.keySet()).stream();
        checkQuantityInWarehouse(warehouseProducts.get(), products, shoppingCartId);
        return calculateDeliveryParams(warehouseProducts);
    }

    @Override
    @Transactional
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        log.info("Процесс приёмки товара: {}", request);
        WarehouseProduct product = getWarehouseProduct(request.getProductId());
        Integer quantity = product.getQuantity();
        quantity += request.getQuantity();
        product.setQuantity(quantity);
        warehouseRepository.save(product);
        updateProductQuantityInShoppingStore(product);
        log.info("Товар принят: {}", product);
    }

    @Override
    public AddressDto getAddress() {
        log.info("Процесс получения адреса склада: {}", CURRENT_ADDRESS);
        return CURRENT_ADDRESS;
    }

    private BookedProductsDto calculateDeliveryParams(Supplier<Stream<WarehouseProduct>> warehouseProducts) {
        BookedProductsDto dto = new BookedProductsDto();
        dto.setDeliveryWeight(
                warehouseProducts.get()
                        .map(WarehouseProduct::getWeight)
                        .reduce(0.0, Double::sum)
        );

        dto.setDeliveryVolume(
                warehouseProducts.get()
                        .map(product -> product.getWidth() * product.getHeight() * product.getDepth())
                        .reduce(0.0, Double::sum));

        dto.setFragile(
                warehouseProducts.get().anyMatch(WarehouseProduct::getFragile));
        log.info("Параметры доставки: {}", dto);
        return dto;
    }

    private void checkQuantityInWarehouse(Stream<WarehouseProduct> warehouseProducts, Map<UUID, Integer> products, UUID cartId) {
        if (warehouseProducts.anyMatch(product -> product.getQuantity() < products.get(product.getProductId()))) {
            throw new ProductInShoppingCartLowQuantityInWarehouse("Недостаточно продуктов на складе для корзины с id " + cartId);
        }
    }

    private WarehouseProduct getWarehouseProduct(UUID productId) {
        return warehouseRepository.findById(productId)
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Данного продукта сейчас нет на складе"));
    }

    private void checkProductInWarehouse(UUID productId) {
        warehouseRepository.findById(productId)
                .ifPresent(product -> {
                    throw new SpecifiedProductAlreadyInWarehouseException("Продукт уже находится на складе");
                });
    }

    private void updateProductQuantityInShoppingStore(WarehouseProduct product) {
        Integer quantity = product.getQuantity();
        QuantityState quantityState;

        if (quantity == 0) {
            quantityState = QuantityState.ENDED;
        } else if (quantity < 10) {
            quantityState = QuantityState.FEW;
        } else if (quantity < 100 && quantity > 10) {
            quantityState = QuantityState.ENOUGH;
        } else {
            quantityState = QuantityState.MANY;
        }
        try {
            shoppingStoreClient.setProductState(new SetProductQuantityStateRequest(product.getProductId(), quantityState));
        } catch (FeignException e) {
            log.error("Процесс изменения состояния продукта в магазине завершен с ошибкой: {}", e.getMessage());
        }
    }
}