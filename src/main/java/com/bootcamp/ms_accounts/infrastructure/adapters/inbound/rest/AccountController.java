package com.bootcamp.ms_accounts.infrastructure.adapters.inbound.rest;

import com.bootcamp.ms_accounts.domain.model.mapper.RequestToModelMapper;
import com.bootcamp.ms_accounts.application.ports.input.*;
import com.bootcamp.ms_accounts.infrastructure.adapters.inbound.rest.api.ApiApi;
import com.bootcamp.ms_accounts.infrastructure.adapters.inbound.rest.dto.*;
import com.bootcamp.ms_accounts.infrastructure.adapters.inbound.rest.mapper.AccountMapper;
import com.bootcamp.ms_accounts.infrastructure.adapters.inbound.rest.mapper.AccountBalanceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AccountController implements ApiApi {

    private final CreateAccountUseCase createAccountUseCase;
    private final GetAccountByIdUseCase getAccountByIdUseCase;
    private final GetAccountsByCustomerUseCase getAccountsByCustomerUseCase;
    private final GetAllAccountsUseCase getAllAccountsUseCase;
    private final UpdateAccountUseCase updateAccountUseCase;
    private final DeleteAccountUseCase deleteAccountUseCase;
    private final GetAccountBalanceUseCase getAccountBalanceUseCase;
    private final AccountMapper accountMapper;
    private final AccountBalanceMapper accountBalanceMapper;
    private final RequestToModelMapper requestToModelMapper;

    @Override
    public Mono<ResponseEntity<AccountResponse>> createAccount(
            Mono<CreateAccountRequest> createAccountRequest,
            ServerWebExchange exchange) {
        return createAccountRequest
            .map(requestToModelMapper::toAccountModel)
            .flatMap(createAccountUseCase::createAccount)
            .map(accountMapper::toRestDto)
            .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
            .onErrorResume(error -> {
                log.error("Error creating account: {}", error.getMessage());
                return Mono.just(ResponseEntity.badRequest().build());
            });
    }

    @Override
    public Mono<ResponseEntity<GetAllAccounts200Response>> getAllAccounts(
            Integer page,
            Integer size,
            ServerWebExchange exchange) {
        int pageNum = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : 20;

        return getAllAccountsUseCase.getAllAccounts(pageNum, pageSize)
            .collectList()
            .map(accounts -> {
                var response = new GetAllAccounts200Response();
                response.setContent(accounts.stream()
                    .map(accountMapper::toRestDto)
                    .toList());
                response.setTotalElements(accounts.size());
                response.setTotalPages(pageSize > 0 ? (accounts.size() + pageSize - 1) / pageSize : 1);
                response.setCurrentPage(pageNum);
                return ResponseEntity.ok(response);
            });
    }

    @Override
    public Mono<ResponseEntity<AccountResponse>> getAccountById(
            String accountId,
            ServerWebExchange exchange) {
        return getAccountByIdUseCase.getAccountById(accountId)
            .map(response -> ResponseEntity.ok(accountMapper.toRestDto(response)))
            .onErrorResume(error -> {
                log.error("Error retrieving account {}: {}", accountId, error.getMessage());
                return Mono.just(ResponseEntity.notFound().build());
            });
    }

    @Override
    public Mono<ResponseEntity<AccountResponse>> updateAccount(
            String accountId,
            Mono<UpdateAccountRequest> updateAccountRequest,
            ServerWebExchange exchange) {
        return updateAccountRequest
            .map(requestToModelMapper::toUpdateAccountModel)
            .flatMap(updateModel -> updateAccountUseCase.updateAccount(accountId, updateModel))
            .map(accountMapper::toRestDto)
            .map(ResponseEntity::ok)
            .onErrorResume(error -> {
                log.error("Error updating account {}: {}", accountId, error.getMessage());
                return Mono.just(ResponseEntity.badRequest().build());
            });
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteAccount(
            String accountId,
            ServerWebExchange exchange) {
        return deleteAccountUseCase.deleteAccount(accountId)
            .then(Mono.just(ResponseEntity.noContent().<Void>build()))
            .onErrorResume(error -> {
                log.error("Error deleting account {}: {}", accountId, error.getMessage());
                return Mono.just(ResponseEntity.notFound().build());
            });
    }

    @Override
    public Mono<ResponseEntity<Flux<AccountResponse>>> getAccountsByCustomer(
            String customerId,
            ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(
            getAccountsByCustomerUseCase.getAccountsByCustomer(customerId)
                .map(accountMapper::toRestDto)
        ));
    }

    @Override
    public Mono<ResponseEntity<AccountBalanceResponse>> getAccountBalance(
            String accountId,
            ServerWebExchange exchange) {
        return getAccountBalanceUseCase.getAccountBalance(accountId)
            .map(accountBalanceMapper::toRestDto)
            .map(ResponseEntity::ok)
            .onErrorResume(error -> {
                log.error("Error retrieving balance for account {}: {}", accountId, error.getMessage());
                return Mono.just(ResponseEntity.notFound().build());
            });
    }
}
