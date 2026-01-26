package com.shramikconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import com.shramikconnect.common.enums.KycStatus;
import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_documents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KycDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer kycId;

    @ManyToOne
    private User user;

    private String documentType;
    private String documentNumber;
    

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KycStatus status;


    @ManyToOne
    private User verifiedBy;

    private LocalDateTime verifiedAt;
}
