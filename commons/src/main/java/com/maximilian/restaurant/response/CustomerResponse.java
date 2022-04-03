package com.maximilian.restaurant.response;

public class CustomerResponse {

    private Long id;
    private String name;
    private Boolean canMakeOrders;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getCanMakeOrders() {
        return canMakeOrders;
    }

    public void setCanMakeOrders(Boolean canMakeOrders) {
        this.canMakeOrders = canMakeOrders;
    }

    @Override
    public String toString() {
        return "CustomerResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", canMakeOrders=" + canMakeOrders +
                '}';
    }
}
