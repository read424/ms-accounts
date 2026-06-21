package com.bootcamp.ms_accounts.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.bootcamp.ms_accounts.domain.model.enums.AccountTypeModel;
import com.bootcamp.ms_accounts.domain.model.enums.AccountStatusModel;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountModel {
    private String accountId;
    private String customerId;
    private AccountTypeModel accountType;
    private Double balance;
    private Double maintenanceFee;
    private Integer monthlyTransactionLimit;
    private Integer monthlyTransactionCounter;
    private Integer restrictedOperationDay;
    private List<AccountHolderModel> accountHolders;
    private List<AuthorizedSignerModel> authorizedSigners;
    private AccountStatusModel status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
