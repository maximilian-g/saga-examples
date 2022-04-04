package com.maximilian.restaurant.transaction;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 *  This class is a Saga implementation example.
 *  This app and this example is only uses HTTP, but for Saga implementation it is better to use some kind of Asynchronous Message Queue(Kafka, RabbitMQ, or other)
 *  Saga:
 *      Saga is a pattern that implements distributed transactions.
 *      There are problems that can be encountered if there is no distributed transaction implementation:
 *          1 - Writing to multiple services. For example you save some entity to service1.
 *              After that you want to save another entity to service2 within one transaction.
 *              Service1 works fine, service2 is down, you cannot save another entity, so transaction must be rolled back.
 *          2 - Reading from a service AND writing to another service depending on read information.
 *              You read entity1 from service1. It passed some "validation" rules, now you want to save something to service2.
 *              At that moment entity1 from service1 can already be changed by someone else.
 *          3 - Synchronous execution(that is why @Async used in current implementation).
 *              For example if you want to complete transaction synchronously that has lets say 5 services involved, it might take some time.
 *              So user experience become really bad.
 *       Saga has 3 types of actions:
 *          1 - Compensating actions. Actions that can be performed and compensated if error occurred.
 *          2 - Final actions. After that type of action only repeatable actions are present.
 *          3 - Repeatable(idempotent) actions. That type of actions means that you can execute them several times and result will be same as if you executed it once.
 */
public abstract class SagaService {

    protected final Logger logger;

    protected SagaService(Logger logger) {
        this.logger = logger;
    }

    protected void performActions(List<SagaStep> steps) {
        List<SagaStep> performedActions = new ArrayList<>(steps.size());
        SagaContext context = new SagaContext(logger);
        logger.info("Started transaction '" + context.getTransactionUUID().toString() + "'");
        for(SagaStep step : steps) {
            try {
                step.execute(context, step.getAction());
                if(!context.hasReachedFinalAction()) {
                    performedActions.add(step);
                }
            } catch (Exception ex) {
                logger.info("Exception during execution of action '" + step.getAction().getName() + "-"
                        + context.getTransactionUUID().toString() + "'. Error: " + ex.getMessage());
                if(!context.hasReachedFinalAction()) {
                    revertActions(performedActions, context);
                    break;
                }
            }
        }
        logger.info("Completed transaction '" + context.getTransactionUUID().toString() + "'");

    }

    protected void revertActions(List<SagaStep> steps, SagaContext context) {
        for(SagaStep step : steps) {
            try {
                step.execute(context, step.getCompensatingAction());
            } catch (Exception ex) {
                logger.info("Exception during rollback of step. Compensating action name: '" +
                        step.getCompensatingAction().getName() + "'. Error: " + ex.getMessage());
            }
        }
    }

}
