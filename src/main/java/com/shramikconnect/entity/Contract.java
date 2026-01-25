package com.shramikconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import com.shramikconnect.common.enums.ContractStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contracts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer contractId;

    @OneToOne
    private Job job;

    @ManyToOne
    private User worker;

    @ManyToOne
    private User client;

    private String contractTerms;
    private Double agreedAmount;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractStatus status;


    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime signedAt;
}
