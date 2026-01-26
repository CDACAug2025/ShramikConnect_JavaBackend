package com.shramikconnect.modules.configsys.controller;

import com.shramikconnect.modules.configsys.entity.Announcement;
import com.shramikconnect.modules.configsys.entity.SystemSetting;
import com.shramikconnect.modules.configsys.service.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/config")
@Tag(name = "Policy & System Config", description = "Manage Rules, Features, and Announcements")
@CrossOrigin("*")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    // --- SETTINGS APIs ---

    @GetMapping("/settings")
    @Operation(summary = "Get All Settings", description = "View feature toggles and limits")
    public ResponseEntity<List<SystemSetting>> getSettings() {
        return ResponseEntity.ok(configService.getAllSettings());
    }

    @PatchMapping("/settings/{key}")
    @Operation(summary = "Update Setting", description = "Change a rule (e.g., disable chat, increase job limits)")
    public ResponseEntity<SystemSetting> updateSetting(@PathVariable String key, @RequestBody Map<String, String> payload) {
        // Frontend sends: { "value": "false" } or { "value": "10" }
        return ResponseEntity.ok(configService.updateSetting(key, payload.get("value")));
    }

    // --- ANNOUNCEMENTS APIs ---

    @GetMapping("/announcements")
    @Operation(summary = "Get Announcements", description = "View history of broadcast messages")
    public ResponseEntity<List<Announcement>> getAnnouncements() {
        return ResponseEntity.ok(configService.getAllAnnouncements());
    }

    @PostMapping("/announcements")
    @Operation(summary = "Post Announcement", description = "Broadcast a new message to users")
    public ResponseEntity<Announcement> createAnnouncement(@RequestBody Announcement announcement) {
        return ResponseEntity.ok(configService.createAnnouncement(announcement));
    }

    @DeleteMapping("/announcements/{id}")
    @Operation(summary = "Delete Announcement", description = "Remove an old announcement")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long id) {
        configService.deleteAnnouncement(id);
        return ResponseEntity.ok().build();
    }
}