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
public class AccountCreatedEvent {
    private String accountId;
    private String customerId;
    private String accountType;
    private Double initialBalance;
    private String status;
    private LocalDateTime createdAt;
}
