package com.maximilian.restaurant.request.kitchen;

import com.maximilian.restaurant.data.Coordinates;
import com.maximilian.restaurant.data.KitchenItem;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class KitchenOrderRequest {

    @NotNull(message = "Items must not be blank")
    @NotEmpty
    private List<KitchenItem> items;
    @NotNull(message = "Delivery point must not be blank")
    @Valid
    private Coordinates deliveryPoint;

    @NotNull(message = "Order id must not be blank")
    private Long orderId;

    public List<KitchenItem> getItems() {
        return items;
    }

    public void setItems(List<KitchenItem> items) {
        this.items = items;
    }

    public Coordinates getDeliveryPoint() {
        return deliveryPoint;
    }

    public void setDeliveryPoint(Coordinates deliveryPoint) {
        this.deliveryPoint = deliveryPoint;
    }

    @Override
    public String toString() {
        return "KitchenOrderRequest{" +
                "items=" + items +
                ", deliveryPoint=" + deliveryPoint +
                '}';
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
