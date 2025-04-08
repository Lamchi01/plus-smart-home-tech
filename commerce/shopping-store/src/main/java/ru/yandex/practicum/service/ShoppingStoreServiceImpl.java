package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.ShoppingStoreRepository;
import ru.yandex.practicum.request.SetProductQuantityStateRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingStoreServiceImpl implements ShoppingStoreService {
    private final ShoppingStoreRepository repository;
    @Override
    public ProductDto getProductById(UUID productId) {
        log.info("Процесс получение продукта по id: {}", productId);
        Product product = findProduct(productId);
        log.info("Получен продукт: {}", product);
        return ProductMapper.toProductDto(product);
    }

    @Override
    public ProductDto addProduct(ProductDto productDto) {
        log.info("Процесс добавления продукта: {}", productDto);
        Product product = repository.save(ProductMapper.toProduct(productDto));
        log.info("Продукт добавлен: {}", product);
        return ProductMapper.toProductDto(product);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        log.info("Процесс обновления продукта: {}", productDto);
        findProduct(productDto.getProductId());
        Product product = repository.save(ProductMapper.toProduct(productDto));
        log.info("Продукт обновлен: {}", product);
        return ProductMapper.toProductDto(product);
    }

    @Override
    public void removeProduct(UUID productId) {
        log.info("Процесс удаления продукта: {}", productId);
        Product product = findProduct(productId);
        product.setProductState(ProductState.DEACTIVATE);
        repository.save(product);
        log.info("Продукт удален со склада: {}", productId);
    }

    @Override
    public void setProductState(SetProductQuantityStateRequest request) {
        log.info("Процесс изменения состояния продукта: {}", request);
        Product product = findProduct(request.getProductId());
        product.setQuantityState(request.getQuantityState());
        repository.save(product);
        log.info("Состояние продукта изменено: {}", product);
    }

    @Override
    public Collection<ProductDto> getProducts(String category, Pageable pageable) {
        log.info("Процесс получения продуктов по категории: {}", category);
        Sort sort = createSort(pageable.getSort());
        PageRequest pageRequest = PageRequest.of(
                pageable.getPage(),
                pageable.getSize(),
                sort
        );
        List<Product> products = repository.findByProductCategory(ProductCategory.valueOf(category), pageRequest);
        log.info("Получено продуктов по категории: {}", products);
        return ProductMapper.toProductDtoList(products);
    }

    private Product findProduct(UUID productId) {
        return repository.findById(productId).orElseThrow(
                () -> new ProductNotFoundException("Продукт с id " + productId + " не найден")
        );
    }

    private Sort createSort(List<String> sortFields) {
        if (sortFields == null || sortFields.isEmpty()) {
            return Sort.unsorted();
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (String field : sortFields) {
            if (field.startsWith("-")) {
                orders.add(Sort.Order.desc(field.substring(1)));
            } else {
                orders.add(Sort.Order.asc(field));
            }
        }
        return Sort.by(orders);
    }
}