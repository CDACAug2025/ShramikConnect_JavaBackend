package com.shramikconnect.modules.subscription.entity;

import com.shramikconnect.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "subscriptions")
@Data
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;

    private LocalDate startDate;
    private LocalDate endDate;
    
    private String status; // "Active", "Expired"

    // Helper to check if valid
    public boolean isValid() {
        return LocalDate.now().isBefore(endDate) || LocalDate.now().equals(endDate);
    }
}