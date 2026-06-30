package com.bootcamp.ms_accounts.infrastructure.adapters.outbound.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "accounts")
@CompoundIndexes({
    @CompoundIndex(name = "idx_customerId_status", def = "{ 'customerId' : 1, 'status' : 1 }"),
    @CompoundIndex(name = "idx_accountType_status", def = "{ 'accountType' : 1, 'status' : 1 }"),
    @CompoundIndex(name = "idx_customerId_accountType", def = "{ 'customerId' : 1, 'accountType' : 1 }"),
    @CompoundIndex(name = "idx_status_createdAt", def = "{ 'status' : 1, 'createdAt' : -1 }")
})
public class AccountEntity {
    @Id
    private String accountId;

    @Indexed(name = "idx_customerId")
    private String customerId;

    @Indexed(name = "idx_accountType")
    private String accountType;

    private Double balance;
    private Double maintenanceFee;
    private Integer monthlyTransactionLimit;
    private Integer monthlyTransactionCounter;
    private Integer restrictedOperationDay;

    @Indexed(name = "idx_status")
    private String status;

    @Indexed(name = "idx_profileType")
    private String profileType;

    private Double minimumDailyBalance;
    private Integer maxMonthlyTransactionsNoFee;
    private List<AccountHolderEntity> accountHolders;
    private List<AuthorizedSignerEntity> authorizedSigners;

    @Indexed(name = "idx_createdAt_desc")
    private LocalDateTime createdAt;

    @Indexed(name = "idx_updatedAt_desc")
    private LocalDateTime updatedAt;
}
