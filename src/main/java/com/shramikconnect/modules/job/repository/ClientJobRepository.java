package com.shramikconnect.modules.job.repository;

import com.shramikconnect.entity.Job;
import com.shramikconnect.entity.User;
import com.shramikconnect.common.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientJobRepository extends JpaRepository<Job, Integer> {
    
    List<Job> findByPostedByOrderByCreatedAtDesc(User postedBy);
    
    List<Job> findByPostedBy(User postedBy);
    
    long countByPostedByAndStatus(User postedBy, JobStatus status);
    
    @Query("SELECT COALESCE(SUM(j.budget), 0) FROM Job j WHERE j.postedBy = :postedBy AND j.status = :status")
    Double sumBudgetByPostedByAndStatus(@Param("postedBy") User postedBy, @Param("status") JobStatus status);
    
    List<Job> findTop5ByPostedByOrderByCreatedAtDesc(User postedBy);
}