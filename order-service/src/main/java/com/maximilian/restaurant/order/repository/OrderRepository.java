package com.maximilian.restaurant.order.repository;

import com.maximilian.restaurant.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
