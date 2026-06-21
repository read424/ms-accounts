package com.bootcamp.ms_accounts.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountBalanceModel {
    private String accountId;
    private Double balance;
    private LocalDateTime lastUpdated;
}
