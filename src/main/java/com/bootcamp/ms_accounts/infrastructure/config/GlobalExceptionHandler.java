package com.bootcamp.ms_accounts.infrastructure.config;

import com.bootcamp.ms_accounts.domain.model.exception.AccountNotFoundException;
import com.bootcamp.ms_accounts.domain.model.exception.BusinessAccountRestrictionException;
import com.bootcamp.ms_accounts.domain.model.exception.CustomerNotFoundException;
import com.bootcamp.ms_accounts.domain.model.exception.ExternalServiceUnavailableException;
import com.bootcamp.ms_accounts.domain.model.exception.InsufficientBalanceException;
import com.bootcamp.ms_accounts.infrastructure.adapters.inbound.rest.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessAccountRestrictionException.class)
    public ResponseEntity<ErrorResponse> handleBusinessAccountRestriction(
            BusinessAccountRestrictionException ex) {
        log.warn("Business account restriction: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse()
            .errorCode(ErrorResponse.ErrorCodeEnum.BUSINESS_ACCOUNT_RESTRICTION)
            .message(ex.getMessage())
            .timestamp(OffsetDateTime.now(ZoneOffset.UTC));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalance(
            InsufficientBalanceException ex) {
        log.warn("Insufficient balance: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse()
            .errorCode(ErrorResponse.ErrorCodeEnum.INSUFFICIENT_FUNDS)
            .message(ex.getMessage())
            .timestamp(OffsetDateTime.now(ZoneOffset.UTC));
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomerNotFound(CustomerNotFoundException ex) {
        log.warn("Customer not found: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse()
            .errorCode(ErrorResponse.ErrorCodeEnum.CUSTOMER_NOT_FOUND)
            .message(ex.getMessage())
            .timestamp(OffsetDateTime.now(ZoneOffset.UTC));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFound(AccountNotFoundException ex) {
        log.warn("Account not found: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse()
            .errorCode(ErrorResponse.ErrorCodeEnum.ACCOUNT_NOT_FOUND)
            .message(ex.getMessage())
            .timestamp(OffsetDateTime.now(ZoneOffset.UTC));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ExternalServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleExternalServiceUnavailable(
            ExternalServiceUnavailableException ex) {
        log.error("External service unavailable: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse()
            .errorCode(ErrorResponse.ErrorCodeEnum.INTERNAL_SERVER_ERROR)
            .message("External service temporarily unavailable. Please try again later.")
            .timestamp(OffsetDateTime.now(ZoneOffset.UTC));
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error: ", ex);
        ErrorResponse response = new ErrorResponse()
            .errorCode(ErrorResponse.ErrorCodeEnum.INTERNAL_SERVER_ERROR)
            .message("An unexpected error occurred. Please contact support.")
            .timestamp(OffsetDateTime.now(ZoneOffset.UTC));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
