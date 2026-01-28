package com.shramikconnect.modules.payment.service;

import com.shramikconnect.modules.payment.entity.Transaction;
import com.shramikconnect.modules.payment.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentService {

    @Autowired
    private TransactionRepository transactionRepository;

    // 1. Fetch All Transactions
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // 2. Release Escrow (Admin Action)
    public Transaction releasePayment(Long txnId) {
        Transaction txn = transactionRepository.findById(txnId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // Only allow releasing if it is currently HELD
        if (txn.getStatus() == Transaction.PaymentStatus.HELD_IN_ESCROW) {
            txn.setStatus(Transaction.PaymentStatus.RELEASED);
            // In a real app, you would trigger the Bank API transfer here
            return transactionRepository.save(txn);
        } else {
            throw new RuntimeException("Transaction is not held in escrow.");
        }
    }

    // 3. Generate Financial Stats (For the Dashboard Cards)
    public Map<String, Object> getFinancialStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Calculate Total Held
        double totalHeld = transactionRepository.findByStatus(Transaction.PaymentStatus.HELD_IN_ESCROW)
                .stream().mapToDouble(Transaction::getAmount).sum();
                
        // Calculate Total Released/Completed
        double totalReleased = transactionRepository.findAll().stream()
                .filter(t -> t.getStatus() == Transaction.PaymentStatus.RELEASED || t.getStatus() == Transaction.PaymentStatus.COMPLETED)
                .mapToDouble(Transaction::getAmount).sum();
                
        // Count Failures
        long failedCount = transactionRepository.countByStatus(Transaction.PaymentStatus.FAILED);

        stats.put("totalHeld", totalHeld);
        stats.put("totalReleased", totalReleased);
        stats.put("failedCount", failedCount);
        
        return stats;
    }
}