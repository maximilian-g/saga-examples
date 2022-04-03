package com.maximilian.restaurant.repository;

import com.maximilian.restaurant.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c WHERE c.id = :id")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Customer> getByIdBlocking(Long id);

}
