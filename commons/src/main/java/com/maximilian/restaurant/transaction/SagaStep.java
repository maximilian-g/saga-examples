package com.maximilian.restaurant.transaction;

public interface SagaStep {

    default void execute(SagaContext context, NamedAction action) {
        context.getLogger().info("Executing '" + action.getName() + "-" + context.getTransactionUUID().toString() + "' action");
        action.run();
        context.getLogger().info("Executed '" + action.getName() + "-" + context.getTransactionUUID().toString() + "' action");
    }

    NamedAction getAction();

    default NamedAction getCompensatingAction() {
        return NamedAction.getEmptyAction("empty-compensating-action");
    }

}
