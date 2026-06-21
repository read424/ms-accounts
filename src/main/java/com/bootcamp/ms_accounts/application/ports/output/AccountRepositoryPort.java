package com.bootcamp.ms_accounts.application.ports.output;

import com.bootcamp.ms_accounts.domain.model.dto.AccountModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountRepositoryPort {
    Mono<AccountModel> save(AccountModel account);
    Mono<AccountModel> update(String accountId, AccountModel account);
    Mono<AccountModel> findById(String accountId);
    Flux<AccountModel> findAll(int page, int size);
    Mono<Void> deleteById(String accountId);
    Flux<AccountModel> findByCustomerId(String customerId);
    Mono<Long> countByCustomerIdAndAccountType(String customerId, String accountType);
}
