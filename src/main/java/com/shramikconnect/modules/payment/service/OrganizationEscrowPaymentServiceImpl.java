package com.shramikconnect.modules.payment.service;

import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import com.shramikconnect.entity.EscrowPayment;
import com.shramikconnect.common.enums.PaymentStatus;
import com.shramikconnect.common.enums.TransactionType;
import com.shramikconnect.modules.payment.repository.OrganizationEscrowPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationEscrowPaymentServiceImpl implements OrganizationEscrowPaymentService {

	private final OrganizationEscrowPaymentRepository repository;

	@Value("${razorpay.key.secret}")
	private String keySecret;

	@Override
	@Transactional
	public boolean verifyAndRelease(String orderId, String paymentId, String signature, Integer escrowId) {
	    try {
	        JSONObject attributes = new JSONObject();
	        attributes.put("razorpay_order_id", orderId);
	        attributes.put("razorpay_payment_id", paymentId);
	        attributes.put("razorpay_signature", signature);

	        // ✅ Step 1: Verify the signature using backend secret
	        if (Utils.verifyPaymentSignature(attributes, keySecret)) {
	            
	            // ✅ Step 2: Use .findById() to UPDATE the specific row
	            return repository.findById(escrowId).map(p -> {
	                p.setPaymentStatus(PaymentStatus.ESCROW_HELD); // Change PENDING to SECURED
	                p.setRazorpayOrderId(orderId); // Link real transaction ID
	                repository.save(p); // Commit update
	                System.out.println("✅ Success: Escrow ID " + escrowId + " updated to SECURED.");
	                return true;
	            }).orElse(false);
	        }
	    } catch (Exception e) {
	        System.err.println("Verification Error: " + e.getMessage());
	    }
	    return false;
	}

	@Override
	public List<EscrowPayment> getAllPayments() {
		return repository.findAll();
	}

	@Override
	public EscrowPayment getPaymentById(Integer escrowId) {
		return repository.findById(escrowId).orElseThrow(() -> new RuntimeException("Not Found"));
	}

	@Override
	public List<EscrowPayment> getPaymentsByContract(Integer contractId) {
		return repository.findByContract_ContractId(contractId);
	}

	@Override
	public List<EscrowPayment> getPaymentsByStatus(PaymentStatus status) {
		return repository.findByPaymentStatus(status);
	}

	@Override
	public List<EscrowPayment> getPaymentsByTransactionType(TransactionType type) {
		return repository.findByTransactionType(type);
	}
}