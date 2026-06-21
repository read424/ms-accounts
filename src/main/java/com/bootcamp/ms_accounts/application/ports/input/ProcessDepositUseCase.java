package com.bootcamp.ms_accounts.application.ports.input;

import reactor.core.publisher.Mono;

public interface ProcessDepositUseCase {
  Mono<Void> processDepositPending(String transactionId, String accountId, Double amount);
}
