package com.shramikconnect.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "workers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer workerId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String skillSet;
    private Integer experienceYears;
    private String location;
    private Double rating;
}
