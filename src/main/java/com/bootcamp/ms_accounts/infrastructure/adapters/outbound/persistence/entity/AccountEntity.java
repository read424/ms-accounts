package com.bootcamp.ms_accounts.infrastructure.adapters.outbound.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "accounts")
public class AccountEntity {
    @Id
    private String accountId;
    private String customerId;
    private String accountType;
    private Double balance;
    private Double maintenanceFee;
    private Integer monthlyTransactionLimit;
    private Integer monthlyTransactionCounter;
    private Integer restrictedOperationDay;
    private String status;
    private List<AccountHolderEntity> accountHolders;
    private List<AuthorizedSignerEntity> authorizedSigners;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
