package com.shramikconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import com.shramikconnect.common.enums.JobStatus;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor 
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer jobId;

    @ManyToOne
    private User postedBy;

    private String title;
    private String description;
    private String category;
    private String location;
    private Double budget;
    

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;


    private LocalDateTime createdAt = LocalDateTime.now();
}
