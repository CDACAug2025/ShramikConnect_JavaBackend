package com.shramikconnect.modules.admin.service;

import com.shramikconnect.modules.user.entity.User;
import com.shramikconnect.modules.user.entity.User.Role;
import com.shramikconnect.modules.user.entity.User.Status;
import com.shramikconnect.modules.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminUserService {

    @Autowired
    private UserRepository userRepository;

    // Fetch All with optional Search & Filters
    public List<User> getAllUsers(String search, Role role, Status status) {
        if (search != null && !search.isEmpty()) {
            return userRepository.searchUsers(search);
        } else if (role != null && status != null) {
            return userRepository.findByRoleAndStatus(role, status);
        } else if (role != null) {
            return userRepository.findByRole(role);
        } else if (status != null) {
            return userRepository.findByStatus(status);
        } else {
            return userRepository.findAll();
        }
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    // Change Status (Block/Activate)
    public User updateUserStatus(Long id, Status newStatus) {
        User user = getUserById(id);
        user.setStatus(newStatus);
        
        // Simulating Audit Log
        System.out.println("AUDIT: User " + id + " status changed to " + newStatus);
        
        return userRepository.save(user);
    }

    // Change Role
    public User updateUserRole(Long id, Role newRole) {
        User user = getUserById(id);
        user.setRole(newRole);
        
        // Simulating Audit Log
        System.out.println("AUDIT: User " + id + " role changed to " + newRole);

        return userRepository.save(user);
    }
}