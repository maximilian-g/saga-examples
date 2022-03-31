package com.maximilian.restaurant.data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class Coordinates {

    // X
    @NotNull(message = "Longitude must not be blank")
    private BigDecimal longitude;
    // Y
    @NotNull(message = "Latitude must not be blank")
    private BigDecimal latitude;

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

    @Override
    public String toString() {
        return "Coordinates{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
