package com.bootcamp.ms_accounts.domain.strategy;

import com.bootcamp.ms_accounts.domain.model.Account;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class StandardPersonalStrategy implements AccountOpeningStrategy {

    @Override
    public Mono<Void> validateRequirements(Account account) {
        log.info("Validating Standard Personal account requirements for accountId={}", account.getAccountId());
        return Mono.empty();
    }

    @Override
    public void applyProfileRules(Account account) {
        log.info("Applying Standard Personal profile rules to accountId={}", account.getAccountId());
        account.setMaxMonthlyTransactionsNoFee(6);
        account.setMaintenanceFee(0.0);
    }

    @Override
    public Double getMinimumBalance() {
        return 0.0;
    }

    @Override
    public Integer getMaxMonthlyTransactionsNoFee() {
        return 6;
    }

    @Override
    public Double getMaintenanceFee() {
        return 0.0;
    }

    @Override
    public String getProfileType() {
        return "STANDARD";
    }
}
