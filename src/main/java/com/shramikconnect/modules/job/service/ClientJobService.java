package com.shramikconnect.modules.job.service;

import com.shramikconnect.entity.Job;
import com.shramikconnect.entity.User;
import com.shramikconnect.modules.job.dto.CreateJobRequest;
import com.shramikconnect.modules.job.dto.JobResponse;
import com.shramikconnect.modules.job.repository.ClientJobRepository;
import com.shramikconnect.modules.user.repository.UserRepository;
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

        Job job = Job.builder()
                .title(request.getTitle())
                .category(request.getCategory())
                .description(request.getDescription())
                .budget(request.getBudget())
                .location(request.getLocation())
                .district(request.getDistrict())
                .status(JobStatus.OPEN)
                .postedByUserId(user.getUserId())
                .build();

        return map(clientJobRepository.save(job));
    }

    public List<JobResponse> getJobsByClient(String username) {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return clientJobRepository
                .findByPostedByUserIdOrderByCreatedAtDesc(user.getUserId())
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    public void deleteJob(Integer jobId, String username) {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = clientJobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getPostedByUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized");
        }

        clientJobRepository.delete(job);
    }

    public JobResponse updateJob(Integer jobId, CreateJobRequest request, String username) {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = clientJobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getPostedByUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized");
        }

        job.setTitle(request.getTitle());
        job.setCategory(request.getCategory());
        job.setDescription(request.getDescription());
        job.setBudget(request.getBudget());
        job.setLocation(request.getLocation());
        job.setDistrict(request.getDistrict());

        return map(clientJobRepository.save(job));
    }

    private JobResponse map(Job job) {

        JobResponse response = new JobResponse();
        response.setJobId(job.getJobId());
        response.setTitle(job.getTitle());
        response.setCategory(job.getCategory());
        response.setDescription(job.getDescription());
        response.setBudget(job.getBudget());
        response.setLocation(job.getLocation());
        response.setDistrict(job.getDistrict());
        response.setStatus(job.getStatus());
        response.setPostedByUserId(job.getPostedByUserId());
        response.setCreatedAt(job.getCreatedAt());

        return response;
    }
}
