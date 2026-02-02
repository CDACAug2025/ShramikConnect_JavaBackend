package com.shramikconnect.modules.job.repository;

import com.shramikconnect.entity.Job;
import com.shramikconnect.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationJobApplicationRepository
        extends JpaRepository<JobApplication, Integer> {

    List<JobApplication> findByJobIn(List<Job> jobs);
}
