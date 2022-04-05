package com.maximilian.restaurant.repository;

import com.maximilian.restaurant.entity.KitchenOrderItemLink;
import com.maximilian.restaurant.entity.KitchenOrderItemPrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KitchenOrderItemLinkRepository extends JpaRepository<KitchenOrderItemLink, KitchenOrderItemPrimaryKey> {
}
