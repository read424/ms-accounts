package com.bootcamp.ms_accounts.infrastructure.adapters.outbound.http;

import com.bootcamp.ms_accounts.application.ports.output.CustomerClientPort;
import com.bootcamp.ms_accounts.domain.model.exception.ExternalServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerServiceClient implements CustomerClientPort {

    private final WebClient webClient;

    @Override
    @CircuitBreaker(name = "customerService", fallbackMethod = "fallbackValidateCustomer")
    @Retry(name = "customerService")
    @TimeLimiter(name = "customerService")
    public Mono<Boolean> validateCustomerExists(String customerId) {
        return webClient.get()
            .uri("/api/v1/customers/{customerId}", customerId)
            .retrieve()
            .toBodilessEntity()
            .map(response -> true)
            .onErrorReturn(false);
    }

    public Mono<Boolean> fallbackValidateCustomer(String customerId, Exception ex) {
        log.warn("Fallback activado para customer {}: {}", customerId, ex.getMessage());
        return Mono.error(
            new ExternalServiceUnavailableException(
                "customer-service",
                "El servicio de clientes no está disponible. Reintente más tarde.",
                ex
            )
        );
    }
}
