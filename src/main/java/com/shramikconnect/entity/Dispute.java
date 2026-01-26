package com.shramikconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import com.shramikconnect.common.enums.DisputeStatus;

@Entity
@Table(name = "disputes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Dispute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer disputeId;

    @ManyToOne
    private Contract contract;

    @ManyToOne
    private User raisedBy;

    private String reason;
   

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DisputeStatus status;


    @ManyToOne
    private User resolvedBy;
}
