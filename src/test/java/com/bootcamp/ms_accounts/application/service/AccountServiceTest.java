package com.bootcamp.ms_accounts.application.service;

import com.bootcamp.ms_accounts.application.ports.output.AccountRepositoryPort;
import com.bootcamp.ms_accounts.application.ports.output.CustomerClientPort;
import com.bootcamp.ms_accounts.domain.model.dto.*;
import com.bootcamp.ms_accounts.domain.model.enums.AccountStatusModel;
import com.bootcamp.ms_accounts.domain.model.enums.AccountTypeModel;
import com.bootcamp.ms_accounts.domain.model.exception.AccountNotFoundException;
import com.bootcamp.ms_accounts.domain.model.exception.BusinessAccountRestrictionException;
import com.bootcamp.ms_accounts.domain.model.exception.CustomerNotFoundException;
import com.bootcamp.ms_accounts.domain.model.mapper.RequestToModelMapper;
import com.bootcamp.ms_accounts.infrastructure.adapters.inbound.rest.dto.CreateAccountRequest;
import com.bootcamp.ms_accounts.infrastructure.adapters.inbound.rest.dto.UpdateAccountRequest;
import com.bootcamp.ms_accounts.infrastructure.adapters.inbound.rest.dto.AccountType;
import com.bootcamp.ms_accounts.infrastructure.adapters.inbound.rest.dto.AccountStatus;
import com.bootcamp.ms_accounts.infrastructure.adapters.outbound.messaging.KafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Account Service Tests")
class AccountServiceTest {

    @Mock
    private AccountRepositoryPort accountRepositoryPort;

    @Mock
    private CustomerClientPort customerClientPort;

    @Mock
    private RequestToModelMapper requestToModelMapper;

