package com.maximilian.restaurant.response.order;

public class OrderCreatedResponse {

    private Long id;
    private String orderStatus;

    public OrderCreatedResponse() {}

    public OrderCreatedResponse(Long id, String orderStatus) {
        this.id = id;
        this.orderStatus = orderStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Override
    public String toString() {
        return "OrderCreatedResponse{" +
                "id=" + id +
                ", orderStatus='" + orderStatus + '\'' +
                '}';
    }
}
