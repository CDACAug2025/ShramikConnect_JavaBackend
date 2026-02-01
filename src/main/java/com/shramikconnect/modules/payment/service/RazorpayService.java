package com.shramikconnect.modules.payment.service;

import java.time.LocalDateTime; // ✅ CRITICAL: Added missing import
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import com.shramikconnect.common.enums.PaymentStatus;
import com.shramikconnect.common.enums.TransactionType;
import com.shramikconnect.entity.Contract;
import com.shramikconnect.entity.EscrowPayment;
import com.shramikconnect.modules.payment.repository.OrganizationEscrowPaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RazorpayService {
    private final RazorpayClient client;
    private final OrganizationEscrowPaymentRepository repository;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Transactional
    public String createOrder(double amount, Integer contractId) throws Exception {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int) (amount * 100)); 
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "contract_" + contractId);

        
        Order order = client.orders.create(orderRequest);
        return order.get("id");
    }

    @Transactional
    public boolean verifyAndRelease(String orderId, String paymentId, String signature) {
        try {
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", orderId);
            attributes.put("razorpay_payment_id", paymentId);
            attributes.put("razorpay_signature", signature);

            // ✅ Verify signature
            if (Utils.verifyPaymentSignature(attributes, keySecret)) {
                updatePaymentToEscrowHeld(orderId);
                return true;
            }
        } catch (Exception e) { return false; }
        return false;
    }

    private void updatePaymentToEscrowHeld(String orderId) {
        // ✅ CRITICAL FIX: Find the EXISTING record by its unique Razorpay Order ID
        repository.findByRazorpayOrderId(orderId).ifPresentOrElse(p -> {
            p.setPaymentStatus(PaymentStatus.ESCROW_HELD); // Change from PENDING to SECURED
            repository.save(p); // ✅ Correctly performs an UPDATE in the database
            System.out.println("✅ Status updated to ESCROW_HELD for Order ID: " + orderId);
        }, () -> {
            // Log if the order ID doesn't exist to prevent a null pointer or crash
            System.err.println("❌ ERROR: Order ID " + orderId + " not found in database.");
        });
    }

    @Transactional
    public void releaseToWorker(Integer contractId) {
        List<EscrowPayment> payments = repository.findByContract_ContractId(contractId);
        payments.stream()
            .filter(p -> p.getPaymentStatus() == PaymentStatus.ESCROW_HELD)
            .forEach(p -> {
                p.setPaymentStatus(PaymentStatus.RELEASED); 
                p.setTransactionType(TransactionType.RELEASE);
                repository.save(p);
            });
    }

    public List<EscrowPayment> getAllEscrowRecords() {
        return repository.findAll();
    }

    public List<EscrowPayment> getPaymentsByWorker(Integer workerId) {
        return repository.findByContract_Worker_UserId(workerId);
    }
    @Transactional
    public boolean processWebhookEvent(String payload) {
        try {
            JSONObject json = new JSONObject(payload);
            // Extracts the Razorpay Order ID from the webhook payload
            String orderId = json.getJSONObject("payload")
                                 .getJSONObject("payment")
                                 .getJSONObject("entity")
                                 .getString("order_id");
                                 
            // Updates the database status to ESCROW_HELD upon successful capture
            updatePaymentToEscrowHeld(orderId);
            return true;
        } catch (Exception e) {
            return false;
        }
        
    }
    public List<EscrowPayment> getPaymentsByClient(Integer clientId) {
        return repository.findByContract_Client_UserId(clientId);
    }
}