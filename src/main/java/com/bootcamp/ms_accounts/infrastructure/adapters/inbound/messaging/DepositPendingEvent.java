package com.bootcamp.ms_accounts.infrastructure.adapters.inbound.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositPendingEvent {
  @JsonProperty("transaction_id")
  private String transactionId;

  @JsonProperty("account_id")
  private String accountId;

  @JsonProperty("amount")
  private Double amount;

  @JsonProperty("timestamp")
  private LocalDateTime timestamp;
}
