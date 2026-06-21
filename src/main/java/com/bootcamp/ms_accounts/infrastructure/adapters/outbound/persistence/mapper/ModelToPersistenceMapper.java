package com.bootcamp.ms_accounts.infrastructure.adapters.outbound.persistence.mapper;

import com.bootcamp.ms_accounts.domain.model.dto.AccountHolderModel;
import com.bootcamp.ms_accounts.domain.model.dto.AccountModel;
import com.bootcamp.ms_accounts.domain.model.dto.AuthorizedSignerModel;
import com.bootcamp.ms_accounts.domain.model.enums.AccountTypeModel;
import com.bootcamp.ms_accounts.domain.model.enums.AccountStatusModel;
import com.bootcamp.ms_accounts.infrastructure.adapters.outbound.persistence.entity.AccountEntity;
import com.bootcamp.ms_accounts.infrastructure.adapters.outbound.persistence.entity.AccountHolderEntity;
import com.bootcamp.ms_accounts.infrastructure.adapters.outbound.persistence.entity.AuthorizedSignerEntity;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ModelToPersistenceMapper {

    @Mappings({
        @Mapping(target = "accountType", source = "accountType", qualifiedByName = "accountTypeToString"),
        @Mapping(target = "balance", source = "balance"),
        @Mapping(target = "status", source = "status", qualifiedByName = "statusModelToString")
    })
    AccountEntity toEntity(AccountModel accountModel);

    @Mappings({
        @Mapping(target = "accountType", source = "accountType", qualifiedByName = "stringToAccountType"),
        @Mapping(target = "status", source = "status", qualifiedByName = "stringToStatusModel")
    })
    AccountModel toDomainModel(AccountEntity accountEntity);

    @Named("accountTypeToString")
    default String accountTypeToString(AccountTypeModel accountType) {
        return accountType == null ? null : accountType.toString();
    }

    @Named("stringToAccountType")
    default AccountTypeModel stringToAccountType(String accountType) {
        return accountType == null ? null : AccountTypeModel.valueOf(accountType);
    }

    @Named("statusModelToString")
    default String statusModelToString(AccountStatusModel status) {
        return status == null ? null : status.toString();
    }

    @Named("stringToStatusModel")
    default AccountStatusModel stringToStatusModel(String status) {
        return status == null ? null : AccountStatusModel.valueOf(status);
    }

    List<AccountHolderEntity> toAccountHolderEntities(List<AccountHolderModel> holders);

    List<AuthorizedSignerEntity> toAuthorizedSignerEntities(List<AuthorizedSignerModel> signers);
}
