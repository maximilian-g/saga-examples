package com.maximilian.restaurant.order.service.transaction;

public class NamedAction {

    private String name;
    private Runnable action;
    private final boolean canRollbackBeDone;

    public NamedAction(String name, Runnable action) {
        this.name = name;
        this.action = action;
        this.canRollbackBeDone = true;
    }

    public NamedAction(String name, Runnable action, boolean canRollbackBeDone) {
        this.name = name;
        this.action = action;
        this.canRollbackBeDone = canRollbackBeDone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Runnable getAction() {
        return action;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    public boolean canRollbackBeDone() {
        return canRollbackBeDone;
    }
}
