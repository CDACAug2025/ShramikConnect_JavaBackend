package com.shramikconnect.entity;

import com.shramikconnect.common.enums.JobStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer jobId;

    private String title;
    private String description;
    private String category;
    private Double budget;

    private String location;
    private String district;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status = JobStatus.OPEN;

    @Column(name = "posted_by_user_id")
    private Integer postedByUserId;

    private LocalDateTime createdAt = LocalDateTime.now();
}
