package com.shramikconnect.modules.job.service;

import com.shramikconnect.entity.Job;
import com.shramikconnect.modules.job.repository.OrganizationJobRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationJobService {

    private final OrganizationJobRepository jobRepository;

    public Job postJob(Job job) {
        return jobRepository.save(job);
    }
    
    public List<Job> getJobsByUserId(Integer userId) {
        return jobRepository.findByPostedByUserId(userId);
    }
    
    public void deleteJob(Integer jobId) {
        jobRepository.deleteById(jobId);
    }
    
    public Job updateJob(Integer jobId, Job jobData) {
        Job existingJob = jobRepository.findById(jobId)
            .orElseThrow(() -> new RuntimeException("Job not found"));
            
        existingJob.setTitle(jobData.getTitle());
        existingJob.setDescription(jobData.getDescription());
        existingJob.setCategory(jobData.getCategory());
        existingJob.setBudget(jobData.getBudget());
        existingJob.setLocation(jobData.getLocation());
        existingJob.setDistrict(jobData.getDistrict());
        
        return jobRepository.save(existingJob);
    }
}