package com.bootcamp.ms_accounts.application.ports.output;

import com.bootcamp.ms_accounts.domain.model.enums.CustomerType;
import reactor.core.publisher.Mono;

public interface CustomerClientPort {
    Mono<Boolean> validateCustomerExists(String customerId);
    Mono<CustomerType> getCustomerType(String customerId);
}
