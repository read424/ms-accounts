package com.bootcamp.ms_accounts.infrastructure.config;

import com.bootcamp.ms_accounts.domain.model.exception.ExternalServiceUnavailableException;
import com.bootcamp.ms_accounts.infrastructure.adapters.inbound.rest.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExternalServiceUnavailableException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleExternalServiceUnavailable(
            ExternalServiceUnavailableException ex) {
        log.warn("Servicio externo no disponible: {} - {}", ex.getServiceName(), ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(ErrorResponse.ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
        errorResponse.setDetails(Map.of("serviceName", ex.getServiceName()));

        return Mono.just(ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(errorResponse));
    }
}
