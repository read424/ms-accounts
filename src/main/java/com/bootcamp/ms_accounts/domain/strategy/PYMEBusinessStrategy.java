package com.bootcamp.ms_accounts.domain.strategy;

import com.bootcamp.ms_accounts.domain.model.Account;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class PYMEBusinessStrategy implements AccountOpeningStrategy {

    private static final Double PYME_MAINTENANCE_FEE = 0.0;
    private static final Integer PYME_MAX_MONTHLY_TRANSACTIONS = 300;

    @Override
    public Mono<Void> validateRequirements(Account account) {
        log.info("Validating PYME Business account requirements for accountId={}", account.getAccountId());

        if (!account.getAccountType().equals("CHECKING")) {
            log.error("PYME account must be CHECKING type, got {}", account.getAccountType());
            return Mono.error(new IllegalArgumentException(
                "PYME account must be CHECKING type"));
        }

        log.info("PYME requirements validated for accountId={}", account.getAccountId());
        return Mono.empty();
    }

    @Override
    public void applyProfileRules(Account account) {
        log.info("Applying PYME Business profile rules to accountId={}", account.getAccountId());
        account.setMaxMonthlyTransactionsNoFee(PYME_MAX_MONTHLY_TRANSACTIONS);
        account.setMaintenanceFee(PYME_MAINTENANCE_FEE);
    }

    @Override
    public Double getMinimumBalance() {
        return 0.0;
    }

    @Override
    public Integer getMaxMonthlyTransactionsNoFee() {
        return PYME_MAX_MONTHLY_TRANSACTIONS;
    }

    @Override
    public Double getMaintenanceFee() {
        return PYME_MAINTENANCE_FEE;
    }

    @Override
    public String getProfileType() {
        return "PYME";
    }
}
