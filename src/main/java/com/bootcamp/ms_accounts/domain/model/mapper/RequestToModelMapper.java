package com.bootcamp.ms_accounts.domain.model.mapper;

import com.bootcamp.ms_accounts.domain.model.dto.AccountModel;
import com.bootcamp.ms_accounts.domain.model.dto.UpdateAccountModel;
import com.bootcamp.ms_accounts.domain.model.enums.AccountTypeModel;
import com.bootcamp.ms_accounts.domain.model.enums.AccountStatusModel;
import com.bootcamp.ms_accounts.infrastructure.adapters.inbound.rest.dto.CreateAccountRequest;
import com.bootcamp.ms_accounts.infrastructure.adapters.inbound.rest.dto.UpdateAccountRequest;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RequestToModelMapper {

    @Mappings({
        @Mapping(target = "accountId", ignore = true),
        @Mapping(target = "customerId", source = "customerId"),
        @Mapping(target = "accountType", source = "accountType", qualifiedByName = "accountTypeToEnum"),
        @Mapping(target = "balance", source = "initialBalance"),
        @Mapping(target = "maintenanceFee", source = "maintenanceFee"),
        @Mapping(target = "monthlyTransactionLimit", source = "monthlyTransactionLimit"),
        @Mapping(target = "monthlyTransactionCounter", constant = "0"),
        @Mapping(target = "restrictedOperationDay", source = "restrictedOperationDay"),
        @Mapping(target = "accountHolders", ignore = true),
        @Mapping(target = "authorizedSigners", ignore = true),
        @Mapping(target = "status", constant = "ACTIVE"),
        @Mapping(target = "createdAt", ignore = true),
        @Mapping(target = "updatedAt", ignore = true)
    })
    AccountModel toAccountModel(CreateAccountRequest createAccountRequest);

    @Mapping(target = "status", source = "status", qualifiedByName = "statusEnumToAccountStatus")
    UpdateAccountModel toUpdateAccountModel(UpdateAccountRequest updateAccountRequest);

    @Named("accountTypeToEnum")
    default AccountTypeModel accountTypeToEnum(com.bootcamp.ms_accounts.infrastructure.adapters.inbound.rest.dto.AccountType accountType) {
        if (accountType == null) {
            return null;
        }
        return AccountTypeModel.valueOf(accountType.toString());
    }

    @Named("statusEnumToAccountStatus")
    default AccountStatusModel statusEnumToAccountStatus(com.bootcamp.ms_accounts.infrastructure.adapters.inbound.rest.dto.AccountStatus accountStatus) {
        if (accountStatus == null) {
            return null;
        }
        return AccountStatusModel.valueOf(accountStatus.toString());
    }
}
