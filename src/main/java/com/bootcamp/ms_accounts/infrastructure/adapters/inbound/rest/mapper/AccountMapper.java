package com.bootcamp.ms_accounts.infrastructure.adapters.inbound.rest.mapper;

import com.bootcamp.ms_accounts.domain.model.dto.AccountModel;
import com.bootcamp.ms_accounts.domain.model.enums.AccountStatusModel;
import com.bootcamp.ms_accounts.infrastructure.adapters.inbound.rest.dto.AccountResponse;
import com.bootcamp.ms_accounts.infrastructure.adapters.inbound.rest.dto.AccountType;
import com.bootcamp.ms_accounts.infrastructure.adapters.inbound.rest.dto.AccountStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "accountId", source = "accountId")
    @Mapping(target = "customerId", source = "customerId")
    @Mapping(target = "accountType", source = "accountType", qualifiedByName = "accountTypeModelToAccountType")
    @Mapping(target = "balance", source = "balance")
    @Mapping(target = "maintenanceFee", source = "maintenanceFee")
    @Mapping(target = "monthlyTransactionLimit", source = "monthlyTransactionLimit")
    @Mapping(target = "monthlyTransactionCounter", source = "monthlyTransactionCounter")
    @Mapping(target = "restrictedOperationDay", source = "restrictedOperationDay", qualifiedByName = "integerToJsonNullable")
    @Mapping(target = "status", source = "status", qualifiedByName = "accountStatusModelToAccountStatus")
    @Mapping(target = "accountHolders", ignore = true)
    @Mapping(target = "authorizedSigners", ignore = true)
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    AccountResponse toRestDto(AccountModel domain);

    @Named("accountTypeModelToAccountType")
    default AccountType accountTypeModelToAccountType(com.bootcamp.ms_accounts.domain.model.enums.AccountTypeModel type) {
        if (type == null) {
            return null;
        }
        return AccountType.fromValue(type.toString());
    }

    @Named("accountStatusModelToAccountStatus")
    default AccountStatus accountStatusModelToAccountStatus(AccountStatusModel status) {
        if (status == null) {
            return null;
        }
        return AccountStatus.fromValue(status.toString());
    }

    @Named("localDateTimeToOffsetDateTime")
    default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.atOffset(ZoneOffset.UTC);
    }

    @Named("integerToJsonNullable")
    default JsonNullable<Integer> integerToJsonNullable(Integer value) {
        if (value == null) {
            return JsonNullable.undefined();
        }
        return JsonNullable.of(value);
    }
}
