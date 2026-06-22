package com.bootcamp.ms_accounts.domain.model.exception;

import lombok.Getter;

@Getter
public class BusinessAccountRestrictionException extends RuntimeException {

    private final int statusCode;

    public BusinessAccountRestrictionException(String message) {
        this(message, 400);
    }

    public BusinessAccountRestrictionException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public BusinessAccountRestrictionException(String message, Throwable cause) {
        this(message, 409, cause);
    }

    public BusinessAccountRestrictionException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }
}
