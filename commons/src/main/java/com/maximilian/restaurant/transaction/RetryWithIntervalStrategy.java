package com.maximilian.restaurant.transaction;

import java.util.concurrent.TimeUnit;

public class RetryWithIntervalStrategy implements RetryStrategy {

    public static final int MAX_REPEATS_DEFAULT = 5;
    public static final long INTERVAL_MS_DEFAULT = TimeUnit.SECONDS.toMillis(3);

    private final int maxRepeats;
    private final long intervalMs;

    public RetryWithIntervalStrategy(int maxRepeats, long intervalMs) {
        this.maxRepeats = maxRepeats;
        this.intervalMs = intervalMs;
        if (maxRepeats <= 0) {
            throw new IllegalArgumentException("Maximum quantity of repeats cannot be less or equal to zero");
        }
        if (intervalMs <= 0) {
            throw new IllegalArgumentException("Interval cannot be less or equal to zero milliseconds");
        }
    }

    public RetryWithIntervalStrategy() {
        this.maxRepeats = MAX_REPEATS_DEFAULT;
        this.intervalMs = INTERVAL_MS_DEFAULT;
    }

    @Override
    public void executeWithRetries(SagaContext context, NamedAction action) {
        executeWithRetries(0, context, action);
    }

    protected void executeWithRetries(int currentRepeatQuantity, SagaContext context, NamedAction action) {
        if (currentRepeatQuantity < maxRepeats) {
            context.getLogger().info("Executing repeatable action '" + action.getName() + "-" + context.getTransactionUUID().toString() + "' for " + currentRepeatQuantity + " time");
            try {
                currentRepeatQuantity++;
                action.run();
                context.getLogger().info("Completed repeatable action '" + action.getName() + "-" + context.getTransactionUUID().toString() + "'");
            } catch (Exception ex) {
                sleep(intervalMs);
                executeWithRetries(currentRepeatQuantity, context, action);
            }
        } else {
            context.getLogger().info("Reached limit of repeats for '" + action.getName() + "-" + context.getTransactionUUID().toString() + "'");
        }
    }

    protected void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception ignored) {

        }
    }

}
