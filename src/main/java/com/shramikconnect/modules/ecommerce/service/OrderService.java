package com.shramikconnect.modules.ecommerce.service;

import com.shramikconnect.modules.ecommerce.entity.Order;
import com.shramikconnect.modules.ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Future Feature: Get orders for a specific worker
    // public List<Order> getOrdersByWorker(Long workerId) { ... }
}