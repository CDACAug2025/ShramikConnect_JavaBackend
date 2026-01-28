package com.shramikconnect.modules.job.repository;

import com.shramikconnect.entity.JobApplication;
import com.shramikconnect.entity.Job;
import com.shramikconnect.entity.User;
import com.shramikconnect.common.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Integer> {
    
    List<JobApplication> findByJobOrderByAppliedAtDesc(Job job);
    
    List<JobApplication> findByJob_PostedBy_UserIdOrderByAppliedAtDesc(Integer userId);
    
    long countByJob_PostedByAndStatus(User postedBy, ApplicationStatus status);
    
    long countByJob(Job job);
}