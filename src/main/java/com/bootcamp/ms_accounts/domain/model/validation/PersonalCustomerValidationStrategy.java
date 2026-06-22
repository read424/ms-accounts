package com.bootcamp.ms_accounts.domain.model.validation;

import com.bootcamp.ms_accounts.domain.model.dto.AccountModel;
import com.bootcamp.ms_accounts.domain.model.enums.AccountTypeModel;
import com.bootcamp.ms_accounts.domain.model.exception.BusinessAccountRestrictionException;
import reactor.core.publisher.Mono;

public class PersonalCustomerValidationStrategy implements AccountOwnershipValidationStrategy {

    @Override
    public Mono<Void> validate(AccountModel accountModel, Long existingAccountCount) {
        AccountTypeModel accountType = accountModel.getAccountType();

        if (existingAccountCount > 0 && (accountType == AccountTypeModel.SAVINGS ||
                accountType == AccountTypeModel.CURRENT || accountType == AccountTypeModel.FIXED_TERM)) {
            return Mono.error(new BusinessAccountRestrictionException(
                "Personal customer can only own one " + accountType + " account"));
        }

        return Mono.empty();
    }
}
