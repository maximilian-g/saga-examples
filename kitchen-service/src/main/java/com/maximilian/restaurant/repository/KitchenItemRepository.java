package com.maximilian.restaurant.repository;

import com.maximilian.restaurant.entity.KitchenItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KitchenItemRepository extends JpaRepository<KitchenItem, Long> {
}
