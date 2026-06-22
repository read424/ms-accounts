package com.bootcamp.ms_accounts.domain.model.validation;

import com.bootcamp.ms_accounts.domain.model.dto.AccountModel;
import com.bootcamp.ms_accounts.domain.model.enums.CustomerType;
import reactor.core.publisher.Mono;

public class AccountOwnershipValidationService {

    public Mono<Void> validateAccountOwnership(
            AccountModel accountModel,
            Long existingAccountCount,
            CustomerType customerType) {

        AccountOwnershipValidationStrategy strategy = selectStrategy(customerType);
        return strategy.validate(accountModel, existingAccountCount);
    }

    private AccountOwnershipValidationStrategy selectStrategy(CustomerType customerType) {
        return switch (customerType) {
            case PERSONAL -> new PersonalCustomerValidationStrategy();
            case BUSINESS -> new BusinessCustomerValidationStrategy();
        };
    }
}
