package com.maximilian.restaurant.request.customer;

import javax.validation.constraints.NotNull;

public class UpdateCustomerRequest {

    @NotNull(message ="Can make orders property must not be blank")
    private Boolean canMakeOrders;

    public Boolean getCanMakeOrders() {
        return canMakeOrders;
    }

    public void setCanMakeOrders(Boolean canMakeOrders) {
        this.canMakeOrders = canMakeOrders;
    }
}
