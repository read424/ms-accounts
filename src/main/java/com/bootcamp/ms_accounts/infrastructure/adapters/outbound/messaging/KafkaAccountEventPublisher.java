package com.bootcamp.ms_accounts.infrastructure.adapters.outbound.messaging;

import com.bootcamp.ms_accounts.application.ports.output.AccountEventPublisherPort;
import com.bootcamp.ms_accounts.domain.model.event.AccountCreatedEvent;
import com.bootcamp.ms_accounts.domain.model.event.AccountDepositCompletedEvent;
import com.bootcamp.ms_accounts.domain.model.event.AccountWithdrawalCompletedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaAccountEventPublisher implements AccountEventPublisherPort {

    private final KafkaSender<String, String> kafkaSender;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topics.account-created}")
    private String accountCreatedTopic;

    @Value("${kafka.topics.account-deposit-completed}")
    private String depositCompletedTopic;

    @Value("${kafka.topics.account-withdrawal-completed}")
    private String withdrawalCompletedTopic;

    @Override
    public Mono<Void> publishAccountCreated(AccountCreatedEvent event) {
        log.info("Publishing AccountCreatedEvent for accountId: {}", event.getAccountId());
        return sendMessage(accountCreatedTopic, event.getAccountId(), event)
            .doOnSuccess(result -> log.info("AccountCreatedEvent published successfully"))
            .doOnError(error -> log.error("Error publishing AccountCreatedEvent", error))
            .then();
    }

    @Override
    public Mono<Void> publishDepositCompleted(AccountDepositCompletedEvent event) {
        log.info("Publishing AccountDepositCompletedEvent for accountId: {}", event.getAccountId());
        return sendMessage(depositCompletedTopic, event.getAccountId(), event)
            .doOnSuccess(result -> log.info("AccountDepositCompletedEvent published successfully"))
            .doOnError(error -> log.error("Error publishing AccountDepositCompletedEvent", error))
            .then();
    }

    @Override
    public Mono<Void> publishWithdrawalCompleted(AccountWithdrawalCompletedEvent event) {
        log.info("Publishing AccountWithdrawalCompletedEvent for accountId: {}", event.getAccountId());
        return sendMessage(withdrawalCompletedTopic, event.getAccountId(), event)
            .doOnSuccess(result -> log.info("AccountWithdrawalCompletedEvent published successfully"))
            .doOnError(error -> log.error("Error publishing AccountWithdrawalCompletedEvent", error))
            .then();
    }

    private Mono<Void> sendMessage(String topic, String key, Object event) {
        try {
            String value = objectMapper.writeValueAsString(event);
            SenderRecord<String, String, Void> record = SenderRecord.create(topic, null, null, key, value, null);
            return kafkaSender.send(Mono.just(record)).then();
        } catch (Exception e) {
            log.error("Error serializing event", e);
            return Mono.error(e);
        }
    }
}
