package com.shramikconnect.modules.admin.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_logs")
@Data
public class SystemLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String level; // ERROR, WARNING, INFO
    private String module; // Payment, User, System
    private String message;
    
    private LocalDateTime timestamp;

    // specific constructor for easy logging
    public SystemLog(String level, String module, String message) {
        this.level = level;
        this.module = module;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
    
    public SystemLog() {} // Default constructor
}