package ru.yandex.practicum.controller;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.DeliveryOperations;

@FeignClient("delivery")
public interface DeliveryClient extends DeliveryOperations {
}