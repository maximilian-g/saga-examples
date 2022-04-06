package com.maximilian.restaurant.transaction;

public interface SagaStep {

    default void execute(SagaContext context, NamedAction action) {
        context.getLogger().info("Executing '" + action.getName() + "-" + context.getTransactionUUID().toString() + "' action");
        action.run();
        context.getLogger().info("Completed '" + action.getName() + "-" + context.getTransactionUUID().toString() + "' action");
    }

    default void afterExecute(SagaContext context, NamedAction action) {

    }

    NamedAction getAction();

    default NamedAction getCompensatingAction() {
        return NamedAction.getEmptyAction("empty-compensating-action");
    }

}
