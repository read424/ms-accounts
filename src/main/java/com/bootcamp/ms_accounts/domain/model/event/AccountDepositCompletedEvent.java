package com.bootcamp.ms_accounts.domain.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDepositCompletedEvent {
    private String accountId;
    private String customerId;
    private Double amount;
    private Double newBalance;
    private String transactionId;
    private LocalDateTime completedAt;
}
