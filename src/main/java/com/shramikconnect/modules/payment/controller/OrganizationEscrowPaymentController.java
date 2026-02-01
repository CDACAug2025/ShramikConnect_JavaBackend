package com.shramikconnect.modules.payment.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shramikconnect.common.enums.PaymentStatus;
import com.shramikconnect.common.enums.TransactionType;
import com.shramikconnect.entity.EscrowPayment;
import com.shramikconnect.modules.payment.dto.OrganizationEscrowPaymentResponse;
import com.shramikconnect.modules.payment.service.OrganizationEscrowPaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/organization/payments")
@RequiredArgsConstructor
public class OrganizationEscrowPaymentController {

    private final OrganizationEscrowPaymentService paymentService;

    @GetMapping
    public List<OrganizationEscrowPaymentResponse> getAllPayments() {
        return paymentService.getAllPayments()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{escrowId}")
    public OrganizationEscrowPaymentResponse getPaymentById(
            @PathVariable Integer escrowId) {
        return mapToResponse(paymentService.getPaymentById(escrowId));
    }

    @GetMapping("/contract/{contractId}")
    public List<OrganizationEscrowPaymentResponse> getByContract(
            @PathVariable Integer contractId) {
        return paymentService.getPaymentsByContract(contractId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/status/{status}")
    public List<OrganizationEscrowPaymentResponse> getByStatus(
            @PathVariable PaymentStatus status) {
        return paymentService.getPaymentsByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/type/{type}")
    public List<OrganizationEscrowPaymentResponse> getByType(
            @PathVariable TransactionType type) {
        return paymentService.getPaymentsByTransactionType(type)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private OrganizationEscrowPaymentResponse mapToResponse(EscrowPayment payment) {
        return new OrganizationEscrowPaymentResponse(
                payment.getEscrowId(),
                payment.getContract().getContractId(),
                payment.getAmount(),
                payment.getPaymentStatus(),
                payment.getTransactionType(),
                payment.getTransactionDate()
        );
    }
    @PostMapping("/verify-and-update/{escrowId}")
    public ResponseEntity<?> verifyPayment(
            @PathVariable Integer escrowId,
            @RequestBody Map<String, String> paymentData) {
        
        // ✅ Uses the Organization Service to UPDATE the specific row
    	boolean success = paymentService.verifyAndRelease(
    		    paymentData.get("razorpay_order_id"),
    		    paymentData.get("razorpay_payment_id"),
    		    paymentData.get("razorpay_signature"),
    		    escrowId 
    		);

        if (success) {
            return ResponseEntity.ok(mapToResponse(paymentService.getPaymentById(escrowId)));
        }
        return ResponseEntity.status(400).body("Payment Verification Failed");
    }
}
