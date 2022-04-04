package com.maximilian.restaurant.transaction;

public abstract class NamedAction implements Runnable {

    private String name;

    public NamedAction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static NamedAction getEmptyAction(String name) {
        return new NamedAction(name) {
            @Override
            public void run() {

            }
        };
    }

    @Override
    public String toString() {
        return "NamedAction{" +
                "name='" + name + '\'' +
                '}';
    }

}
