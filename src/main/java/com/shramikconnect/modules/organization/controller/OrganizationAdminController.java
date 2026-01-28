package com.shramikconnect.modules.organization.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organization/admin")
@RequiredArgsConstructor
public class OrganizationAdminController {

    @GetMapping("/dashboard")
    public String getDashboard() {
        return "Organization Admin Dashboard";
    }

    @GetMapping("/settings")
    public String getSettings() {
        return "Organization Admin Settings";
    }
}