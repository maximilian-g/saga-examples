package com.maximilian.restaurant.order.service.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RevertableActionsService {

    private final Logger logger = LoggerFactory.getLogger(RevertableActionsService.class);

    @Async
    public void performActions(List<RevertableAction> actions) {
        UUID requestUUID = UUID.randomUUID();
        List<RevertableAction> performedActions = new ArrayList<>(actions.size());
        boolean reachedNotRevertableAction = false;
        for(RevertableAction action : actions) {
            try {
                logger.info("Started processing action '" + action.getNamedAction().getName() + "-" + requestUUID.toString() + "'");
                action.getNamedAction().getAction().run();
                if(!reachedNotRevertableAction) {
                    reachedNotRevertableAction = action.getNamedAction().canRollbackBeDone();
                    performedActions.add(action);
                    if(reachedNotRevertableAction) {
                        logger.info("Action '" + action.getNamedAction().getName() + "-" + requestUUID.toString() +
                                "' is final, revert cannot be done for previous actions");
                    }
                }
                logger.info("Completed processing action '" + action.getNamedAction().getName() + "-" + requestUUID.toString() + "'");
            } catch (Exception ex) {
                logger.info("Exception during execution of list '" + action.getNamedAction().getName() + "-" + requestUUID.toString() +
                        "'. Error: " + ex.getMessage());
                if(!reachedNotRevertableAction) {
                    revertActions(performedActions, requestUUID);
                    break;
                }
            }
        }

    }

    public void revertActions(List<RevertableAction> actions, UUID requestUUID) {
        for(RevertableAction actionToRollback : actions) {
            try {
                logger.info("Started rollback action '" + actionToRollback.getNamedAction().getName() + "-" + requestUUID.toString() + "'");
                actionToRollback.getRollbackAction().getAction().run();
                logger.info("Completed rollback action '" + actionToRollback.getNamedAction().getName() + "-" + requestUUID.toString() + "'");
            } catch (Exception ex) {
                logger.info("Exception during rollback of list '" + requestUUID.toString() + "'. Error: " + ex.getMessage());
            }
        }
    }

}
