package com.shramikconnect.modules.payment.service;

import com.shramikconnect.entity.EscrowPayment;
import com.shramikconnect.common.enums.PaymentStatus;
import com.shramikconnect.common.enums.TransactionType;
import com.shramikconnect.modules.payment.repository.OrganizationEscrowPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationEscrowPaymentServiceImpl
        implements OrganizationEscrowPaymentService {

    private final OrganizationEscrowPaymentRepository repository;

    @Override
    public List<EscrowPayment> getAllPayments() {
        return repository.findAll();
    }

    @Override
    public EscrowPayment getPaymentById(Integer escrowId) {
        return repository.findById(escrowId)
                .orElseThrow(() ->
                        new RuntimeException("Escrow Payment not found"));
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
