package com.bootcamp.ms_accounts.domain.model.dto;

import com.bootcamp.ms_accounts.domain.model.enums.AccountStatusModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAccountModel {
    private AccountStatusModel status;
    private List<AccountHolderModel> accountHolders;
    private List<AuthorizedSignerModel> authorizedSigners;
}
