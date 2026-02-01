package com.shramikconnect.modules.payment.repository;

import com.shramikconnect.entity.EscrowPayment;
import com.shramikconnect.common.enums.PaymentStatus;
import com.shramikconnect.common.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationEscrowPaymentRepository extends JpaRepository<EscrowPayment, Integer> {

    Optional<EscrowPayment> findByRazorpayOrderId(String razorpayOrderId);

    List<EscrowPayment> findByContract_ContractId(Integer contractId);

    List<EscrowPayment> findByPaymentStatus(PaymentStatus paymentStatus);

    List<EscrowPayment> findByTransactionType(TransactionType transactionType);

    // ✅ Matches the logic in RazorpayService for Worker dashboards
    List<EscrowPayment> findByContract_Client_UserId(Integer clientId);
    List<EscrowPayment> findByContract_Worker_UserId(Integer userId);
}