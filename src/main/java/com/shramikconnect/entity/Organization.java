package com.shramikconnect.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "organizations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orgId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String orgName;
    private String gstNumber;
    private String address;
}
