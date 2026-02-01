package com.shramikconnect.modules.payment.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.shramikconnect.modules.payment.service.RazorpayService;

@RestController
@RequestMapping("/api/payments")
public class PaymentOrderController {

    @Autowired
    private RazorpayService razorpayService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> data, Authentication auth) {
        try {
            double amount = Double.parseDouble(data.get("amount").toString());
            Integer contractId = Integer.parseInt(data.get("contractId").toString());
            String orderId = razorpayService.createOrder(amount, contractId);
            return ResponseEntity.ok(Map.of("orderId", orderId));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Order creation failed: " + e.getMessage());
        }
    }

    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> data) {
        String orderId = data.get("razorpay_order_id");
        String paymentId = data.get("razorpay_payment_id");
        String signature = data.get("razorpay_signature");

        boolean isValid = razorpayService.verifyAndRelease(orderId, paymentId, signature);
        if (isValid) {
            return ResponseEntity.ok(Map.of("status", "success", "message", "Payment Verified"));
        } else {
            return ResponseEntity.status(400).body(Map.of("status", "failure", "message", "Invalid Signature"));
        }
    }

    @PostMapping("/release/{contractId}")
    public ResponseEntity<?> releaseFunds(@PathVariable Integer contractId) {
        try {
            razorpayService.releaseToWorker(contractId);
            return ResponseEntity.ok(Map.of("message", "Funds released successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Release failed: " + e.getMessage());
        }
    }

    // ✅ NEW: Organization specific history
    @GetMapping("/client-history/{clientId}")
    public ResponseEntity<?> getClientPayments(@PathVariable Integer clientId) {
        return ResponseEntity.ok(razorpayService.getPaymentsByClient(clientId));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllPayments() {
        return ResponseEntity.ok(razorpayService.getAllEscrowRecords());
    }
}