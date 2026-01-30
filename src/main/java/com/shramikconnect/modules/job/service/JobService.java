package com.shramikconnect.modules.job.service; // ✅ Corrected Package

import com.shramikconnect.common.enums.JobStatus;
import com.shramikconnect.entity.Job;
import com.shramikconnect.entity.User;
import com.shramikconnect.modules.job.repository.JobRepository; // Using the renamed repo
import com.shramikconnect.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    /**
     * WORKER FEATURE: Job Discovery Feed
     * Fetches OPEN jobs filtered by the worker's district.
     */
    public List<Job> getJobFeedForWorker(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Worker not found"));

        // Fetch district from the workers table using the user_id
        String districtSql = "SELECT district FROM workers WHERE user_id = ?";
        String district = jdbcTemplate.queryForObject(districtSql, String.class, user.getUserId());

        return jobRepository.findByDistrictAndStatus(district, JobStatus.OPEN);
    }

    public List<Job> getJobsByClient(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return jobRepository.findByPostedByUserId(user.getUserId());
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Job getJobById(Integer jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));
    }
}