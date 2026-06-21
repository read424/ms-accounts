package com.bootcamp.ms_accounts.domain.model.mapper;

import com.bootcamp.ms_accounts.domain.model.dto.AccountModel;
import com.bootcamp.ms_accounts.domain.model.dto.UpdateAccountModel;
import com.bootcamp.ms_accounts.domain.model.enums.AccountType;
import com.bootcamp.ms_accounts.infrastructure.adapters.inbound.rest.dto.CreateAccountRequest;
import com.bootcamp.ms_accounts.infrastructure.adapters.inbound.rest.dto.UpdateAccountRequest;
import org.mapstruct.*;
import org.springframework.stereotype.Component;
import com.bootcamp.ms_accounts.domain.model.enums.AccountStatus;

@Component
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RequestToModelMapper {

    @Mappings({
        @Mapping(target = "customerId", source = "customerId"),
        @Mapping(target = "accountType", source = "accountType", qualifiedByName = "accountTypeStringToEnum"),
        @Mapping(target = "initialBalance", source = "initialBalance"),
        @Mapping(target = "maintenanceFee", source = "maintenanceFee"),
        @Mapping(target = "monthlyTransactionLimit", source = "monthlyTransactionLimit"),
        @Mapping(target = "restrictedOperationDay", source = "restrictedOperationDay"),
        @Mapping(target = "accountHolders", source = "accountHolders"),
        @Mapping(target = "authorizedSigners", source = "authorizedSigners")
    })
    AccountModel toAccountModel(CreateAccountRequest createAccountRequest);

    @Mapping(target = "status", source = "status", qualifiedByName = "statusEnumToAccountStatus")
    UpdateAccountModel toUpdateAccountModel(UpdateAccountRequest updateAccountRequest);

    @Named("accountTypeStringToEnum")
    default AccountType accountTypeStringToEnum(String accountType) {
        if (accountType == null) {
            return null;
        }
        return AccountType.valueOf(accountType);
    }

    @Named("statusEnumToAccountStatus")
    default AccountStatus statusEnumToAccountStatus(UpdateAccountRequest.StatusEnum statusEnum) {
        if (statusEnum == null) {
            return null;
        }
        return AccountStatus.valueOf(statusEnum.toString());
    }
}
