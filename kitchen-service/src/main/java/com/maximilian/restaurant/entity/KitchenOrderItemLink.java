package com.maximilian.restaurant.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Entity
@Table(name = "item_order_links_r")
public class KitchenOrderItemLink {

    @EmbeddedId
    private KitchenOrderItemPrimaryKey id;

    @ManyToOne
    @JoinColumn(name = "kitchen_item_id", updatable = false, insertable = false)
    private KitchenItem kitchenItem;

    @ManyToOne
    @JoinColumn(name = "kitchen_order_id", updatable = false, insertable = false)
    private KitchenOrder kitchenOrder;

    @Column
    @NotNull
    @Positive
    private Integer quantity;

    public KitchenOrderItemPrimaryKey getId() {
        return id;
    }

    public void setId(KitchenOrderItemPrimaryKey id) {
        this.id = id;
    }

    public KitchenItem getKitchenItem() {
        return kitchenItem;
    }

    public void setKitchenItem(KitchenItem kitchenItem) {
        this.kitchenItem = kitchenItem;
    }

    public KitchenOrder getKitchenOrder() {
        return kitchenOrder;
    }

    public void setKitchenOrder(KitchenOrder kitchenOrder) {
        this.kitchenOrder = kitchenOrder;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}
