package ru.practicum.shareit.util.exception;

public class NoAccessException extends RuntimeException {
    public NoAccessException(String message) {
        super(message);
    }
}