package com.shramikconnect.modules.configsys.service;

import com.shramikconnect.modules.configsys.entity.Announcement;
import com.shramikconnect.modules.configsys.entity.SystemSetting;
import com.shramikconnect.modules.configsys.repository.AnnouncementRepository;
import com.shramikconnect.modules.configsys.repository.SystemSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ConfigService {

    @Autowired
    private SystemSettingRepository settingRepository;

    @Autowired
    private AnnouncementRepository announcementRepository;

    // --- SETTINGS ---
    public List<SystemSetting> getAllSettings() {
        return settingRepository.findAll();
    }

    public SystemSetting updateSetting(String key, String newValue) {
        SystemSetting setting = settingRepository.findById(key)
                .orElseThrow(() -> new RuntimeException("Setting not found: " + key));
        setting.setValue(newValue);
        return settingRepository.save(setting);
    }

    // --- ANNOUNCEMENTS ---
    public List<Announcement> getAllAnnouncements() {
        return announcementRepository.findAll();
    }

    public Announcement createAnnouncement(Announcement announcement) {
        announcement.setActive(true);
        return announcementRepository.save(announcement);
    }

    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }
}