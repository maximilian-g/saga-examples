package com.maximilian.restaurant.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Collection;

//TODO not order, but "ticket"
@Entity
@Table(name = "kitchen_tickets_r")
public class KitchenTicket {

    @Id
    @NotNull
    @Column(name = "outer_order_id", unique = true, nullable = false)
    private Long outerOrderId;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Ticket state cannot be blank")
    @Column
    private KitchenTicketStatus status;

    // X
    @NotNull(message = "Longitude must not be blank")
    @Column
    private BigDecimal longitude;

    // Y
    @NotNull(message = "Latitude must not be blank")
    @Column
    private BigDecimal latitude;

    @OneToMany(mappedBy = "kitchenTicket", fetch = FetchType.LAZY)
    private Collection<KitchenTicketItemLink> ticketItems;

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

    public Collection<KitchenTicketItemLink> getTicketItems() {
        return ticketItems;
    }

    public void setTicketItems(Collection<KitchenTicketItemLink> orderItems) {
        this.ticketItems = orderItems;
    }

    public KitchenTicketStatus getStatus() {
        return status;
    }

    public void setStatus(KitchenTicketStatus status) {
        this.status = status;
    }

    public Long getOuterOrderId() {
        return outerOrderId;
    }

    public void setOuterOrderId(Long outerOrderId) {
        this.outerOrderId = outerOrderId;
    }
}
