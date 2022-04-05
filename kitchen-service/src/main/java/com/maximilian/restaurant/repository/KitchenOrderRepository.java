package com.maximilian.restaurant.repository;

import com.maximilian.restaurant.entity.KitchenOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KitchenOrderRepository extends JpaRepository<KitchenOrder, Long> {

    Optional<KitchenOrder> findKitchenOrderByOuterOrderId(Long id);

}
