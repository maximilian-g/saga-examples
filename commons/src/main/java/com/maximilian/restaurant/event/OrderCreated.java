package com.maximilian.restaurant.event;

import com.maximilian.restaurant.request.order.OrderRequest;

public class OrderCreated extends OrderRequest {

    private Long orderId;

    public OrderCreated() {
    }

    public OrderCreated(OrderRequest request, Long orderId) {
        super(request);
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
