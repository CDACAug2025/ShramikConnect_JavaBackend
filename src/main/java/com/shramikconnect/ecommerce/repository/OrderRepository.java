package com.shramikconnect.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shramikconnect.entity.Order;
import com.shramikconnect.entity.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    // ✅ This name is case-sensitive and must match Order.java
    Optional<Order> findByRazorpayOrderId(String razorpayOrderId);
    
    List<Order> findByWorker(User worker);
}