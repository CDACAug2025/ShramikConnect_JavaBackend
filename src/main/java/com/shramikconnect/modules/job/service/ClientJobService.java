package com.shramikconnect.modules.job.service;

import com.shramikconnect.entity.Job;
import com.shramikconnect.entity.User;
import com.shramikconnect.modules.job.dto.CreateJobRequest;
import com.shramikconnect.modules.job.dto.JobResponse;
import com.shramikconnect.modules.job.repository.ClientJobRepository;
import com.shramikconnect.modules.user.repository.UserRepository;
import com.shramikconnect.common.enums.District;
import com.shramikconnect.common.enums.JobStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientJobService {

    private final ClientJobRepository clientJobRepository;
    private final UserRepository userRepository;

    public JobResponse createJob(CreateJobRequest request, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = new Job();
        job.setTitle(request.getTitle());
        job.setCategory(request.getCategory());
        job.setDescription(request.getDescription());
        job.setBudget(request.getBudget());
        job.setDuration(request.getDuration());
        job.setLocation(request.getLocation());
        job.setDistrict(District.valueOf(request.getDistrict().toUpperCase()));
        job.setStatus(JobStatus.OPEN);
        job.setPostedBy(user);

        Job savedJob = clientJobRepository.save(job);
        return mapToResponse(savedJob);
    }

    public List<JobResponse> getJobsByClient(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Job> jobs = clientJobRepository.findByPostedByOrderByCreatedAtDesc(user);
        return jobs.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public void deleteJob(Integer jobId, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = clientJobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getPostedBy().equals(user)) {
            throw new RuntimeException("Unauthorized to delete this job");
        }

        clientJobRepository.delete(job);
    }

    public JobResponse updateJob(Integer jobId, CreateJobRequest request, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = clientJobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getPostedBy().equals(user)) {
            throw new RuntimeException("Unauthorized to update this job");
        }

        job.setTitle(request.getTitle());
        job.setCategory(request.getCategory());
        job.setDescription(request.getDescription());
        job.setBudget(request.getBudget());
        job.setDuration(request.getDuration());
        job.setLocation(request.getLocation());
        job.setDistrict(District.valueOf(request.getDistrict().toUpperCase()));

        Job updatedJob = clientJobRepository.save(job);
        return mapToResponse(updatedJob);
    }

    private JobResponse mapToResponse(Job job) {
        JobResponse response = new JobResponse();
        response.setJobId(job.getJobId());
        response.setTitle(job.getTitle());
        response.setCategory(job.getCategory());
        response.setDescription(job.getDescription());
        response.setBudget(job.getBudget());
        response.setDuration(job.getDuration());
        response.setLocation(job.getLocation());
        response.setDistrict(job.getDistrict().toString());
        response.setStatus(job.getStatus());
        response.setPostedBy(job.getPostedBy().getEmail());
        response.setCreatedAt(job.getCreatedAt());
        return response;
    }
}