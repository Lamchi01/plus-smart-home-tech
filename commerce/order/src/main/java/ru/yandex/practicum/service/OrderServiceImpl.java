package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.controller.DeliveryClient;
import ru.yandex.practicum.controller.PaymentClient;
import ru.yandex.practicum.controller.WarehouseClient;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.request.CreateNewOrderRequest;
import ru.yandex.practicum.request.ProductReturnRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final WarehouseClient warehouseClient;
    private final DeliveryClient deliveryClient;
    private final PaymentClient paymentClient;

    @Override
    public List<OrderDto> getOrdersUser(String username) {
        validUser(username);
        List<Order> orders = orderRepository.findByUsername(username);
        log.info("Заказы пользователя: {}", orders);
        return orders.stream()
                .map(OrderMapper::toOrderDto)
                .toList();
    }

    @Override
    @Transactional
    public OrderDto createOrder(CreateNewOrderRequest request) {
        BookedProductsDto bookedProductsDto = warehouseClient.checkProduct(request.getShoppingCart());
        Order order = OrderMapper.toOrder(request, bookedProductsDto);
        orderRepository.save(order);

        AddressDto addressDto = warehouseClient.getAddress();
        DeliveryDto deliveryDto = DeliveryDto.builder()
                .fromAddress(addressDto)
                .toAddress(request.getDeliveryAddress())
                .orderId(order.getOrderId())
                .deliveryState(DeliveryState.CREATED)
                .build();
        deliveryDto = deliveryClient.planDelivery(deliveryDto);
        order.setDeliveryId(deliveryDto.getDeliveryId());
        log.info("Заказ создан: {}", order);
        return OrderMapper.toOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto returnOrder(ProductReturnRequest request) {
        Order order = getOrder(request.getOrderId());
        warehouseClient.returnProduct(order.getProducts());
        order.setState(OrderState.PRODUCT_RETURNED);
        orderRepository.save(order);
        log.info("Продукты возвращены: {}", order);
        return OrderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto paymentOrder(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.PAID);
        orderRepository.save(order);
        log.info("Заказ оплачен: {}", order);
        return OrderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto failedPaymentOrder(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        orderRepository.save(order);
        log.info("При оплате заказа произошла ошибка: {}", order);
        return OrderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto deliveryOrder(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.DELIVERED);
        orderRepository.save(order);
        log.info("Заказ доставлен: {}", order);
        return OrderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto deliveryOrderFailed(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        orderRepository.save(order);
        log.info("При доставке заказа произошла ошибка: {}", order);
        return OrderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto completeOrder(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.COMPLETED);
        orderRepository.save(order);
        log.info("Заказ завершен: {}", order);
        return OrderMapper.toOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto calculateTotalCost(UUID orderId) {
        Order order = getOrder(orderId);
        BigDecimal totalPrice = paymentClient.totalCost(OrderMapper.toOrderDto(order));
        order.setTotalPrice(totalPrice);
        orderRepository.save(order);
        log.info("Стоимость заказа: {}", order);
        return OrderMapper.toOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto calculateDeliveryCost(UUID orderId) {
        Order order = getOrder(orderId);
        BigDecimal deliveryPrice = deliveryClient.calculateDeliveryCost(OrderMapper.toOrderDto(order));
        order.setDeliveryPrice(deliveryPrice);
        orderRepository.save(order);
        log.info("Стоимость доставки заказа: {}", order);
        return OrderMapper.toOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto assembly(UUID orderId) {
        Order order = getOrder(orderId);

        AssemblyProductsForOrderRequest request = AssemblyProductsForOrderRequest.builder()
                .products(order.getProducts())
                .orderId(order.getOrderId())
                .build();
        warehouseClient.assembleProduct(request);

        order.setState(OrderState.ASSEMBLED);
        log.info("Заказ собран: {}", order);
        return OrderMapper.toOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        orderRepository.save(order);
        log.info("При сборке заказа произошла ошибка: {}", order);
        return OrderMapper.toOrderDto(orderRepository.save(order));
    }

    private void validUser(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя отсутствует или оно пустое");
        }
    }

    private Order getOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.info("Заказ не найден: {}", orderId);
                    return new NoOrderFoundException("Заказ не найден");
                });
    }
}
