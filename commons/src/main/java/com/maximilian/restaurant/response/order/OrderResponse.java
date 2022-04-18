package com.maximilian.restaurant.response.order;

import com.maximilian.restaurant.response.customer.CustomerResponse;
import com.maximilian.restaurant.response.kitchen.KitchenTicketResponse;

public class OrderResponse {

    private Long orderId;
    private String status;
    private String description;
    private KitchenTicketResponse ticket;
    private CustomerResponse customer;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public CustomerResponse getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerResponse customer) {
        this.customer = customer;
    }

    public KitchenTicketResponse getTicket() {
        return ticket;
    }

    public void setTicket(KitchenTicketResponse ticket) {
        this.ticket = ticket;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
