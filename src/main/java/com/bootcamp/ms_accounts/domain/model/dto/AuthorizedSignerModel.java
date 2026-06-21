package com.bootcamp.ms_accounts.domain.model.dto;

import com.bootcamp.ms_accounts.domain.model.enums.DocumentTypeModel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorizedSignerModel {
    private DocumentTypeModel documentType;
    private String documentNumber;
    private String fullName;
}
