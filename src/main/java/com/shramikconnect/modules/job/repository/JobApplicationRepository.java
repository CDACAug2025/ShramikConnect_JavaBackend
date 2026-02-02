package com.shramikconnect.modules.job.repository;

import com.shramikconnect.entity.Job;
import com.shramikconnect.entity.JobApplication;
import com.shramikconnect.entity.User;
import com.shramikconnect.common.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Integer> {

    // ✅ Organization → Applications for my jobs
    @Query("""
        SELECT ja
        FROM JobApplication ja
        WHERE ja.job.postedByUserId = :orgId
    """)
    List<JobApplication> findByOrganizationJobs(
            @Param("orgId") Integer orgId
    );

    // ✅ FIXED: Applications applied by a worker
    // ❌ OLD: ja.applicant.userId
    // ✅ NEW: ja.worker.userId
    @Query("""
        SELECT ja
        FROM JobApplication ja
        WHERE ja.worker.userId = :userId
    """)
    List<JobApplication> findByWorkerUserId(
            @Param("userId") Integer userId
    );
    
    @Query("""
    	    SELECT ja
    	    FROM JobApplication ja
    	    JOIN FETCH ja.job j
    	    WHERE j.postedByUserId = :clientId
    	""")
    	List<JobApplication> findClientApplicationsWithJob(
    	        @Param("clientId") Integer clientId
    	);


    long countByJobJobId(Integer jobId);

    // ✅ Count applications for jobs posted by a client
    @Query("""
        SELECT COUNT(ja)
        FROM JobApplication ja
        WHERE ja.job.postedByUserId = :userId
          AND ja.status = :status
    """)
    long countByClientAndStatus(
            @Param("userId") Integer userId,
            @Param("status") ApplicationStatus status
    );

    // ✅ Already correct
    List<JobApplication> findByWorker(User worker);

    List<JobApplication> findByJobIn(List<Job> jobs);
}
