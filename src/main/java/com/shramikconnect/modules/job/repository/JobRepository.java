package com.shramikconnect.modules.job.repository;

import com.shramikconnect.common.enums.JobStatus;
import com.shramikconnect.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Integer> {
    
    // For Client Dashboard: Get jobs posted by a specific user
    List<Job> findByPostedByUserId(Integer userId);

    // For Worker Feed: Find nearby jobs by district and status
    List<Job> findByDistrictAndStatus(String district, JobStatus status);

    // For Filtered Feed: Find jobs by district and category
    List<Job> findByDistrictAndCategoryAndStatus(String district, String category, JobStatus status);
}