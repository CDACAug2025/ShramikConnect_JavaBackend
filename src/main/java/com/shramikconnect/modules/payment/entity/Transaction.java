package com.shramikconnect.modules.payment.entity;

import com.shramikconnect.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Who paid the money? (Client)
    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    // Who is receiving the money? (Worker or System)
    @ManyToOne
    @JoinColumn(name = "worker_id")
    private User worker;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentType type; // Escrow, Subscription, Store Order

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // Held, Released, Failed, Completed

    private LocalDate date;

    // --- ENUMS ---
    public enum PaymentType {
        ESCROW_DEPOSIT, SUBSCRIPTION, STORE_ORDER, REFUND, MILESTONE_RELEASE
    }

    public enum PaymentStatus {
        HELD_IN_ESCROW, RELEASED, COMPLETED, FAILED, REFUNDED
    }
}