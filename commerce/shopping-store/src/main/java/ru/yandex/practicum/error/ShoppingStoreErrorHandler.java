package ru.yandex.practicum.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.ApiError;
import ru.yandex.practicum.exception.ProductNotFoundException;

@RestControllerAdvice
public class ShoppingStoreErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleProductNotFound(ProductNotFoundException e) {
        return new ApiError(HttpStatus.NOT_FOUND, e, "Продукт не обнаружен");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(Exception e) {
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, e, "Внутренняя ошибка сервера");
    }
}