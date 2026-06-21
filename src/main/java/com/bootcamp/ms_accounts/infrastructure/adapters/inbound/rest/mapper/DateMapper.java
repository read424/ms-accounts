package com.bootcamp.ms_accounts.infrastructure.adapters.inbound.rest.mapper;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class DateMapper {

    @Named("localDateTimeToOffsetDateTime")
    public OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return OffsetDateTime.of(localDateTime, ZoneOffset.UTC);
    }
}
