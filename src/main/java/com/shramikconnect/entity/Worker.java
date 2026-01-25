package com.shramikconnect.entity;

import com.shramikconnect.common.enums.District;

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
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private District district;

}
