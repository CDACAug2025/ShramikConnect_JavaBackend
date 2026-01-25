package com.shramikconnect.modules.supervisor.controller;

import com.shramikconnect.modules.supervisor.dto.SupervisorDashboardDto;
import com.shramikconnect.modules.supervisor.service.SupervisorDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/supervisor/dashboard")
@RequiredArgsConstructor
public class SupervisorDashboardController {

    private final SupervisorDashboardService dashboardService;

    @GetMapping
    public SupervisorDashboardDto getDashboard() {
        return dashboardService.getDashboard();
    }
}
