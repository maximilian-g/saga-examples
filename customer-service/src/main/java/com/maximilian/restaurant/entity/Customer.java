package com.maximilian.restaurant.entity;

import com.maximilian.restaurant.response.customer.CustomerResponse;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "customers_r")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id", nullable = false, unique = true)
    private Long id;

    @NotNull
    @Column
    private String name;

    @NotNull
    @Column
    @Enumerated(EnumType.STRING)
    private CustomerStatus status;

    @NotNull
    @Column
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

    public CustomerStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }

    public Boolean getCanMakeOrders() {
        return canMakeOrders;
    }

    public void setCanMakeOrders(Boolean canMakeOrders) {
        this.canMakeOrders = canMakeOrders;
    }

    public CustomerResponse toResponse() {
        CustomerResponse response = new CustomerResponse();
        response.setId(id);
        response.setName(name);
        response.setCanMakeOrders(canMakeOrders);
        return response;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", canMakeOrders=" + canMakeOrders +
                '}';
    }
}
