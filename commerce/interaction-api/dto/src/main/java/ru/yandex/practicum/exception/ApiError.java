package ru.yandex.practicum.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ApiError {
    private Throwable cause;
    private StackTraceElement[] stackTrace;
    private HttpStatus httpStatus;
    private String userMessage;
    private String message;
    private Throwable[] suppressed;
    private String localizedMessage;

    public ApiError(HttpStatus httpStatus, Exception e, String userMessage) {
        this.cause = e.getCause();
        this.stackTrace = e.getStackTrace();
        this.httpStatus = httpStatus;
        this.userMessage = userMessage;
        this.message = e.getMessage();
        this.suppressed = e.getSuppressed();
        this.localizedMessage = e.getLocalizedMessage();
    }
}