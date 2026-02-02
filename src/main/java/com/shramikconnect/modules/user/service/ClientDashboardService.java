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

        Integer userId = user.getUserId();

        ClientDashboardResponse response = new ClientDashboardResponse();

        // ---------- STATS ----------
        ClientDashboardResponse.DashboardStats stats =
                new ClientDashboardResponse.DashboardStats();

        stats.setActiveJobs(
                clientJobRepository.countByPostedByUserIdAndStatus(userId, JobStatus.OPEN)
        );

        stats.setCompletedJobs(
                clientJobRepository.countByPostedByUserIdAndStatus(userId, JobStatus.COMPLETED)
        );

        Double totalSpent =
                clientJobRepository.sumBudgetByPostedByUserIdAndStatus(
                        userId,
                        JobStatus.COMPLETED
                );

        stats.setTotalSpent(totalSpent != null ? totalSpent : 0.0);

        stats.setPendingApplications(
        		jobApplicationRepository.countByClientAndStatus(
        		        user.getUserId(),
        		        ApplicationStatus.APPLIED
        		)
        );

        response.setStats(stats);

        // ---------- RECENT JOBS ----------
        List<Job> recentJobsList =
                clientJobRepository.findTop5ByPostedByUserIdOrderByCreatedAtDesc(userId);

        List<ClientDashboardResponse.RecentJobResponse> recentJobs =
                recentJobsList.stream().map(job -> {

                    ClientDashboardResponse.RecentJobResponse jobResponse =
                            new ClientDashboardResponse.RecentJobResponse();

                    jobResponse.setJobId(job.getJobId());
                    jobResponse.setTitle(job.getTitle());
                    jobResponse.setStatus(job.getStatus().name());
                    jobResponse.setCreatedAt(job.getCreatedAt().toString());
                    jobResponse.setApplicationCount(
                            jobApplicationRepository.countByJobJobId(job.getJobId())
                    );

                    return jobResponse;
                }).collect(Collectors.toList());

        response.setRecentJobs(recentJobs);

        return response;
    }
}
