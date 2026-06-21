package com.bootcamp.ms_accounts.application.ports.input;

import com.bootcamp.ms_accounts.domain.model.dto.AccountModel;
import reactor.core.publisher.Flux;

public interface GetAllAccountsUseCase {
    Flux<AccountModel> getAllAccounts(int page, int size);
}