    @Mock
    private KafkaProducer kafkaProducer;

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(
            accountRepositoryPort,
            customerClientPort,
            requestToModelMapper,
            kafkaProducer
        );
    }

    @Nested
    @DisplayName("Create Account Operations")
    class CreateAccountOperations {

        @Test
        @DisplayName("Should create account when customer exists and rules are valid")
        void shouldCreateAccountSuccessfully() {
            // Given
            String customerId = "CUST_001";
            AccountModel accountModel = AccountModel.builder()
                .customerId(customerId)
                .accountType(AccountTypeModel.SAVINGS)
                .balance(1000.0)
                .status(AccountStatusModel.ACTIVE)
                .build();

            AccountModel savedAccount = AccountModel.builder()
                .accountId("ACC_001")
                .customerId(customerId)
                .accountType(AccountTypeModel.SAVINGS)
                .balance(1000.0)
                .status(AccountStatusModel.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

            when(customerClientPort.validateCustomerExists(customerId))
                .thenReturn(Mono.just(true));
            when(accountRepositoryPort.countByCustomerIdAndAccountType(customerId, "SAVINGS"))
                .thenReturn(Mono.just(0L));
            when(accountRepositoryPort.save(any(AccountModel.class)))
                .thenReturn(Mono.just(savedAccount));

            // When & Then
            StepVerifier.create(accountService.createAccount(accountModel))
                .expectNext(savedAccount)
                .verifyComplete();

            verify(accountRepositoryPort).save(any(AccountModel.class));
        }

        @Test
        @DisplayName("Should fail when customer doesn't exist")
        void shouldThrowCustomerNotFoundExceptionWhenCustomerDoesNotExist() {
            // Given
            String customerId = "INVALID_CUST";
            AccountModel accountModel = AccountModel.builder()
                .customerId(customerId)
                .accountType(AccountTypeModel.SAVINGS)
                .balance(1000.0)
                .build();

            when(customerClientPort.validateCustomerExists(customerId))
                .thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(accountService.createAccount(accountModel))
                .expectErrorMatches(throwable ->
                    throwable instanceof CustomerNotFoundException &&
                    throwable.getMessage().contains(customerId))
                .verify();
        }

        @Test
        @DisplayName("Should fail when personal customer tries second savings account")
        void shouldRejectSecondSavingsAccountForPersonal() {
            // Given
            String customerId = "PERSONAL_001";
            AccountModel accountModel = AccountModel.builder()
                .customerId(customerId)
                .accountType(AccountTypeModel.SAVINGS)
                .balance(1000.0)
                .build();

            when(customerClientPort.validateCustomerExists(customerId))
                .thenReturn(Mono.just(true));
            when(accountRepositoryPort.countByCustomerIdAndAccountType(customerId, "SAVINGS"))
                .thenReturn(Mono.just(1L)); // Already has one

            // When & Then
            StepVerifier.create(accountService.createAccount(accountModel))
                .expectErrorMatches(throwable ->
                    throwable instanceof BusinessAccountRestrictionException &&
                    throwable.getMessage().contains("SAVINGS"))
                .verify();
        }

        @Test
        @DisplayName("Should fail when business customer tries savings account")
        void shouldRejectSavingsAccountForBusiness() {
            // Given
            String customerId = "BUSINESS_001";
            AccountModel accountModel = AccountModel.builder()
                .customerId(customerId)
                .accountType(AccountTypeModel.SAVINGS)
                .balance(10000.0)
                .build();

            when(customerClientPort.validateCustomerExists(customerId))
                .thenReturn(Mono.just(true));

            // When & Then
            StepVerifier.create(accountService.createAccount(accountModel))
                .expectErrorMatches(throwable ->
                    throwable instanceof BusinessAccountRestrictionException)
                .verify();
        }

        @Test
        @DisplayName("Should fail when business customer tries fixed-term account")
        void shouldRejectFixedTermAccountForBusiness() {
            // Given
            String customerId = "BUSINESS_002";
            AccountModel accountModel = AccountModel.builder()
                .customerId(customerId)
                .accountType(AccountTypeModel.FIXED_TERM)
                .balance(20000.0)
                .restrictedOperationDay(15)
                .build();

            when(customerClientPort.validateCustomerExists(customerId))
                .thenReturn(Mono.just(true));

            // When & Then
            StepVerifier.create(accountService.createAccount(accountModel))
                .expectErrorMatches(throwable ->
                    throwable instanceof BusinessAccountRestrictionException)
                .verify();
        }
    }

    @Nested
    @DisplayName("Get Account Operations")
    class GetAccountOperations {

        @Test
        @DisplayName("Should get account by ID when exists")
        void shouldGetAccountByIdSuccessfully() {
            // Given
            String accountId = "ACC_002";
            AccountModel account = AccountModel.builder()
                .accountId(accountId)
                .customerId("CUST_002")
                .accountType(AccountTypeModel.SAVINGS)
                .balance(5000.0)
                .status(AccountStatusModel.ACTIVE)
                .build();

            when(accountRepositoryPort.findById(accountId))
                .thenReturn(Mono.just(account));

            // When & Then
            StepVerifier.create(accountService.getAccountById(accountId))
                .expectNext(account)
                .verifyComplete();
        }

        @Test
        @DisplayName("Should fail when account doesn't exist")
        void shouldThrowAccountNotFoundExceptionWhenAccountDoesNotExist() {
            // Given
            String accountId = "INVALID_ACC";

            when(accountRepositoryPort.findById(accountId))
                .thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(accountService.getAccountById(accountId))
                .expectErrorMatches(throwable ->
                    throwable instanceof AccountNotFoundException &&
                    throwable.getMessage().contains(accountId))
                .verify();
        }

        @Test
        @DisplayName("Should get accounts by customer")
        void shouldGetAccountsByCustomer() {
            // Given
            String customerId = "CUST_003";
            AccountModel acc1 = AccountModel.builder()
                .accountId("ACC_003")
                .customerId(customerId)
                .accountType(AccountTypeModel.SAVINGS)
                .balance(1000.0)
                .build();

            AccountModel acc2 = AccountModel.builder()
                .accountId("ACC_004")
                .customerId(customerId)
                .accountType(AccountTypeModel.CURRENT)
                .balance(2000.0)
                .build();

            when(accountRepositoryPort.findByCustomerId(customerId))
                .thenReturn(reactor.core.publisher.Flux.just(acc1, acc2));

            // When & Then
            StepVerifier.create(accountService.getAccountsByCustomer(customerId))
                .expectNext(acc1)
                .expectNext(acc2)
                .verifyComplete();
        }

        @Test
        @DisplayName("Should get all accounts with pagination")
        void shouldGetAllAccounts() {
            // Given
            AccountModel account = AccountModel.builder()
                .accountId("ACC_005")
                .customerId("CUST_004")
                .accountType(AccountTypeModel.SAVINGS)
                .balance(3000.0)
                .build();

            when(accountRepositoryPort.findAll(0, 20))
                .thenReturn(reactor.core.publisher.Flux.just(account));

            // When & Then
            StepVerifier.create(accountService.getAllAccounts(0, 20))
                .expectNext(account)
                .verifyComplete();
        }

        @Test
        @DisplayName("Should get account balance")
        void shouldGetAccountBalance() {
            // Given
            String accountId = "ACC_006";
            AccountModel account = AccountModel.builder()
                .accountId(accountId)
                .customerId("CUST_005")
                .balance(7500.0)
                .updatedAt(LocalDateTime.now())
                .build();

            when(accountRepositoryPort.findById(accountId))
                .thenReturn(Mono.just(account));

            // When & Then
            StepVerifier.create(accountService.getAccountBalance(accountId))
                .assertNext(response -> {
                    assert response.getAccountId().equals(accountId);
                    assert response.getBalance() == 7500.0;
                })
                .verifyComplete();
        }

        @Test
        @DisplayName("Should fail when getting balance of non-existent account")
        void shouldThrowErrorWhenGettingBalanceOfNonExistentAccount() {
            // Given
            String accountId = "INVALID_ACC";

            when(accountRepositoryPort.findById(accountId))
                .thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(accountService.getAccountBalance(accountId))
                .expectErrorMatches(throwable ->
                    throwable instanceof AccountNotFoundException)
                .verify();
        }
    }

    @Nested
    @DisplayName("Update Account Operations")
    class UpdateAccountOperations {

        @Test
        @DisplayName("Should update account status successfully")
        void shouldUpdateAccountStatusSuccessfully() {
            // Given
            String accountId = "ACC_007";
            AccountModel existingAccount = AccountModel.builder()
                .accountId(accountId)
                .customerId("CUST_006")
                .accountType(AccountTypeModel.SAVINGS)
                .balance(4000.0)
                .status(AccountStatusModel.ACTIVE)
                .updatedAt(LocalDateTime.now())
                .build();

            AccountModel updatedAccount = AccountModel.builder()
                .accountId(accountId)
                .customerId("CUST_006")
                .accountType(AccountTypeModel.SAVINGS)
                .balance(4000.0)
                .status(AccountStatusModel.SUSPENDED)
                .updatedAt(LocalDateTime.now())
                .build();

            UpdateAccountModel updateRequest = UpdateAccountModel.builder()
                .status(AccountStatusModel.SUSPENDED)
                .build();

            when(accountRepositoryPort.findById(accountId))
                .thenReturn(Mono.just(existingAccount));
            when(accountRepositoryPort.update(accountId, existingAccount))
                .thenReturn(Mono.just(updatedAccount));

            // When & Then
            StepVerifier.create(accountService.updateAccount(accountId, updateRequest))
                .expectNext(updatedAccount)
                .verifyComplete();
        }

        @Test
        @DisplayName("Should fail when updating non-existent account")
        void shouldThrowErrorWhenUpdatingNonExistentAccount() {
            // Given
            String accountId = "INVALID_ACC";
            UpdateAccountModel updateRequest = UpdateAccountModel.builder()
                .status(AccountStatusModel.CLOSED)
                .build();

            when(accountRepositoryPort.findById(accountId))
                .thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(accountService.updateAccount(accountId, updateRequest))
                .expectErrorMatches(throwable ->
                    throwable instanceof AccountNotFoundException)
                .verify();
        }
    }

    @Nested
    @DisplayName("Delete Account Operations")
    class DeleteAccountOperations {

        @Test
        @DisplayName("Should delete account successfully")
        void shouldDeleteAccountSuccessfully() {
            // Given
            String accountId = "ACC_008";
            AccountModel account = AccountModel.builder()
                .accountId(accountId)
                .customerId("CUST_007")
                .accountType(AccountTypeModel.SAVINGS)
                .balance(2000.0)
                .build();

            when(accountRepositoryPort.findById(accountId))
                .thenReturn(Mono.just(account));
            when(accountRepositoryPort.deleteById(accountId))
                .thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(accountService.deleteAccount(accountId))
                .verifyComplete();

            verify(accountRepositoryPort).deleteById(accountId);
        }

        @Test
        @DisplayName("Should fail when deleting non-existent account")
        void shouldThrowErrorWhenDeletingNonExistentAccount() {
            // Given
            String accountId = "INVALID_ACC";

            when(accountRepositoryPort.findById(accountId))
                .thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(accountService.deleteAccount(accountId))
                .expectErrorMatches(throwable ->
                    throwable instanceof AccountNotFoundException)
                .verify();
        }
    }
}
