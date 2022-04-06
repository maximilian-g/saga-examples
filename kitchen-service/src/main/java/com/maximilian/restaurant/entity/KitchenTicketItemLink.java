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
@Table(name = "item_ticket_links_r")
public class KitchenTicketItemLink {

    @EmbeddedId
    private KitchenTicketItemPrimaryKey id;

    @ManyToOne
    @JoinColumn(name = "kitchen_item_id", updatable = false, insertable = false)
    private KitchenItem kitchenItem;

    @ManyToOne
    @JoinColumn(name = "outer_order_id", updatable = false, insertable = false)
    private KitchenTicket kitchenTicket;

    @Column
    @NotNull
    @Positive
    private Integer quantity;

    public KitchenTicketItemPrimaryKey getId() {
        return id;
    }

    public void setId(KitchenTicketItemPrimaryKey id) {
        this.id = id;
    }

    public KitchenItem getKitchenItem() {
        return kitchenItem;
    }

    public void setKitchenItem(KitchenItem kitchenItem) {
        this.kitchenItem = kitchenItem;
    }

    public KitchenTicket getKitchenTicket() {
        return kitchenTicket;
    }

    public void setKitchenTicket(KitchenTicket kitchenTicket) {
        this.kitchenTicket = kitchenTicket;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}
