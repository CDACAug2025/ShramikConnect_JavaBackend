package com.shramikconnect.modules.job.repository;

import com.shramikconnect.entity.Job;
import com.shramikconnect.common.enums.JobStatus; // ✅ Import the Enum
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    // ✅ Change 'String' to 'JobStatus'
    long countByStatus(JobStatus status);
}