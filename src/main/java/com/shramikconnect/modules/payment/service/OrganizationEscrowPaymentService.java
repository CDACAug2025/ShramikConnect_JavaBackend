package com.shramikconnect.modules.payment.service;

import com.shramikconnect.entity.EscrowPayment;
import com.shramikconnect.common.enums.PaymentStatus;
import com.shramikconnect.common.enums.TransactionType;

import java.util.List;

public interface OrganizationEscrowPaymentService {

    List<EscrowPayment> getAllPayments();

    EscrowPayment getPaymentById(Integer escrowId);

    List<EscrowPayment> getPaymentsByContract(Integer contractId);

    List<EscrowPayment> getPaymentsByStatus(PaymentStatus status);

    List<EscrowPayment> getPaymentsByTransactionType(TransactionType type);
}
