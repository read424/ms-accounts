package com.bootcamp.ms_accounts.infrastructure.adapters.outbound.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorizedSignerEntity {
    private String documentType;
    private String documentNumber;
    private String fullName;
}
