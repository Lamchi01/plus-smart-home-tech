package ru.yandex.practicum.error;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.ApiError;
import ru.yandex.practicum.exception.NoPaymentFoundException;
import ru.yandex.practicum.exception.NotEnoughInfoInOrderToCalculateException;

@RestControllerAdvice
public class PaymentErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNoPaymentFound(NoPaymentFoundException e) {
        return new ApiError(HttpStatus.NOT_FOUND, e, "Платеж не обнаружен");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotEnoughInfoInOrderToCalculate(NotEnoughInfoInOrderToCalculateException e) {
        return new ApiError(HttpStatus.BAD_REQUEST, e, "Недостаточно информации о заказе");
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
    public ApiError handleThrowable(Exception e) {
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, e, "Внутренняя ошибка сервера");
    }
}