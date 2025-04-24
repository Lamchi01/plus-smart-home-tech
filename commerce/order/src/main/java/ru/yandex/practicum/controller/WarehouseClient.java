package ru.yandex.practicum.controller;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.WarehouseOperations;

@FeignClient(name = "warehouse")
public interface WarehouseClient extends WarehouseOperations {
}