package com.bootcamp.ms_accounts.infrastructure.adapters.outbound.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {
  private final KafkaTemplate<String, Object> kafkaTemplate;

  public Mono<Void> sendDepositCompleted(DepositCompletedEvent event) {
    return Mono.fromRunnable(() -> {
      log.info("Publishing deposit.completed event for transaction: {}", event.getTransactionId());
      kafkaTemplate.send("deposit.completed", event.getTransactionId(), event);
    }).subscribeOn(Schedulers.boundedElastic()).then();
  }
}
