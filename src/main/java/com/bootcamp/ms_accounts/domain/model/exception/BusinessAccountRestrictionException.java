package com.bootcamp.ms_accounts.domain.model.exception;

public class BusinessAccountRestrictionException extends RuntimeException {
    public BusinessAccountRestrictionException(String message) {
        super(message);
    }

    public BusinessAccountRestrictionException(String message, Throwable cause) {
        super(message, cause);
    }
}
