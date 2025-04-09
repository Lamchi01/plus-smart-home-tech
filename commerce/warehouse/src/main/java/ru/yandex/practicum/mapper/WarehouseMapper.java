package ru.yandex.practicum.mapper;

import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;

public class WarehouseMapper {
    public static WarehouseProduct toWarehouseProduct(NewProductInWarehouseRequest request) {
        WarehouseProduct product = new WarehouseProduct();
        product.setProductId(request.getProductId());
        product.setFragile(request.getFragile());
        product.setWidth(request.getDimension().getWidth());
        product.setHeight(request.getDimension().getHeight());
        product.setDepth(request.getDimension().getDepth());
        product.setWeight(request.getWeight());
        return product;
    }
}