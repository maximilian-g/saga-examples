package com.maximilian.restaurant.transaction;

public abstract class RepeatableSagaStep implements SagaStep {

    private final RetryStrategy strategy;

    protected RepeatableSagaStep(RetryStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public void execute(SagaContext context, NamedAction action) {
        strategy.executeWithRetries(context, action);
    }

}
