package ru.clevertec.exception;

public class SQLNotFoundException extends RuntimeException {

    public SQLNotFoundException(String message) {
        super(message);
    }
}
