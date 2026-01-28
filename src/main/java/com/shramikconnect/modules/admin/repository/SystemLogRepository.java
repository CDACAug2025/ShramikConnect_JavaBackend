package com.shramikconnect.modules.admin.repository;

import com.shramikconnect.modules.admin.entity.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
    // Fetch latest 10 logs
    List<SystemLog> findTop10ByOrderByTimestampDesc();
}