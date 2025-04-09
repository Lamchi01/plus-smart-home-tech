package ru.yandex.practicum.mapper;

import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.model.ProductDto;

import java.util.List;

public class ProductMapper {
    public static ProductDto toProductDto(Product product) {
        return new ProductDto(
                product.getProductId(),
                product.getProductName(),
                product.getDescription(),
                product.getImageSrc(),
                product.getQuantityState(),
                product.getProductState(),
                product.getProductCategory(),
                product.getPrice()
        );
    }

    public static Product toProduct(ProductDto productDto) {
        Product product = new Product();
        product.setProductId(productDto.getProductId());
        product.setProductName(productDto.getProductName());
        product.setDescription(productDto.getDescription());
        product.setImageSrc(productDto.getImageSrc());
        product.setQuantityState(productDto.getQuantityState());
        product.setProductState(productDto.getProductState());
        product.setProductCategory(productDto.getProductCategory());
        product.setPrice(productDto.getPrice());
        return product;
    }

    public static List<ProductDto> toProductDtoList(List<Product> products) {
        return products.stream().map(ProductMapper::toProductDto).toList();
    }
}