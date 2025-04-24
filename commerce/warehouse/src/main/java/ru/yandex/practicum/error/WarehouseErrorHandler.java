package ru.yandex.practicum.error;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.ApiError;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;

@RestControllerAdvice
public class WarehouseErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleSpecifiedProductAlreadyInWarehouseException(SpecifiedProductAlreadyInWarehouseException e) {
        return new ApiError(HttpStatus.BAD_REQUEST, e, "Продукт уже есть на складе");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleProductInShoppingCartLowQuantityInWarehouse(ProductInShoppingCartLowQuantityInWarehouse e) {
        return new ApiError(HttpStatus.BAD_REQUEST, e, "Недостаточно товара на складе");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleNoSpecifiedProductInWarehouseException(NoSpecifiedProductInWarehouseException e) {
        return new ApiError(HttpStatus.BAD_REQUEST, e, "Выбранный продукт отсутствует на складе");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraintViolationException(ConstraintViolationException e) {
        return new ApiError(HttpStatus.BAD_REQUEST, e, "Некорректные данные");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleArgumentNotValidException(MethodArgumentNotValidException e) {
        return new ApiError(HttpStatus.BAD_REQUEST, e, "Недостающие данные");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowable(Exception exception) {
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, exception, "Внутренняя ошибка сервера");
    }
}