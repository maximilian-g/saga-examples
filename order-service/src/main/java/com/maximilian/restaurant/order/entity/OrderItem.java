package com.maximilian.restaurant.order.entity;

import com.maximilian.restaurant.data.KitchenItem;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "orders_items_r")
public class OrderItem {

    @EmbeddedId
    private OrderItemPrimaryKey id;

    @Column
    private Long quantity;

    @ManyToOne
    @JoinColumn(name = "order_id", updatable = false, insertable = false)
    private Order order;


    public static OrderItem from(KitchenItem item, Order order) {
        OrderItemPrimaryKey key = new OrderItemPrimaryKey();
        key.setItemId(item.getItemId());
        key.setOrderId(order.getId());

        OrderItem orderItem = new OrderItem();
        orderItem.setId(key);
        orderItem.setQuantity((long)item.getQuantity());
        orderItem.setOrder(order);
        return orderItem;
    }

    public OrderItemPrimaryKey getId() {
        return id;
    }

    public void setId(OrderItemPrimaryKey id) {
        this.id = id;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", order=" + order.getId() +
                '}';
    }
}
