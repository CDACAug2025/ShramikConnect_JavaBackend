package com.shramikconnect.modules.payment.repository;

import com.shramikconnect.modules.payment.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    // Filters (Useful for backend filtering logic)
    List<Transaction> findByStatus(Transaction.PaymentStatus status);
    
    // Count specific statuses for Dashboard Stats
    long countByStatus(Transaction.PaymentStatus status);
}