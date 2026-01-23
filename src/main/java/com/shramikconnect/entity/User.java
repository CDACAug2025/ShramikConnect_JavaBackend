package com.shramikconnect.entity;

import com.shramikconnect.entity.Role;
import com.shramikconnect.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    private String fullName;
    private String email;
    private String phone;
    private String passwordHash;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    private LocalDateTime createdAt = LocalDateTime.now();
}
