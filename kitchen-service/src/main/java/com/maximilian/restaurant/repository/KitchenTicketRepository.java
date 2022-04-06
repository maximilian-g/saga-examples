package com.maximilian.restaurant.repository;

import com.maximilian.restaurant.entity.KitchenTicket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KitchenTicketRepository extends JpaRepository<KitchenTicket, Long> {

}
