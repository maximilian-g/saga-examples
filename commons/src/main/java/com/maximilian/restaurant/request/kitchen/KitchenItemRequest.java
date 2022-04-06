package com.maximilian.restaurant.request.kitchen;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class KitchenItemRequest {

    @NotNull(message = "Name of item cannot be blank")
    @Size(min = 3, max = 128, message = "Name length must be between 3 and 128")
    private String name;
    @NotNull(message = "Cost of item cannot be blank")
    @Positive(message = "Cost cannot be negative or zero")
    private BigDecimal cost;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "KitchenItemRequest{" +
                "name='" + name + '\'' +
                ", cost=" + cost +
                '}';
    }
}
