package com.shramikconnect.modules.admin.controller;

import com.shramikconnect.modules.admin.service.AdminUserService;
import com.shramikconnect.modules.user.entity.User;
import com.shramikconnect.modules.user.entity.User.Role;
import com.shramikconnect.modules.user.entity.User.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "Admin User Management", description = "APIs for managing users, roles, and statuses")
@CrossOrigin("*") // Allow frontend access
public class AdminUserController {

    @Autowired
    private AdminUserService adminService;

    @GetMapping
    @Operation(summary = "Fetch Users", description = "Get all users or filter by search, role, and status")
    public ResponseEntity<List<User>> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Status status) {
        
        return ResponseEntity.ok(adminService.getAllUsers(search, role, status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get User Profile", description = "Fetch complete details of a single user")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Block/Activate User", description = "Update user status (ACTIVE, BLOCKED, INACTIVE)")
    public ResponseEntity<User> updateUserStatus(@PathVariable Long id, @RequestParam Status status) {
        return ResponseEntity.ok(adminService.updateUserStatus(id, status));
    }

    @PatchMapping("/{id}/role")
    @Operation(summary = "Change User Role", description = "Assign a new role to a user")
    public ResponseEntity<User> updateUserRole(@PathVariable Long id, @RequestParam Role role) {
        return ResponseEntity.ok(adminService.updateUserRole(id, role));
    }
}