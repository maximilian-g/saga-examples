package com.maximilian.restaurant.order.service.transaction;

public interface RevertableAction {

    NamedAction getNamedAction();

    NamedAction getRollbackAction();

}
