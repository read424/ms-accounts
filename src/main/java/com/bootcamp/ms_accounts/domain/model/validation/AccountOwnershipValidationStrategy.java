package com.bootcamp.ms_accounts.domain.model.validation;

import com.bootcamp.ms_accounts.domain.model.dto.AccountModel;
import reactor.core.publisher.Mono;

public interface AccountOwnershipValidationStrategy {
    Mono<Void> validate(AccountModel accountModel, Long existingAccountCount);
}
