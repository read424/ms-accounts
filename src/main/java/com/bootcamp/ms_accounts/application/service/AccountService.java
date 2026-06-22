package com.bootcamp.ms_accounts.application.service;

import com.bootcamp.ms_accounts.application.ports.input.*;
import com.bootcamp.ms_accounts.domain.model.dto.*;
import com.bootcamp.ms_accounts.domain.model.enums.AccountTypeModel;
import com.bootcamp.ms_accounts.domain.model.exception.BusinessAccountRestrictionException;
import com.bootcamp.ms_accounts.domain.model.exception.CustomerNotFoundException;
import com.bootcamp.ms_accounts.domain.model.exception.AccountNotFoundException;
import com.bootcamp.ms_accounts.domain.model.mapper.RequestToModelMapper;
import com.bootcamp.ms_accounts.domain.model.dto.UpdateAccountModel;
import com.bootcamp.ms_accounts.domain.model.validation.AccountOwnershipValidationService;
import com.bootcamp.ms_accounts.application.ports.output.AccountRepositoryPort;
import com.bootcamp.ms_accounts.application.ports.output.CustomerClientPort;
import com.bootcamp.ms_accounts.infrastructure.adapters.outbound.messaging.KafkaProducer;
import com.bootcamp.ms_accounts.infrastructure.adapters.outbound.messaging.DepositCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountService implements
        CreateAccountUseCase,
        GetAccountByIdUseCase,
        GetAccountsByCustomerUseCase,
        GetAllAccountsUseCase,
        UpdateAccountUseCase,
        DeleteAccountUseCase,
        GetAccountBalanceUseCase,
        ProcessDepositUseCase {

    private final AccountRepositoryPort accountRepositoryPort;
    private final CustomerClientPort customerClientPort;
    private final KafkaProducer kafkaProducer;
    private final AccountOwnershipValidationService validationService;

    @Override
    public Mono<AccountModel> createAccount(AccountModel request) {
        return customerClientPort.validateCustomerExists(request.getCustomerId())
            .switchIfEmpty(Mono.error(new CustomerNotFoundException(
                "Customer with ID " + request.getCustomerId() + " not found")))
            .flatMap(valid -> validateAccountOwnershipRules(request)
                .thenReturn(request))
            .flatMap(this::buildAndSaveAccount);
    }

    @Override
    public Mono<AccountModel> getAccountById(String accountId) {
        return accountRepositoryPort.findById(accountId)
            .switchIfEmpty(Mono.error(new AccountNotFoundException(
                "Account with ID " + accountId + " not found")))
            .flatMap(response -> Mono.just(response));
    }

    @Override
    public Flux<AccountModel> getAccountsByCustomer(String customerId) {
        return accountRepositoryPort.findByCustomerId(customerId)
            .flatMap(response -> Mono.just(response));
    }

    @Override
    public Flux<AccountModel> getAllAccounts(int page, int size) {
        return accountRepositoryPort.findAll(page, size)
            .flatMap(response -> Mono.just(response));
    }

    @Override
    public Mono<AccountModel> updateAccount(String accountId, UpdateAccountModel request) {
        return accountRepositoryPort.findById(accountId)
            .switchIfEmpty(Mono.error(new AccountNotFoundException(
                "Account with ID " + accountId + " not found")))
            .flatMap(accountModel -> {
                return accountRepositoryPort.update(accountId, accountModel);
            });
    }

    @Override
    public Mono<Void> deleteAccount(String accountId) {
        return accountRepositoryPort.findById(accountId)
            .switchIfEmpty(Mono.error(new AccountNotFoundException(
                "Account with ID " + accountId + " not found")))
            .flatMap(account -> accountRepositoryPort.deleteById(accountId));
    }

    @Override
    public Mono<AccountBalanceModel> getAccountBalance(String accountId) {
        return accountRepositoryPort.findById(accountId)
            .switchIfEmpty(Mono.error(new AccountNotFoundException(
                "Account with ID " + accountId + " not found")))
            .map(account -> AccountBalanceModel.builder()
                .accountId(account.getAccountId())
                .balance(account.getBalance())
                .lastUpdated(account.getUpdatedAt())
                .build());
    }

    private Mono<Void> validateAccountOwnershipRules(AccountModel request) {
        return customerClientPort.getCustomerType(request.getCustomerId())
            .flatMap(customerType ->
                accountRepositoryPort.countByCustomerIdAndAccountType(
                        request.getCustomerId(),
                        request.getAccountType().name())
                    .flatMap(count ->
                        validationService.validateAccountOwnership(request, count, customerType)
                    )
            );
    }

    private Mono<AccountModel> buildAndSaveAccount(AccountModel request) {
        return accountRepositoryPort.save(request);
    }

    @Override
    public Mono<Void> processDepositPending(String transactionId, String accountId, Double amount) {
        return accountRepositoryPort.findById(accountId)
            .flatMap(account -> {
                account.setBalance(account.getBalance() + amount);
                return accountRepositoryPort.update(accountId, account);
            })
            .flatMap(updatedAccount -> {
                DepositCompletedEvent event = DepositCompletedEvent.builder()
                    .transactionId(transactionId)
                    .accountId(accountId)
                    .amount(amount)
                    .timestamp(LocalDateTime.now())
                    .build();
                return kafkaProducer.sendDepositCompleted(event);
            });
    }
}
