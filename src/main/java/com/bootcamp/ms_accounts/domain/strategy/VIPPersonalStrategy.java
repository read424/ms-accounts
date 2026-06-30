package com.bootcamp.ms_accounts.domain.strategy;

import com.bootcamp.ms_accounts.domain.model.Account;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class VIPPersonalStrategy implements AccountOpeningStrategy {

    private static final Double VIP_MINIMUM_DAILY_BALANCE = 10000.0;
    private static final Integer VIP_MAX_MONTHLY_TRANSACTIONS = 50;
    private static final Double VIP_MAINTENANCE_FEE = 0.0;

    @Override
    public Mono<Void> validateRequirements(Account account) {
        log.info("Validating VIP Personal account requirements for accountId={}", account.getAccountId());

        if (account.getBalance() < VIP_MINIMUM_DAILY_BALANCE) {
            log.error("VIP account requires minimum balance of {}, got {}",
                VIP_MINIMUM_DAILY_BALANCE, account.getBalance());
            return Mono.error(new IllegalArgumentException(
                "VIP account requires minimum balance of " + VIP_MINIMUM_DAILY_BALANCE));
        }

        log.info("VIP requirements validated for accountId={}", account.getAccountId());
        return Mono.empty();
    }

    @Override
    public void applyProfileRules(Account account) {
        log.info("Applying VIP Personal profile rules to accountId={}", account.getAccountId());
        account.setMaxMonthlyTransactionsNoFee(VIP_MAX_MONTHLY_TRANSACTIONS);
        account.setMaintenanceFee(VIP_MAINTENANCE_FEE);
        account.setMinimumDailyBalance(VIP_MINIMUM_DAILY_BALANCE);
    }

    @Override
    public Double getMinimumBalance() {
        return VIP_MINIMUM_DAILY_BALANCE;
    }

    @Override
    public Integer getMaxMonthlyTransactionsNoFee() {
        return VIP_MAX_MONTHLY_TRANSACTIONS;
    }

    @Override
    public Double getMaintenanceFee() {
        return VIP_MAINTENANCE_FEE;
    }

    @Override
    public String getProfileType() {
        return "VIP";
    }
}
