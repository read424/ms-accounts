package com.bootcamp.ms_accounts.domain.service;

import com.bootcamp.ms_accounts.application.ports.output.AccountRepositoryPort;
import com.bootcamp.ms_accounts.application.ports.output.CustomerClientPort;
import com.bootcamp.ms_accounts.domain.model.dto.*;
import com.bootcamp.ms_accounts.domain.model.enums.AccountStatusModel;
import com.bootcamp.ms_accounts.domain.model.enums.AccountTypeModel;
import com.bootcamp.ms_accounts.domain.model.enums.DocumentType;
import com.bootcamp.ms_accounts.domain.model.exception.AccountNotFoundException;
import com.bootcamp.ms_accounts.domain.model.exception.BusinessAccountRestrictionException;
import com.bootcamp.ms_accounts.domain.model.exception.CustomerNotFoundException;
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
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
/*
@ExtendWith(MockitoExtension.class)
@DisplayName("Account Domain Service - Business Rules Tests")
class AccountDomainServiceTest {

    @Mock
    private AccountRepositoryPort accountRepositoryPort;

    @Mock
    private CustomerClientPort customerClientPort;

    @Mock
    private com.bootcamp.ms_accounts.infrastructure.adapters.outbound.mapper.AccountMapper accountMapper;

    private AccountDomainService accountDomainService;

    @BeforeEach
    void setUp() {
        org.mockito.Mockito.lenient()
            .when(accountMapper.toDto(org.mockito.ArgumentMatchers.any()))
            .thenAnswer(invocation -> {
                com.bootcamp.ms_accounts.infrastructure.adapters.outbound.persistence.entity.AccountEntity entity =
                    invocation.getArgument(0);
                return AccountResponse.builder()
                    .accountId(entity.getAccountId())
                    .customerId(entity.getCustomerId())
                    .accountType(entity.getAccountType())
                    .balance(entity.getBalance())
                    .maintenanceFee(entity.getMaintenanceFee())
                    .monthlyTransactionLimit(entity.getMonthlyTransactionLimit())
                    .monthlyTransactionCounter(entity.getMonthlyTransactionCounter())
                    .restrictedOperationDay(entity.getRestrictedOperationDay())
                    .status(entity.getStatus())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .build();
            });

        accountDomainService = new AccountDomainService(accountRepositoryPort, customerClientPort, accountMapper);
    }

    @Nested
    @DisplayName("BR-001: Customer debe existir")
    class BusinessRule001_CustomerMustExist {

        @Test
        @DisplayName("Cuando se intenta crear cuenta para cliente inexistente, debe lanzar CustomerNotFoundException")
        void shouldThrowCustomerNotFoundExceptionWhenCustomerDoesNotExist() {
            // Given
            CreateAccountRequest request = CreateAccountRequest.builder()
                .customerId("NON_EXISTENT_CUSTOMER")
                .accountType(AccountTypeModel.SAVINGS)
                .initialBalance(1000.0)
                .build();

            when(customerClientPort.validateCustomerExists("NON_EXISTENT_CUSTOMER"))
                .thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(accountDomainService.createAccount(request))
                .expectErrorMatches(throwable -> throwable instanceof CustomerNotFoundException &&
                    throwable.getMessage().contains("NON_EXISTENT_CUSTOMER"))
                .verify();
        }

        @Test
        @DisplayName("Cuando cliente existe, debe permitir crear la cuenta")
        void shouldCreateAccountWhenCustomerExists() {
            // Given
            String customerId = "VALID_CUSTOMER";
            CreateAccountRequest request = CreateAccountRequest.builder()
                .customerId(customerId)
                .accountType(AccountTypeModel.SAVINGS)
                .initialBalance(1000.0)
                .build();

            AccountResponse savedAccount = AccountResponse.builder()
                .accountId("ACC-001")
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
            when(accountRepositoryPort.save(any(AccountResponse.class)))
                .thenReturn(Mono.just(savedAccount));

            // When & Then
            StepVerifier.create(accountDomainService.createAccount(request))
                .expectNext(savedAccount)
                .verifyComplete();

            verify(accountRepositoryPort).save(any(AccountResponse.class));
        }
    }

    @Nested
    @DisplayName("BR-002: Cliente Personal - Límites de cuentas")
    class BusinessRule002_PersonalCustomerLimits {

        private String personalCustomerId = "PERSONAL_001";

        @Test
        @DisplayName("Cliente personal NO PUEDE tener 2 cuentas de ahorro")
        void shouldNotAllowSecondSavingsAccountForPersonal() {
            // Given
            CreateAccountRequest request = CreateAccountRequest.builder()
                .customerId(personalCustomerId)
                .accountType(AccountTypeModel.SAVINGS)
                .initialBalance(1000.0)
                .build();

            when(customerClientPort.validateCustomerExists(personalCustomerId))
                .thenReturn(Mono.just(true));
            when(accountRepositoryPort.countByCustomerIdAndAccountType(personalCustomerId, "SAVINGS"))
                .thenReturn(Mono.just(1L)); // Ya tiene 1 cuenta de ahorro

            // When & Then
            StepVerifier.create(accountDomainService.createAccount(request))
                .expectErrorMatches(throwable -> throwable instanceof BusinessAccountRestrictionException &&
                    throwable.getMessage().contains("SAVINGS"))
                .verify();
        }

        @Test
        @DisplayName("Cliente personal NO PUEDE tener 2 cuentas corrientes")
        void shouldNotAllowSecondCurrentAccountForPersonal() {
            // Given
            CreateAccountRequest request = CreateAccountRequest.builder()
                .customerId(personalCustomerId)
                .accountType(AccountTypeModel.CURRENT)
                .initialBalance(1000.0)
                .maintenanceFee(5.0)
                .build();

            when(customerClientPort.validateCustomerExists(personalCustomerId))
                .thenReturn(Mono.just(true));
            when(accountRepositoryPort.countByCustomerIdAndAccountType(personalCustomerId, "CURRENT"))
                .thenReturn(Mono.just(1L)); // Ya tiene 1 cuenta corriente

            // When & Then
            StepVerifier.create(accountDomainService.createAccount(request))
                .expectErrorMatches(throwable -> throwable instanceof BusinessAccountRestrictionException &&
                    throwable.getMessage().contains("CURRENT"))
                .verify();
        }

        @Test
        @DisplayName("Cliente personal PUEDE tener múltiples cuentas de plazo fijo")
        void shouldAllowMultipleFixedTermAccountsForPersonal() {
            // Given
            String customerId = "PERSONAL_002";
            CreateAccountRequest request = CreateAccountRequest.builder()
                .customerId(customerId)
                .accountType(AccountTypeModel.FIXED_TERM)
                .initialBalance(5000.0)
                .restrictedOperationDay(15)
                .build();

            AccountResponse savedAccount = AccountResponse.builder()
                .accountId("ACC-002")
                .customerId(customerId)
                .accountType(AccountTypeModel.FIXED_TERM)
                .balance(5000.0)
                .restrictedOperationDay(15)
                .status(AccountStatusModel.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

            when(customerClientPort.validateCustomerExists(customerId))
                .thenReturn(Mono.just(true));
            when(accountRepositoryPort.countByCustomerIdAndAccountType(customerId, "FIXED_TERM"))
                .thenReturn(Mono.just(1L)); // Ya tiene 1, pero puede tener más
            when(accountRepositoryPort.save(any(AccountResponse.class)))
                .thenReturn(Mono.just(savedAccount));

            // When & Then
            StepVerifier.create(accountDomainService.createAccount(request))
                .expectNext(savedAccount)
                .verifyComplete();
        }
    }

    @Nested
    @DisplayName("BR-003: Cliente Empresarial - Restricciones")
    class BusinessRule003_BusinessCustomerRestrictions {

        private String businessCustomerId = "BUSINESS_001";

        @Test
        @DisplayName("Cliente empresarial NO PUEDE tener cuenta de ahorro")
        void shouldNotAllowSavingsAccountForBusiness() {
            // Given
            CreateAccountRequest request = CreateAccountRequest.builder()
                .customerId(businessCustomerId)
                .accountType(AccountTypeModel.SAVINGS)
                .initialBalance(10000.0)
                .build();

            when(customerClientPort.validateCustomerExists(businessCustomerId))
                .thenReturn(Mono.just(true));

            // When & Then
            StepVerifier.create(accountDomainService.createAccount(request))
                .expectErrorMatches(throwable -> throwable instanceof BusinessAccountRestrictionException)
                .verify();
        }

        @Test
        @DisplayName("Cliente empresarial NO PUEDE tener cuenta de plazo fijo")
        void shouldNotAllowFixedTermAccountForBusiness() {
            // Given
            CreateAccountRequest request = CreateAccountRequest.builder()
                .customerId(businessCustomerId)
                .accountType(AccountTypeModel.FIXED_TERM)
                .initialBalance(20000.0)
                .restrictedOperationDay(1)
                .build();

            when(customerClientPort.validateCustomerExists(businessCustomerId))
                .thenReturn(Mono.just(true));

            // When & Then
            StepVerifier.create(accountDomainService.createAccount(request))
                .expectErrorMatches(throwable -> throwable instanceof BusinessAccountRestrictionException)
                .verify();
        }

        @Test
        @DisplayName("Cliente empresarial PUEDE tener múltiples cuentas corrientes")
        void shouldAllowMultipleCurrentAccountsForBusiness() {
            // Given
            CreateAccountRequest request = CreateAccountRequest.builder()
                .customerId(businessCustomerId)
                .accountType(AccountTypeModel.CURRENT)
                .initialBalance(50000.0)
                .maintenanceFee(15.0)
                .build();

            AccountResponse savedAccount = AccountResponse.builder()
                .accountId("ACC-003")
                .customerId(businessCustomerId)
                .accountType(AccountTypeModel.CURRENT)
                .balance(50000.0)
                .maintenanceFee(15.0)
                .status(AccountStatusModel.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

            when(customerClientPort.validateCustomerExists(businessCustomerId))
                .thenReturn(Mono.just(true));
            when(accountRepositoryPort.countByCustomerIdAndAccountType(businessCustomerId, "CURRENT"))
                .thenReturn(Mono.just(0L)); // Primera cuenta corriente
            when(accountRepositoryPort.save(any(AccountResponse.class)))
                .thenReturn(Mono.just(savedAccount));

            // When & Then
            StepVerifier.create(accountDomainService.createAccount(request))
                .expectNext(savedAccount)
                .verifyComplete();
        }
    }

    @Nested
    @DisplayName("BR-005 & BR-006: Comisiones por tipo de cuenta")
    class BusinessRule005_006_MaintenanceFees {

        @Test
        @DisplayName("BR-005: Cuenta de ahorro NO debe tener comisión de mantenimiento")
        void savingsAccountShouldHaveZeroMaintenanceFee() {
            // Given
            CreateAccountRequest request = CreateAccountRequest.builder()
                .customerId("PERSONAL_003")
                .accountType(AccountTypeModel.SAVINGS)
                .initialBalance(2000.0)
                .build();

            AccountResponse expectedAccount = AccountResponse.builder()
                .accountId("ACC-004")
                .customerId("PERSONAL_003")
                .accountType(AccountTypeModel.SAVINGS)
                .balance(2000.0)
                .maintenanceFee(0.0)
                .status(AccountStatusModel.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

            when(customerClientPort.validateCustomerExists("PERSONAL_003"))
                .thenReturn(Mono.just(true));
            when(accountRepositoryPort.countByCustomerIdAndAccountType("PERSONAL_003", "SAVINGS"))
                .thenReturn(Mono.just(0L));
            when(accountRepositoryPort.save(any(AccountResponse.class)))
                .thenReturn(Mono.just(expectedAccount));

            // When & Then
            StepVerifier.create(accountDomainService.createAccount(request))
                .assertNext(account -> {
                    assert account.getMaintenanceFee() == 0.0 : "Savings account should have 0 maintenance fee";
                })
                .verifyComplete();
        }

        @Test
        @DisplayName("BR-006: Cuenta corriente DEBE tener comisión de mantenimiento")
        void currentAccountMustHaveMaintenanceFee() {
            // Given
            CreateAccountRequest request = CreateAccountRequest.builder()
                .customerId("PERSONAL_004")
                .accountType(AccountTypeModel.CURRENT)
                .initialBalance(3000.0)
                .maintenanceFee(8.5)
                .build();

            AccountResponse expectedAccount = AccountResponse.builder()
                .accountId("ACC-005")
                .customerId("PERSONAL_004")
                .accountType(AccountTypeModel.CURRENT)
                .balance(3000.0)
                .maintenanceFee(8.5)
                .status(AccountStatusModel.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

            when(customerClientPort.validateCustomerExists("PERSONAL_004"))
                .thenReturn(Mono.just(true));
            when(accountRepositoryPort.countByCustomerIdAndAccountType("PERSONAL_004", "CURRENT"))
                .thenReturn(Mono.just(0L));
            when(accountRepositoryPort.save(any(AccountResponse.class)))
                .thenReturn(Mono.just(expectedAccount));

            // When & Then
            StepVerifier.create(accountDomainService.createAccount(request))
                .assertNext(account -> {
                    assert account.getMaintenanceFee() == 8.5 : "Current account must have maintenance fee";
                })
                .verifyComplete();
        }
    }

    @Nested
    @DisplayName("BR-007: Cuenta de plazo fijo - Restricciones operacionales")
    class BusinessRule007_FixedTermRestrictions {

        @Test
        @DisplayName("BR-007: Cuenta de plazo fijo debe tener día restringido de operaciones")
        void fixedTermAccountMustHaveRestrictedOperationDay() {
            // Given
            CreateAccountRequest request = CreateAccountRequest.builder()
                .customerId("PERSONAL_005")
                .accountType(AccountTypeModel.FIXED_TERM)
                .initialBalance(10000.0)
                .restrictedOperationDay(20)
                .build();

            AccountResponse expectedAccount = AccountResponse.builder()
                .accountId("ACC-006")
                .customerId("PERSONAL_005")
                .accountType(AccountTypeModel.FIXED_TERM)
                .balance(10000.0)
                .restrictedOperationDay(20)
                .status(AccountStatusModel.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

            when(customerClientPort.validateCustomerExists("PERSONAL_005"))
                .thenReturn(Mono.just(true));
            when(accountRepositoryPort.countByCustomerIdAndAccountType("PERSONAL_005", "FIXED_TERM"))
                .thenReturn(Mono.just(0L));
            when(accountRepositoryPort.save(any(AccountResponse.class)))
                .thenReturn(Mono.just(expectedAccount));

            // When & Then
            StepVerifier.create(accountDomainService.createAccount(request))
                .assertNext(account -> {
                    assert account.getRestrictedOperationDay() == 20 : 
                        "Fixed term account must have restricted operation day";
                })
                .verifyComplete();
        }

        @Test
        @DisplayName("BR-007: Cuenta de plazo fijo NO debe tener comisión de mantenimiento")
        void fixedTermAccountShouldHaveZeroMaintenanceFee() {
            // Given
            CreateAccountRequest request = CreateAccountRequest.builder()
                .customerId("PERSONAL_006")
                .accountType(AccountTypeModel.FIXED_TERM)
                .initialBalance(15000.0)
                .restrictedOperationDay(10)
                .build();

            AccountResponse expectedAccount = AccountResponse.builder()
                .accountId("ACC-007")
                .customerId("PERSONAL_006")
                .accountType(AccountTypeModel.FIXED_TERM)
                .balance(15000.0)
                .maintenanceFee(0.0)
                .restrictedOperationDay(10)
                .status(AccountStatusModel.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

            when(customerClientPort.validateCustomerExists("PERSONAL_006"))
                .thenReturn(Mono.just(true));
            when(accountRepositoryPort.countByCustomerIdAndAccountType("PERSONAL_006", "FIXED_TERM"))
                .thenReturn(Mono.just(0L));
            when(accountRepositoryPort.save(any(AccountResponse.class)))
                .thenReturn(Mono.just(expectedAccount));

            // When & Then
            StepVerifier.create(accountDomainService.createAccount(request))
                .assertNext(account -> {
                    assert account.getMaintenanceFee() == 0.0 : 
                        "Fixed term account should have 0 maintenance fee";
                })
                .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Get Account Operations")
    class GetAccountOperations {

        @Test
        @DisplayName("Obtener cuenta por ID - Cuenta existe")
        void shouldGetAccountWhenExists() {
            // Given
            String accountId = "ACC-008";
            AccountResponse account = AccountResponse.builder()
                .accountId(accountId)
                .customerId("CUSTOMER_001")
                .accountType(AccountTypeModel.SAVINGS)
                .balance(5000.0)
                .status(AccountStatusModel.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

            when(accountRepositoryPort.findById(accountId))
                .thenReturn(Mono.just(account));

            // When & Then
            StepVerifier.create(accountDomainService.getAccountById(accountId))
                .expectNext(account)
                .verifyComplete();
        }

        @Test
        @DisplayName("Obtener cuenta por ID - Cuenta NO existe")
        void shouldThrowErrorWhenAccountNotFound() {
            // Given
            String accountId = "NON_EXISTENT";

            when(accountRepositoryPort.findById(accountId))
                .thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(accountDomainService.getAccountById(accountId))
                .expectErrorMatches(throwable -> throwable instanceof AccountNotFoundException &&
                    throwable.getMessage().contains(accountId))
                .verify();
        }

        @Test
        @DisplayName("Obtener cuentas por cliente")
        void shouldGetAccountsByCustomer() {
            // Given
            String customerId = "CUSTOMER_002";
            AccountResponse acc1 = AccountResponse.builder()
                .accountId("ACC-009")
                .customerId(customerId)
                .accountType(AccountTypeModel.SAVINGS)
                .balance(1000.0)
                .build();

            AccountResponse acc2 = AccountResponse.builder()
                .accountId("ACC-010")
                .customerId(customerId)
                .accountType(AccountTypeModel.CURRENT)
                .balance(2000.0)
                .build();

            when(accountRepositoryPort.findByCustomerId(customerId))
                .thenReturn(reactor.core.publisher.Flux.just(acc1, acc2));

            // When & Then
            StepVerifier.create(accountDomainService.getAccountsByCustomer(customerId))
                .expectNext(acc1)
                .expectNext(acc2)
                .verifyComplete();
        }

        @Test
        @DisplayName("Obtener todas las cuentas con paginación")
        void shouldGetAllAccountsWithPagination() {
            // Given
            AccountResponse account = AccountResponse.builder()
                .accountId("ACC-011")
                .customerId("CUSTOMER_003")
                .accountType(AccountTypeModel.SAVINGS)
                .balance(3000.0)
                .build();

            when(accountRepositoryPort.findAll(0, 20))
                .thenReturn(reactor.core.publisher.Flux.just(account));

            // When & Then
            StepVerifier.create(accountDomainService.getAllAccounts(0, 20))
                .expectNext(account)
                .verifyComplete();
        }

        @Test
        @DisplayName("Obtener balance de cuenta")
        void shouldGetAccountBalance() {
            // Given
            String accountId = "ACC-012";
            AccountResponse account = AccountResponse.builder()
                .accountId(accountId)
                .customerId("CUSTOMER_004")
                .balance(7500.0)
                .build();

            when(accountRepositoryPort.findById(accountId))
                .thenReturn(Mono.just(account));

            // When & Then
            StepVerifier.create(accountDomainService.getAccountBalance(accountId))
                .assertNext(response -> {
                    assert response.getAccountId().equals(accountId);
                    assert response.getBalance() == 7500.0;
                })
                .verifyComplete();
        }

        @Test
        @DisplayName("Obtener balance - Cuenta NO existe")
        void shouldThrowErrorWhenGettingBalanceOfNonExistentAccount() {
            // Given
            String accountId = "NON_EXISTENT";

            when(accountRepositoryPort.findById(accountId))
                .thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(accountDomainService.getAccountBalance(accountId))
                .expectErrorMatches(throwable -> throwable instanceof AccountNotFoundException)
                .verify();
        }
    }

    @Nested
    @DisplayName("Update Account Operations")
    class UpdateAccountOperations {

        @Test
        @DisplayName("Actualizar estado de cuenta exitosamente")
        void shouldUpdateAccountStatusSuccessfully() {
            // Given
            String accountId = "ACC-013";
            UpdateAccountRequest updateRequest = UpdateAccountRequest.builder()
                .status(AccountStatusModel.SUSPENDED)
                .build();

            AccountResponse existingAccount = AccountResponse.builder()
                .accountId(accountId)
                .customerId("CUSTOMER_005")
                .accountType(AccountTypeModel.SAVINGS)
                .balance(4000.0)
                .status(AccountStatusModel.ACTIVE)
                .updatedAt(LocalDateTime.now())
                .build();

            AccountResponse updatedAccount = AccountResponse.builder()
                .accountId(accountId)
                .customerId("CUSTOMER_005")
                .accountType(AccountTypeModel.SAVINGS)
                .balance(4000.0)
                .status(AccountStatusModel.SUSPENDED)
                .updatedAt(LocalDateTime.now())
                .build();

            when(accountRepositoryPort.findById(accountId))
                .thenReturn(Mono.just(existingAccount));
            when(accountRepositoryPort.save(any(AccountResponse.class)))
                .thenReturn(Mono.just(updatedAccount));

            // When & Then
            StepVerifier.create(accountDomainService.updateAccount(accountId, updateRequest))
                .assertNext(account -> {
                    assert account.getStatus() == AccountStatusModel.SUSPENDED : 
                        "Account status should be SUSPENDED";
                })
                .verifyComplete();
        }

        @Test
        @DisplayName("Actualizar account holders")
        void shouldUpdateAccountHolders() {
            // Given
            String accountId = "ACC-014";
            AccountHolder newHolder = AccountHolder.builder()
                .customerId("CUSTOMER_006")
                .build();

            UpdateAccountRequest updateRequest = UpdateAccountRequest.builder()
                .accountHolders(Arrays.asList(newHolder))
                .build();

            AccountResponse existingAccount = AccountResponse.builder()
                .accountId(accountId)
                .customerId("CUSTOMER_005")
                .accountType(AccountTypeModel.CURRENT)
                .balance(5000.0)
                .build();

            when(accountRepositoryPort.findById(accountId))
                .thenReturn(Mono.just(existingAccount));
            when(accountRepositoryPort.save(any(AccountResponse.class)))
                .thenReturn(Mono.just(existingAccount));

            // When & Then
            StepVerifier.create(accountDomainService.updateAccount(accountId, updateRequest))
                .expectNext(existingAccount)
                .verifyComplete();
        }

        @Test
        @DisplayName("Actualizar cuenta - Cuenta NO existe")
        void shouldThrowErrorWhenUpdatingNonExistentAccount() {
            // Given
            String accountId = "NON_EXISTENT";
            UpdateAccountRequest updateRequest = UpdateAccountRequest.builder()
                .status(AccountStatusModel.CLOSED)
                .build();

            when(accountRepositoryPort.findById(accountId))
                .thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(accountDomainService.updateAccount(accountId, updateRequest))
                .expectErrorMatches(throwable -> throwable instanceof AccountNotFoundException)
                .verify();
        }
    }

    @Nested
    @DisplayName("Delete Account Operations")
    class DeleteAccountOperations {

        @Test
        @DisplayName("Eliminar cuenta exitosamente")
        void shouldDeleteAccountSuccessfully() {
            // Given
            String accountId = "ACC-015";
            AccountResponse account = AccountResponse.builder()
                .accountId(accountId)
                .customerId("CUSTOMER_007")
                .accountType(AccountTypeModel.SAVINGS)
                .balance(2000.0)
                .build();

            when(accountRepositoryPort.findById(accountId))
                .thenReturn(Mono.just(account));
            when(accountRepositoryPort.deleteById(accountId))
                .thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(accountDomainService.deleteAccount(accountId))
                .verifyComplete();

            verify(accountRepositoryPort).deleteById(accountId);
        }

        @Test
        @DisplayName("Eliminar cuenta - Cuenta NO existe")
        void shouldThrowErrorWhenDeletingNonExistentAccount() {
            // Given
            String accountId = "NON_EXISTENT";

            when(accountRepositoryPort.findById(accountId))
                .thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(accountDomainService.deleteAccount(accountId))
                .expectErrorMatches(throwable -> throwable instanceof AccountNotFoundException)
                .verify();
        }
    }
}
*/