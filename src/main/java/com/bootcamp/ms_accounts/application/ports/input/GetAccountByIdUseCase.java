package com.bootcamp.ms_accounts.application.ports.input;

import com.bootcamp.ms_accounts.domain.model.dto.AccountModel;
import reactor.core.publisher.Mono;

public interface GetAccountByIdUseCase {
    Mono<AccountModel> getAccountById(String accountId);
}
