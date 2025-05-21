package ru.yandex.practicum.controller;


import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.PaymentOperations;

@FeignClient("payment")
public interface PaymentClient extends PaymentOperations {
}