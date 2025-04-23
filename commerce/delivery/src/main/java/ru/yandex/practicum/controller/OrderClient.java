package ru.yandex.practicum.controller;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.OrderOperations;

@FeignClient("order")
public interface OrderClient extends OrderOperations {
}