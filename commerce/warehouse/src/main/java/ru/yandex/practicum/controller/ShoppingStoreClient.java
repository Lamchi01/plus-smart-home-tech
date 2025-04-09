package ru.yandex.practicum.controller;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.ShoppingStoreOperations;

@FeignClient(name = "shopping-store")
public interface ShoppingStoreClient extends ShoppingStoreOperations {
}