package com.maximilian.restaurant.transaction;

public interface RetryStrategy {

    void executeWithRetries(SagaContext context, NamedAction action);

}
