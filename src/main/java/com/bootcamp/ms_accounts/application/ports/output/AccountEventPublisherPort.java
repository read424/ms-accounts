package com.bootcamp.ms_accounts.application.ports.output;

import com.bootcamp.ms_accounts.domain.model.event.AccountCreatedEvent;
import com.bootcamp.ms_accounts.domain.model.event.AccountDepositCompletedEvent;
import com.bootcamp.ms_accounts.domain.model.event.AccountWithdrawalCompletedEvent;
import reactor.core.publisher.Mono;

public interface AccountEventPublisherPort {
    Mono<Void> publishAccountCreated(AccountCreatedEvent event);
    Mono<Void> publishDepositCompleted(AccountDepositCompletedEvent event);
    Mono<Void> publishWithdrawalCompleted(AccountWithdrawalCompletedEvent event);
}
