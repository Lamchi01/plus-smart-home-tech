package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.controller.OrderClient;
import ru.yandex.practicum.controller.ShoppingStoreClient;
import ru.yandex.practicum.exception.NoPaymentFoundException;
import ru.yandex.practicum.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.PaymentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;
    private static final double NDS = 0.1;

    @Override
    @Transactional
    public PaymentDto payment(OrderDto order) {
        validPrice(List.of(order.getTotalPrice(), order.getDeliveryPrice(), order.getProductPrice()));
        Payment payment = PaymentMapper.toPayment(order);
        payment = paymentRepository.save(payment);
        log.info("Платеж сохранен: {}", payment);
        return PaymentMapper.toPaymentDto(payment);
    }

    @Override
    @Transactional
    public Double totalCost(OrderDto order) {
        List<Double> prices = new ArrayList<>();
        Map<UUID, Integer> orderProducts = order.getProducts();

        orderProducts.forEach((id, quantity) -> {
            ProductDto productDto = shoppingStoreClient.getProductById(id);
            double totalPrice = productDto.getPrice() * quantity;
            prices.add(totalPrice);
        });

        double totalCost = prices.stream().mapToDouble(Double::doubleValue).sum();
        log.info("Общая стоимость заказа: {}", totalCost);
        return totalCost;
    }

    @Override
    @Transactional
    public void refund(UUID paymentId) {
        Payment payment = getPayment(paymentId);
        payment.setPaymentState(PaymentState.SUCCESS);
        orderClient.paymentOrder(payment.getOrderId());
        paymentRepository.save(payment);
        log.info("Платеж проведен: {}", payment);
    }

    @Override
    public Double productCost(OrderDto order) {
        validPrice(List.of(order.getTotalPrice(), order.getDeliveryPrice(), order.getProductPrice()));
        double productCost = order.getProductPrice();
        double deliveryCost = order.getDeliveryPrice();
        double totalCost = deliveryCost + productCost + (productCost * NDS);
        log.info("Общая стоимость продуктов: {}", totalCost);
        return totalCost;
    }

    @Override
    @Transactional
    public void paymentFailed(UUID paymentId) {
        Payment payment = getPayment(paymentId);
        payment.setPaymentState(PaymentState.FAILED);
        orderClient.failedPaymentOrder(payment.getOrderId());
        paymentRepository.save(payment);
        log.info("Платеж провален: {}", payment);
    }

    private Payment getPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    log.info("Платеж не найден: {}", paymentId);
                    return new NoPaymentFoundException("Платеж не найден");
                });
    }

    private void validPrice(List<Double> prices) {
        for (Double price : prices) {
            if (price == null || price < 0) {
                throw new NotEnoughInfoInOrderToCalculateException("Недостаточно информации для формирования платежа");
            }
        }
    }
}