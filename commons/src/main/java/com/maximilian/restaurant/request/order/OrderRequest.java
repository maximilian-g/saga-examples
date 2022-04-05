package com.maximilian.restaurant.request.order;

import com.maximilian.restaurant.data.CardDetails;
import com.maximilian.restaurant.data.Coordinates;
import com.maximilian.restaurant.data.KitchenItem;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class OrderRequest {

    @NotNull(message = "Customer must not be blank")
    private Long customerId;
    @NotNull(message = "Items must not be blank")
    @NotEmpty
    private List<KitchenItem> items;
    @NotNull(message = "Delivery point must not be blank")
    @Valid
    private Coordinates deliveryPoint;
    @NotNull(message = "Card details must not be blank")
    @Valid
    private CardDetails cardDetails;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

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

    public CardDetails getCardDetails() {
        return cardDetails;
    }

    public void setCardDetails(CardDetails cardDetails) {
        this.cardDetails = cardDetails;
    }

    @Override
    public String toString() {
        return "OrderRequest{" +
                "customerId=" + customerId +
                ", items=" + items +
                ", deliveryPoint=" + deliveryPoint +
                ", cardDetails=" + cardDetails +
                '}';
    }
}
