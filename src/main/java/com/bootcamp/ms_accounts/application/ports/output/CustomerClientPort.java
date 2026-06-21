package com.bootcamp.ms_accounts.application.ports.output;

import reactor.core.publisher.Mono;

public interface CustomerClientPort {
    Mono<Boolean> validateCustomerExists(String customerId);
}
