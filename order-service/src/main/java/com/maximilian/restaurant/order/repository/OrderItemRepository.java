package com.maximilian.restaurant.order.repository;

import com.maximilian.restaurant.order.entity.OrderItem;
import com.maximilian.restaurant.order.entity.OrderItemPrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemPrimaryKey> {
}

