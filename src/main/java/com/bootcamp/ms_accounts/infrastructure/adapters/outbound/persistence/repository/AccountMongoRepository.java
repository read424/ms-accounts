package com.bootcamp.ms_accounts.infrastructure.adapters.outbound.persistence.repository;

import com.bootcamp.ms_accounts.infrastructure.adapters.outbound.persistence.entity.AccountEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AccountMongoRepository extends ReactiveMongoRepository<AccountEntity, String> {
    Flux<AccountEntity> findByCustomerId(String customerId);

    Mono<Long> countByCustomerIdAndAccountType(String customerId, String accountType);
}
