package ru.yandex.practicum.controller;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.ShoppingStoreOperations;

@FeignClient("shopping-store")
public interface ShoppingStoreClient extends ShoppingStoreOperations {
}