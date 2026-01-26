package com.shramikconnect.modules.user.repository;

import com.shramikconnect.modules.user.entity.User;
import com.shramikconnect.modules.user.entity.User.Role;
import com.shramikconnect.modules.user.entity.User.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 1. Filter by Role and Status
    List<User> findByRoleAndStatus(Role role, Status status);
    List<User> findByRole(Role role);
    List<User> findByStatus(Status status);

    // 2. Search by Name, Email, or Phone (Custom Query)
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "u.phone LIKE CONCAT('%', :keyword, '%')")
    List<User> searchUsers(@Param("keyword") String keyword);
}