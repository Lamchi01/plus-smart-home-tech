package ru.yandex.practicum.mapper;

import ru.yandex.practicum.model.OrderDto;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.model.PaymentDto;
import ru.yandex.practicum.model.PaymentState;

public class PaymentMapper {
    public static Payment toPayment(OrderDto order) {
        Payment payment = new Payment();
        payment.setPaymentState(PaymentState.PENDING);
        payment.setTotalPayment(order.getProductPrice());
        payment.setDeliveryTotal(order.getDeliveryPrice());
        payment.setFeeTotal(order.getTotalPrice());
        return payment;
    }

    public static PaymentDto toPaymentDto(Payment payment) {
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setPaymentId(paymentDto.getPaymentId());
        paymentDto.setDeliveryTotal(payment.getDeliveryTotal());
        paymentDto.setTotalPrice(payment.getTotalPayment());
        paymentDto.setFeeTotal(payment.getFeeTotal());
        return paymentDto;
    }
}