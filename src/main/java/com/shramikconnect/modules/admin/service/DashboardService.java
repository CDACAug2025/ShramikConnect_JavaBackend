package com.shramikconnect.modules.admin.service;

import com.shramikconnect.modules.admin.dto.DashboardStatsDTO;
import com.shramikconnect.modules.admin.entity.SystemLog;
import com.shramikconnect.modules.admin.repository.SystemLogRepository;
import com.shramikconnect.modules.user.entity.User;
import com.shramikconnect.modules.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SystemLogRepository logRepository;

    // 1. Aggregation API Logic
    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        // --- REAL DATA (From User Module) ---
        stats.setTotalUsers(userRepository.count());
        stats.setActiveUsers(userRepository.findByStatus(User.Status.ACTIVE).size());

        // --- MOCK DATA (Since Job/Payment modules aren't built yet) ---
        stats.setTotalJobs(450);
        stats.setOngoingJobs(35);
        stats.setCompletedJobs(405);
        stats.setTotalRevenue(540000.00);

        // --- SYSTEM HEALTH ---
        long uptimeMillis = ManagementFactory.getRuntimeMXBean().getUptime();
        long uptimeHours = Duration.ofMillis(uptimeMillis).toHours();
        stats.setUptime(uptimeHours + " Hours");
        stats.setStatus("Healthy");

        return stats;
    }

    // 2. Fetch Logs
    public List<SystemLog> getSystemLogs() {
        return logRepository.findTop10ByOrderByTimestampDesc();
    }

    // 3. Simulated Scheduled Job (Runs every 10 seconds to generate a log)
    // In a real app, this would run nightly to calculate complex stats
    @Scheduled(fixedRate = 60000) 
    public void performSystemHealthCheck() {
        // Simulating a system check
        long memoryUsed = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        if (memoryUsed > 1000000000) { // If using too much RAM (Fake check)
            logRepository.save(new SystemLog("WARNING", "System", "High Memory Usage Detected"));
        } else {
            // Uncomment below to see logs appearing automatically
            // logRepository.save(new SystemLog("INFO", "System", "Health Check Passed"));
        }
    }
}