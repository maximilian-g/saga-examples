package com.maximilian.restaurant.repository;

import com.maximilian.restaurant.entity.KitchenTicketItemLink;
import com.maximilian.restaurant.entity.KitchenTicketItemPrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KitchenTicketItemLinkRepository extends JpaRepository<KitchenTicketItemLink, KitchenTicketItemPrimaryKey> {
}
