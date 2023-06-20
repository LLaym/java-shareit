package ru.practicum.shareit.util.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.util.exception.NoAccessException;
import ru.practicum.shareit.util.exception.NotFoundException;

@Slf4j
@RestControllerAdvice("ru.practicum.shareit")
public class ErrorHandler {

    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Error handleNotFoundException(final Throwable e) {
        String errorName = "Ошибка поиска";
        String errorDescription = e.getMessage();
        log.warn("Произошла ошибка: {}. Описание: {}", errorName, errorDescription);
        return new Error(errorName, errorDescription);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleValidationException(final Throwable e) {
        String errorName = "Ошибка валидации";
        String errorDescription = e.getMessage();
        log.warn("Произошла ошибка: {}. Описание: {}", errorName, errorDescription);
        return new Error(errorName, errorDescription);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Error handleThrowable(final Throwable e) {
        String errorName = "Непредвиденная ошибка";
        String errorDescription = e.getMessage();
        log.warn("Произошла ошибка: {}. Описание: {}", errorName, errorDescription);
        return new Error(errorName, errorDescription);
    }

    @ExceptionHandler(NoAccessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleNoAccessException(final Throwable e) {
        String errorName = "Ошибка доступа";
        String errorDescription = e.getMessage();
        log.warn("Произошла ошибка: {}. Описание: {}", errorName, errorDescription);
        return new Error(errorName, errorDescription);
    }
}