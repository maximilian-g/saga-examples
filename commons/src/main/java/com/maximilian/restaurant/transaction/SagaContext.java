package com.maximilian.restaurant.transaction;

import org.slf4j.Logger;

import java.util.UUID;

public class SagaContext {

    private final UUID transactionUUID = UUID.randomUUID();
    private final Logger logger;

    private boolean hasReachedFinalAction = false;

    public SagaContext(Logger logger) {
        this.logger = logger;
    }

    public boolean hasReachedFinalAction() {
        return hasReachedFinalAction;
    }

    public void setReachedFinalAction(boolean reachedFinalAction) {
        this.hasReachedFinalAction = reachedFinalAction;
    }

    public UUID getTransactionUUID() {
        return transactionUUID;
    }

    public Logger getLogger() {
        return logger;
    }
}
