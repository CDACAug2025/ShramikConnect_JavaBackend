package com.shramikconnect.modules.payment.repository;

import com.shramikconnect.entity.EscrowPayment;
import com.shramikconnect.common.enums.PaymentStatus;
import com.shramikconnect.common.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationEscrowPaymentRepository
        extends JpaRepository<EscrowPayment, Integer> {

    List<EscrowPayment> findByContract_ContractId(Integer contractId);

    List<EscrowPayment> findByPaymentStatus(PaymentStatus paymentStatus);

    List<EscrowPayment> findByTransactionType(TransactionType transactionType);
}
