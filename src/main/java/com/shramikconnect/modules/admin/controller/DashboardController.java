package com.shramikconnect.modules.admin.controller;

import com.shramikconnect.modules.admin.dto.DashboardStatsDTO;
import com.shramikconnect.entity.SystemLog;
import com.shramikconnect.modules.admin.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@Tag(name = "Platform Monitoring", description = "System Health and Stats Dashboard")
@CrossOrigin("*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats")
    @Operation(summary = "Get Dashboard Counters", description = "Returns aggregated stats for Users, Jobs, and Revenue")
    public ResponseEntity<DashboardStatsDTO> getStats() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }

//    @GetMapping("/logs")
//    @Operation(summary = "Get System Logs", description = "Fetch the latest 10 system error/warning logs")
//    public ResponseEntity<List<SystemLog>> getLogs() {
//        return ResponseEntity.ok(dashboardService.getSystemLogs());
//    }
}