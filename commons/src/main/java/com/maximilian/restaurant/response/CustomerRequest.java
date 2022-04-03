package com.maximilian.restaurant.response;

import javax.validation.constraints.NotNull;

public class CustomerRequest {

    @NotNull(message = "Name of customer cannot be empty")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CustomerRequest{" +
                "name='" + name + '\'' +
                '}';
    }
}
