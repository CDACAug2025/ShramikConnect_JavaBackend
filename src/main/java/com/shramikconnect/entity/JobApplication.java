package com.shramikconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import com.shramikconnect.common.enums.ApplicationStatus;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer applicationId;

    @ManyToOne
    private Job job;

    @ManyToOne
    private User applicant;

  

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    private LocalDateTime appliedAt = LocalDateTime.now();
}
