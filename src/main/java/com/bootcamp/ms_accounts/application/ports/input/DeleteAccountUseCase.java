package com.bootcamp.ms_accounts.application.ports.input;

import reactor.core.publisher.Mono;

public interface DeleteAccountUseCase {
    Mono<Void> deleteAccount(String accountId);
}
