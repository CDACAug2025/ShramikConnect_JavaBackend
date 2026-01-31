package com.shramikconnect.modules.job.repository;


import com.shramikconnect.entity.Job;
import com.shramikconnect.common.enums.JobStatus; // ✅ Import the Enum

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    // ✅ Change 'String' to 'JobStatus'
    long countByStatus(JobStatus status);
 // For Client Dashboard: Get jobs posted by a specific user
    List<Job> findByPostedByUserId(Integer userId);

    // For Worker Feed: Find nearby jobs by district and status
    List<Job> findByDistrictAndStatus(String district, JobStatus status);

    // For Filtered Feed: Find jobs by district and category
    List<Job> findByDistrictAndCategoryAndStatus(String district, String category, JobStatus status);
//	Optional<Job> findById(Integer jobId);

}