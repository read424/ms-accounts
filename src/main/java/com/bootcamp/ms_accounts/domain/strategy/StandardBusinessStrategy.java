package com.bootcamp.ms_accounts.domain.strategy;

import com.bootcamp.ms_accounts.domain.model.Account;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class StandardBusinessStrategy implements AccountOpeningStrategy {

    private static final Double BUSINESS_MAINTENANCE_FEE = 25.0;
    private static final Integer BUSINESS_MAX_MONTHLY_TRANSACTIONS = 200;

    @Override
    public Mono<Void> validateRequirements(Account account) {
        log.info("Validating Standard Business account requirements for accountId={}", account.getAccountId());
        return Mono.empty();
    }

    @Override
    public void applyProfileRules(Account account) {
        log.info("Applying Standard Business profile rules to accountId={}", account.getAccountId());
        account.setMaxMonthlyTransactionsNoFee(BUSINESS_MAX_MONTHLY_TRANSACTIONS);
        account.setMaintenanceFee(BUSINESS_MAINTENANCE_FEE);
    }

    @Override
    public Double getMinimumBalance() {
        return 0.0;
    }

    @Override
    public Integer getMaxMonthlyTransactionsNoFee() {
        return BUSINESS_MAX_MONTHLY_TRANSACTIONS;
    }

    @Override
    public Double getMaintenanceFee() {
        return BUSINESS_MAINTENANCE_FEE;
    }

    @Override
    public String getProfileType() {
        return "STANDARD";
    }
}
