package ru.yandex.practicum.mapper;

import ru.yandex.practicum.model.BookedProductsDto;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.model.OrderState;
import ru.yandex.practicum.request.CreateNewOrderRequest;

public class OrderMapper {
    public static OrderDto toOrderDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderId(order.getOrderId());
        orderDto.setShoppingCartId(order.getShoppingCartId());
        orderDto.setProducts(order.getProducts());
        orderDto.setPaymentId(order.getPaymentId());
        orderDto.setDeliveryId(order.getDeliveryId());
        orderDto.setState(order.getState());
        orderDto.setDeliveryWeight(order.getDeliveryWeight());
        orderDto.setDeliveryVolume(order.getDeliveryVolume());
        orderDto.setFragile(order.getFragile());
        orderDto.setTotalPrice(order.getTotalPrice());
        orderDto.setDeliveryPrice(order.getDeliveryPrice());
        orderDto.setProductPrice(order.getProductPrice());
        return orderDto;
    }

    public static Order toOrder(CreateNewOrderRequest request, BookedProductsDto bookedProductsDto) {
        Order order = new Order();
        order.setShoppingCartId(request.getShoppingCart().getShoppingCartId());
        order.setProducts(request.getShoppingCart().getProducts());
        order.setDeliveryWeight(bookedProductsDto.getDeliveryWeight());
        order.setDeliveryVolume(bookedProductsDto.getDeliveryVolume());
        order.setFragile(bookedProductsDto.getFragile());
        order.setState(OrderState.NEW);
        return order;
    }
}