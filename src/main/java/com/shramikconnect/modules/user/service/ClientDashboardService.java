package com.shramikconnect.modules.user.service;

import com.shramikconnect.entity.User;
import com.shramikconnect.entity.Job;
import com.shramikconnect.modules.job.repository.ClientJobRepository;
import com.shramikconnect.modules.job.repository.JobApplicationRepository;
import com.shramikconnect.modules.user.dto.ClientDashboardResponse;
import com.shramikconnect.modules.user.repository.UserRepository;
import com.shramikconnect.common.enums.JobStatus;
import com.shramikconnect.common.enums.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientDashboardService {

    private final ClientJobRepository clientJobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;

    public ClientDashboardResponse getDashboardData(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ClientDashboardResponse response = new ClientDashboardResponse();
        
        // Get stats
        ClientDashboardResponse.DashboardStats stats = new ClientDashboardResponse.DashboardStats();
        stats.setActiveJobs(clientJobRepository.countByPostedByAndStatus(user, JobStatus.OPEN));
        stats.setCompletedJobs(clientJobRepository.countByPostedByAndStatus(user, JobStatus.COMPLETED));
        Double totalSpent = clientJobRepository.sumBudgetByPostedByAndStatus(user, JobStatus.COMPLETED);
        stats.setTotalSpent(totalSpent != null ? totalSpent : 0.0);
        stats.setPendingApplications(jobApplicationRepository.countByJob_PostedByAndStatus(user, ApplicationStatus.APPLIED));
        
        response.setStats(stats);
        
        // Get recent jobs
        List<Job> recentJobsList = clientJobRepository.findTop5ByPostedByOrderByCreatedAtDesc(user);
        List<ClientDashboardResponse.RecentJobResponse> recentJobs = recentJobsList
                .stream()
                .map(job -> {
                    ClientDashboardResponse.RecentJobResponse jobResponse = new ClientDashboardResponse.RecentJobResponse();
                    jobResponse.setJobId(job.getJobId());
                    jobResponse.setTitle(job.getTitle());
                    jobResponse.setStatus(job.getStatus().toString());
                    jobResponse.setCreatedAt(job.getCreatedAt().toString());
                    jobResponse.setApplicationCount(jobApplicationRepository.countByJob(job));
                    return jobResponse;
                })
                .collect(Collectors.toList());
        
        response.setRecentJobs(recentJobs);
        
        return response;
    }
}