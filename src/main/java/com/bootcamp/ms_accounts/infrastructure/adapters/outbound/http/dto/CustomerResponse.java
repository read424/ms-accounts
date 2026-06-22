package com.bootcamp.ms_accounts.infrastructure.adapters.outbound.http.dto;

import com.bootcamp.ms_accounts.domain.model.enums.CustomerType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomerResponse {

    private String customerId;

    private CustomerType customerType;

    private String documentType;

    private String documentNumber;

    @JsonProperty(value = "firstName")
    private String firstName;

    @JsonProperty(value = "lastName")
    private String lastName;

    @JsonProperty(value = "businessName")
    private String businessName;

    private String email;

    private String phoneNumber;
}
