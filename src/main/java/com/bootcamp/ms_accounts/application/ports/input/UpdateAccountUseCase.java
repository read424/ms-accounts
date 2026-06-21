package com.bootcamp.ms_accounts.application.ports.input;

import com.bootcamp.ms_accounts.domain.model.dto.AccountModel;
import com.bootcamp.ms_accounts.domain.model.dto.UpdateAccountModel;
import reactor.core.publisher.Mono;

public interface UpdateAccountUseCase {
    Mono<AccountModel> updateAccount(String accountId, UpdateAccountModel request);
}
