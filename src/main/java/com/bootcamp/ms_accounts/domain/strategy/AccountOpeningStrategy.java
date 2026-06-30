package com.bootcamp.ms_accounts.domain.strategy;

import com.bootcamp.ms_accounts.domain.model.Account;
import reactor.core.publisher.Mono;

public interface AccountOpeningStrategy {

    Mono<Void> validateRequirements(Account account);

    void applyProfileRules(Account account);

    Double getMinimumBalance();

    Integer getMaxMonthlyTransactionsNoFee();

    Double getMaintenanceFee();

    String getProfileType();
}
