package com.shramikconnect.modules.configsys.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "announcements")
@Data
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    
    @Column(length = 1000)
    private String message;
    
    private String targetAudience; // "ALL", "WORKERS", "CLIENTS"
    private boolean isActive;
    
    private LocalDateTime createdAt;

    public Announcement() {
        this.createdAt = LocalDateTime.now();
    }
}