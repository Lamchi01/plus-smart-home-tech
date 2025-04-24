package ru.yandex.practicum.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.controller.OrderClient;
import ru.yandex.practicum.controller.ShoppingStoreClient;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.BookingMapper;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.BookingRepository;
import ru.yandex.practicum.repository.WarehouseRepository;
import ru.yandex.practicum.request.*;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
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
    private final BookingRepository bookingRepository;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;
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
    @Transactional
    public void shipped(ShippedToDeliveryRequest request) {
        Booking booking = bookingRepository.findByOrderId(request.getOrderId());
        booking.setDeliveryId(request.getDeliveryId());
        bookingRepository.save(booking);
        log.info("Процесс отгрузки продуктов: {}", request);
    }

    @Override
    public void returnProduct(Map<UUID, Integer> products) {
        List<AddProductToWarehouseRequest> requests = products.entrySet().stream()
                .map(entry -> new AddProductToWarehouseRequest(entry.getKey(), entry.getValue()))
                .toList();
        requests.forEach(this::addProductToWarehouse);
        log.info("Процесс возврата продуктов: {}", requests);
    }

    @Override
    public BookedProductsDto checkProduct(ShoppingCartDto cart) {
        log.info("Процесс проверки продуктов в корзине: {}", cart);
        Map<UUID, Integer> products = cart.getProducts();
        Supplier<Stream<WarehouseProduct>> warehouseProducts =
                () -> warehouseRepository.findAllById(products.keySet()).stream();
        checkQuantityInWarehouse(warehouseProducts.get(), products);
        return calculateDeliveryParams(warehouseProducts);
    }

    @Override
    @Transactional
    public BookedProductsDto assembleProduct(AssemblyProductsForOrderRequest request) {
        Map<UUID, Integer> products = request.getProducts();
        Supplier<Stream<WarehouseProduct>> warehouseProducts =
                () -> warehouseRepository.findAllById(products.keySet()).stream();
        try {
            checkQuantityInWarehouse(warehouseProducts.get(), products);
        } catch (ProductInShoppingCartLowQuantityInWarehouse e) {
            orderClient.assemblyFailed(request.getOrderId());
            throw e;
        }

        BookedProductsDto dto = calculateDeliveryParams(warehouseProducts);
        processBookingInWarehouse(products);
        Booking booking = BookingMapper.toBooking(dto, request);
        booking = bookingRepository.save(booking);
        log.info("Продукты забронированы для отгрузки: {}", booking);
        return BookingMapper.toBookedProductsDto(booking);
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
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        dto.setDeliveryVolume(
                warehouseProducts.get()
                        .map(product ->
                                product.getWidth().multiply(product.getHeight()).multiply(product.getDepth()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        dto.setFragile(
                warehouseProducts.get().anyMatch(WarehouseProduct::getFragile));
        log.info("Параметры доставки: {}", dto);
        return dto;
    }

    private void checkQuantityInWarehouse(Stream<WarehouseProduct> warehouseProducts, Map<UUID, Integer> products) {
        if (warehouseProducts.anyMatch(product -> product.getQuantity() < products.get(product.getProductId()))) {
            throw new ProductInShoppingCartLowQuantityInWarehouse("Недостаточно продуктов на складе");
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

    private void processBookingInWarehouse(Map<UUID, Integer> products) {
        products.forEach((productId, quantity) -> {
            WarehouseProduct product = getWarehouseProduct(productId);
            int oldQuantity = product.getQuantity();
            int decreasedQuantity = quantity;
            product.setQuantity(oldQuantity - decreasedQuantity);
            warehouseRepository.save(product);
            updateProductQuantityInShoppingStore(product);
        });
    }
}