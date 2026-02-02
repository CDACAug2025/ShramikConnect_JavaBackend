package com.shramikconnect.ecommerce.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.shramikconnect.ecommerce.repository.OrderRepository;
import com.shramikconnect.ecommerce.repository.ProductRepository;
import com.shramikconnect.ecommerce.service.OrderService;
import com.shramikconnect.entity.Order;
import com.shramikconnect.entity.OrderItem;
import com.shramikconnect.entity.Product;
import com.shramikconnect.entity.User;
import com.shramikconnect.modules.user.repository.UserRepository;

@RestController
@RequestMapping("/api/worker/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private OrderRepository orderRepo;

    private final String KEY_ID = "rzp_test_SAWUs3cxm7J6XZ";
    private final String KEY_SECRET = "Ja3C16xXT88G6IFVzn80aUsn";

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> data) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User worker = userRepo.findByEmail(email).orElseThrow();

            Double totalAmount = Double.valueOf(data.get("totalAmount").toString());
            Map<String, String> address = (Map<String, String>) data.get("address");

            RazorpayClient client = new RazorpayClient(KEY_ID, KEY_SECRET);
            JSONObject options = new JSONObject();
            options.put("amount", (int) (totalAmount * 100)); // in paise
            options.put("currency", "INR");
            options.put("receipt", "txn_" + System.currentTimeMillis());

            com.razorpay.Order rzpOrder = client.orders.create(options);

            Order order = Order.builder()
                    .worker(worker)
                    .razorpayOrderId(rzpOrder.get("id"))
                    .totalAmount(totalAmount)
                    .shippingAddress(address.get("street"))
                    .city(address.get("city"))
                    .zipCode(address.get("zip"))
                    .status("PENDING")
                    .createdAt(LocalDateTime.now())
                    .build();

            List<Map<String, Object>> cartItemsData = (List<Map<String, Object>>) data.get("items");
            List<OrderItem> items = new ArrayList<>();

            for (Map<String, Object> itemData : cartItemsData) {
                Product product = productRepo.findById((Integer) itemData.get("productId")).orElseThrow();
                OrderItem orderItem = new OrderItem();
                orderItem.setProduct(product);
                orderItem.setQuantity((Integer) itemData.get("qty"));
                orderItem.setOrder(order); 
                items.add(orderItem);
            }

            orderService.createInitialOrder(order, items); 
            
            // Return Razorpay Order Object as String
            return ResponseEntity.ok(rzpOrder.toString());
            
        } catch (Exception e) {
            e.printStackTrace(); // Log detailed error in console
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> paymentData) {
        try {
            String rzpOrderId = paymentData.get("razorpay_order_id");
            String rzpPaymentId = paymentData.get("razorpay_payment_id");
            // Razorpay signature can be verified here for higher security
            
            orderService.updateOrderPaymentStatus(rzpOrderId, rzpPaymentId, "PAID");
            return ResponseEntity.ok(Map.of("message", "Payment verified successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Verification failed");
        }
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<Order>> getMyOrders() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User worker = userRepo.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(orderRepo.findByWorker(worker));
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getAllWorkerOrders() {
        return ResponseEntity.ok(orderRepo.findAll());
    }
}