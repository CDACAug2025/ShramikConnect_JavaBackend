package com.shramikconnect.modules.admin.controller;

import com.shramikconnect.entity.Role;
import com.shramikconnect.entity.User;
import com.shramikconnect.common.enums.UserStatus;
import com.shramikconnect.modules.admin.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin("*")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer roleId,
            @RequestParam(required = false) UserStatus status) {

        return ResponseEntity.ok(
                adminService.getAllUsers(search, roleId, status)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<User> updateUserStatus(
            @PathVariable Integer id,
            @RequestParam UserStatus status) {

        return ResponseEntity.ok(adminService.updateUserStatus(id, status));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<User> updateUserRole(
            @PathVariable Integer id,
            @RequestParam Integer roleId) {

        Role role = new Role();
        role.setRoleId(roleId);
        return ResponseEntity.ok(adminService.updateUserRole(id, role));
    }
}
