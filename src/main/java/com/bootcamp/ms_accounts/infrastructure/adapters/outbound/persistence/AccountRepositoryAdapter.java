package com.bootcamp.ms_accounts.infrastructure.adapters.outbound.persistence;

import com.bootcamp.ms_accounts.application.ports.output.AccountRepositoryPort;
import com.bootcamp.ms_accounts.domain.model.dto.AccountModel;
import com.bootcamp.ms_accounts.domain.model.exception.AccountNotFoundException;
import com.bootcamp.ms_accounts.infrastructure.adapters.outbound.persistence.entity.AccountEntity;
import com.bootcamp.ms_accounts.infrastructure.adapters.outbound.persistence.mapper.ModelToPersistenceMapper;
import com.bootcamp.ms_accounts.infrastructure.adapters.outbound.persistence.repository.AccountMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRepositoryPort {

    private final AccountMongoRepository repository;
    private final ModelToPersistenceMapper modelToPersistenceMapper;

    @Override
    public Mono<AccountModel> save(AccountModel account) {
        AccountEntity entity = modelToPersistenceMapper.toEntity(account);
        entity.setAccountId(UUID.randomUUID().toString());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return repository.save(entity)
            .map(modelToPersistenceMapper::toDomainModel);
    }

    @Override
    public Mono<AccountModel> update(String accountId, AccountModel account) {
        return repository.findById(accountId)
            .switchIfEmpty(Mono.error(new AccountNotFoundException(
                "Account with ID " + accountId + " not found")))
            .flatMap(existingEntity -> {
                if (account.getStatus() != null) {
                    existingEntity.setStatus(account.getStatus().toString());
                }
                if (account.getAccountHolders() != null) {
                    existingEntity.setAccountHolders(modelToPersistenceMapper.toAccountHolderEntities(account.getAccountHolders()));
                }
                if (account.getAuthorizedSigners() != null) {
                    existingEntity.setAuthorizedSigners(modelToPersistenceMapper.toAuthorizedSignerEntities(account.getAuthorizedSigners()));
                }
                existingEntity.setUpdatedAt(LocalDateTime.now());
                return repository.save(existingEntity)
                    .map(modelToPersistenceMapper::toDomainModel);
            });
    }

    @Override
    public Mono<AccountModel> findById(String accountId) {
        return repository.findById(accountId)
            .switchIfEmpty(Mono.error(new AccountNotFoundException(
                "Account with ID " + accountId + " not found")))
            .map(modelToPersistenceMapper::toDomainModel);
    }

    @Override
    public Flux<AccountModel> findAll(int page, int size) {
        int offset = page * size;
        return repository.findAll()
            .skip(offset)
            .take(size)
            .map(modelToPersistenceMapper::toDomainModel);
    }

    @Override
    public Mono<Void> deleteById(String accountId) {
        return repository.deleteById(accountId);
    }

    @Override
    public Flux<AccountModel> findByCustomerId(String customerId) {
        return repository.findByCustomerId(customerId)
            .map(modelToPersistenceMapper::toDomainModel);
    }

    @Override
    public Mono<Long> countByCustomerIdAndAccountType(String customerId, String accountType) {
        return repository.countByCustomerIdAndAccountType(customerId, accountType);
    }
}
