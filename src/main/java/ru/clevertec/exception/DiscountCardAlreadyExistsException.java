package ru.clevertec.exception;

public class DiscountCardAlreadyExistsException extends RuntimeException {

    public DiscountCardAlreadyExistsException(String message) {
        super(message);
    }
}
