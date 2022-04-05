package com.maximilian.restaurant.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Collection;

//TODO not order, but "ticket"
@Entity
@Table(name = "kitchen_orders_r")
public class KitchenOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kitchen_order_id", nullable = false, unique = true)
    private Long id;

    @NotNull
    @Column(unique = true, nullable = false)
    private Long outerOrderId;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Order state cannot be blank")
    @Column
    private KitchenOrderStatus status;

    // X
    @NotNull(message = "Longitude must not be blank")
    @Column
    private BigDecimal longitude;

    // Y
    @NotNull(message = "Latitude must not be blank")
    @Column
    private BigDecimal latitude;

    @OneToMany(mappedBy = "kitchenOrder", fetch = FetchType.LAZY)
    private Collection<KitchenOrderItemLink> orderItems;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public Collection<KitchenOrderItemLink> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(Collection<KitchenOrderItemLink> orderItems) {
        this.orderItems = orderItems;
    }

    public KitchenOrderStatus getStatus() {
        return status;
    }

    public void setStatus(KitchenOrderStatus status) {
        this.status = status;
    }

    public Long getOuterOrderId() {
        return outerOrderId;
    }

    public void setOuterOrderId(Long outerOrderId) {
        this.outerOrderId = outerOrderId;
    }
}
