package com.shramikconnect.modules.ecommerce.entity;

import com.shramikconnect.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to the User (Worker) who bought it
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User worker;

    private String productName; // Storing name directly for simplicity in history
    private Integer quantity;
    private Double totalPrice;
    private String status; // "Delivered", "Processing", "Shipped"
    private LocalDate date;
}