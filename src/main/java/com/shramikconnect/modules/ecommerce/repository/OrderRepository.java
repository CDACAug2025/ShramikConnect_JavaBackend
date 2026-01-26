package com.shramikconnect.modules.ecommerce.repository;

import com.shramikconnect.modules.ecommerce.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Useful for fetching history of a specific worker
    // List<Order> findByWorkerId(Long workerId); 
}