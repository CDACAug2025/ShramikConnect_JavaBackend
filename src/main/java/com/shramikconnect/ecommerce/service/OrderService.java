package com.shramikconnect.ecommerce.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shramikconnect.ecommerce.repository.OrderItemRepository;
import com.shramikconnect.ecommerce.repository.OrderRepository;
import com.shramikconnect.ecommerce.repository.ProductRepository;
import com.shramikconnect.entity.Order;
import com.shramikconnect.entity.OrderItem;
import com.shramikconnect.entity.Product;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order createInitialOrder(Order order, List<OrderItem> items) {
        // 1. Save main order
        Order savedOrder = orderRepository.save(order);

        for (OrderItem item : items) {
            // Fetch fresh product data to check stock
            Product product = productRepository.findById(item.getProduct().getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            
            // ✅ Check if stock is sufficient
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for: " + product.getName());
            }

            // ✅ Reduce Stock and save
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            // 2. Link item to the saved order and save
            item.setOrder(savedOrder);
            orderItemRepository.save(item);
        }
        return savedOrder;
    }

    @Transactional
    public void updateOrderPaymentStatus(String razorpayOrderId, String paymentId, String status) {
        Order order = orderRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setRazorpayPaymentId(paymentId);
        order.setStatus(status);
        orderRepository.save(order);
    }
}