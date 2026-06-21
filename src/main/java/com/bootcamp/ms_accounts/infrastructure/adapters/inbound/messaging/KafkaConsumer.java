package com.bootcamp.ms_accounts.infrastructure.adapters.inbound.messaging;

import com.bootcamp.ms_accounts.application.ports.input.ProcessDepositUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {
  private final ProcessDepositUseCase processDepositUseCase;

  @KafkaListener(topics = "deposit.pending", groupId = "ms-accounts-group")
  public void onDepositPending(@Payload DepositPendingEvent event) {
    log.info("Processing deposit.pending event for transaction: {}", event.getTransactionId());
    processDepositUseCase.processDepositPending(
        event.getTransactionId(),
        event.getAccountId(),
        event.getAmount()
    ).doOnSuccess(v -> log.info("✓ Deposit pending processed for transaction: {}", event.getTransactionId()))
    .doOnError(error -> log.error("✗ Error processing deposit pending for transaction {}: {}",
        event.getTransactionId(), error.getMessage(), error))
    .subscribe();
  }
}
