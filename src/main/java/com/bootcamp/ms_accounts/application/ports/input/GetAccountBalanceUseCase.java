package com.bootcamp.ms_accounts.application.ports.input;

import com.bootcamp.ms_accounts.domain.model.dto.AccountBalanceModel;
import reactor.core.publisher.Mono;

public interface GetAccountBalanceUseCase {
    Mono<AccountBalanceModel> getAccountBalance(String accountId);
}
