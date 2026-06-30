package com.bootcamp.ms_accounts.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    private String accountId;
    private String customerId;
    private String accountType;
    private Double balance;
    private Double maintenanceFee;
    private Integer monthlyTransactionLimit;
    private Integer monthlyTransactionCounter;
    private Integer restrictedOperationDay;
    private String status;
    private String profileType;
    private Double minimumDailyBalance;
    private Integer maxMonthlyTransactionsNoFee;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
