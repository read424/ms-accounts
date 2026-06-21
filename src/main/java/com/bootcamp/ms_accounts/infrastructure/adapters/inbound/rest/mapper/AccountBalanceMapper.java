package com.bootcamp.ms_accounts.infrastructure.adapters.inbound.rest.mapper;

import com.bootcamp.ms_accounts.domain.model.dto.AccountBalanceModel;
import com.bootcamp.ms_accounts.infrastructure.adapters.inbound.rest.dto.AccountBalanceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
@Mapper(componentModel = "spring")
public interface AccountBalanceMapper {

    @Mapping(target = "lastUpdated", source = "lastUpdated", qualifiedByName = "localDateTimeToOffsetDateTime")
    AccountBalanceResponse toRestDto(AccountBalanceModel domain);

    @Named("localDateTimeToOffsetDateTime")
    default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.atOffset(ZoneOffset.UTC);
    }
}
