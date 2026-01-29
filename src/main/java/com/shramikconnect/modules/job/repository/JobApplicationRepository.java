package com.shramikconnect.modules.job.repository;

import com.shramikconnect.entity.JobApplication;
import com.shramikconnect.common.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Integer> {

    List<JobApplication> findByJobJobIdIn(List<Integer> jobIds);

    long countByJobJobId(Integer jobId);

    @Query("""
        SELECT COUNT(ja)
        FROM JobApplication ja
        WHERE ja.jobJobId IN (
            SELECT j.jobId
            FROM Job j
            WHERE j.postedByUserId = :userId
        )
        AND ja.status = :status
    """)
    long countByClientJobsAndStatus(
            @Param("userId") Integer userId,
            @Param("status") ApplicationStatus status
    );
}
