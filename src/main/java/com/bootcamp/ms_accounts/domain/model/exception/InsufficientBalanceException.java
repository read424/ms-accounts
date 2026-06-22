package com.bootcamp.ms_accounts.domain.model.exception;

import lombok.Getter;

@Getter
public class InsufficientBalanceException extends RuntimeException {

    private final int statusCode;

    public InsufficientBalanceException(String message) {
        this(message, 422);
    }

    public InsufficientBalanceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public InsufficientBalanceException(String message, Throwable cause) {
        this(message, 422, cause);
    }

    public InsufficientBalanceException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }
}
