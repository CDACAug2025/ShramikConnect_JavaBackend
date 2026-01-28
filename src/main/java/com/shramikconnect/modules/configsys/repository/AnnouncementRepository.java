package com.shramikconnect.modules.configsys.repository;

import com.shramikconnect.modules.configsys.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {}