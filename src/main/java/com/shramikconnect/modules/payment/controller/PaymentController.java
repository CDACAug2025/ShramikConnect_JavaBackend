package com.shramikconnect.modules.payment.controller;

import com.shramikconnect.modules.payment.entity.Transaction;
import com.shramikconnect.modules.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/payments")
@Tag(name = "Payment & Financial Oversight", description = "Manage Escrow, Subscriptions, and Reports")
@CrossOrigin("*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    @Operation(summary = "View All Transactions", description = "List all payments (Escrow, Store, Subs)")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(paymentService.getAllTransactions());
    }

    @GetMapping("/stats")
    @Operation(summary = "Financial Stats", description = "Get totals for Held vs Released funds")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(paymentService.getFinancialStats());
    }

    @PatchMapping("/{id}/release")
    @Operation(summary = "Release Escrow", description = "Admin manually releases held funds to the worker")
    public ResponseEntity<Transaction> releasePayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.releasePayment(id));
    }
    
    @GetMapping("/report")
    @Operation(summary = "Download Report", description = "Mock endpoint to download financial CSV/PDF")
    public ResponseEntity<String> downloadReport() {
        return ResponseEntity.ok("Report generation started... Check email for link.");
    }
}