package com.maximilian.restaurant.order.repository;

import com.maximilian.restaurant.order.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
