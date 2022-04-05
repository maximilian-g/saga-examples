package com.maximilian.restaurant.response.kitchen;

import com.maximilian.restaurant.data.Coordinates;
import com.maximilian.restaurant.data.KitchenItemDetails;

import java.util.List;

public class KitchenOrderResponse {

    private Long kitchenOrderId;

    private Coordinates deliveryPoint;

    private String status;

    private List<KitchenItemDetails> kitchenItemDetails;

    public Coordinates getDeliveryPoint() {
        return deliveryPoint;
    }

    public void setDeliveryPoint(Coordinates deliveryPoint) {
        this.deliveryPoint = deliveryPoint;
    }

    public List<KitchenItemDetails> getKitchenItemDetails() {
        return kitchenItemDetails;
    }

    public void setKitchenItemDetails(List<KitchenItemDetails> kitchenItemDetails) {
        this.kitchenItemDetails = kitchenItemDetails;
    }

    public Long getKitchenOrderId() {
        return kitchenOrderId;
    }

    public void setKitchenOrderId(Long kitchenOrderId) {
        this.kitchenOrderId = kitchenOrderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "KitchenOrderResponse{" +
                "kitchenOrderId=" + kitchenOrderId +
                ", deliveryPoint=" + deliveryPoint +
                ", status='" + status + '\'' +
                ", kitchenItemDetails=" + kitchenItemDetails +
                '}';
    }
}
