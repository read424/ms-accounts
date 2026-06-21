package com.bootcamp.ms_accounts.application.ports.input;

import com.bootcamp.ms_accounts.domain.model.dto.AccountModel;
import reactor.core.publisher.Flux;

public interface GetAccountsByCustomerUseCase {
    Flux<AccountModel> getAccountsByCustomer(String customerId);
}
