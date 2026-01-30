package com.shramikconnect.modules.user.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map; // ✅ Use java.util.Map, not java.awt.Map

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.shramikconnect.modules.user.service.FileStorageService;

@RestController
@RequestMapping("/api/worker")
@CrossOrigin(origins = "http://localhost:5173")
public class WorkerController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FileStorageService fileStorageService;

 // src/main/java/com/shramikconnect/modules/user/controller/WorkerController.java

    @GetMapping("/profile") // ✅ Removed {id} to use JWT identity
    public ResponseEntity<?> getWorkerProfile(Authentication authentication) {
        try {
            // Extract the user's email/username from the validated JWT
            String userEmail = authentication.getName();
            
            // Find the user_id from the users table using the email
            String userIdSql = "SELECT user_id FROM users WHERE email = ?";
            Long id = jdbcTemplate.queryForObject(userIdSql, Long.class, userEmail);

            String sql = "SELECT u.full_name, u.phone, w.location, w.skill_set, w.district, w.experience_years " +
                         "FROM users u LEFT JOIN workers w ON u.user_id = w.user_id WHERE u.user_id = ?";
            
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Worker profile not found.");
        }
    }

 // --- UPDATE PROFILE DETAILS ---
    @PutMapping("/profile/update") // ✅ Use a consistent endpoint
    public ResponseEntity<String> updateProfile(Authentication authentication, @RequestBody Map<String, Object> data) {
        try {
            String userEmail = authentication.getName();
            String userIdSql = "SELECT user_id FROM users WHERE email = ?";
            Long id = jdbcTemplate.queryForObject(userIdSql, Long.class, userEmail);

            // ✅ Updated SQL to include experience_years based on your table schema
            String sql = "UPDATE workers SET skill_set = ?, experience_years = ? WHERE user_id = ?";
            
            jdbcTemplate.update(sql, 
                data.get("skill_set"),        // Matches frontend key
                data.get("experience_years"), // Matches frontend key
                id);
                
            return ResponseEntity.ok("Profile updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Update failed: " + e.getMessage());
        }
    }

 // ✅ Secure KYC Upload using Authentication instead of PathVariable id
    @PostMapping("/upload-kyc")
    public ResponseEntity<String> uploadKyc(Authentication authentication, @RequestParam("file") MultipartFile file) {
        try {
            String userEmail = authentication.getName();
            String userIdSql = "SELECT user_id FROM users WHERE email = ?";
            Long id = jdbcTemplate.queryForObject(userIdSql, Long.class, userEmail);

            String filePath = fileStorageService.saveFile(file, id);
            
            // Update kyc_documents table using the worker's user_id
            String sql = "INSERT INTO kyc_documents (worker_id, document_type, document_path, status) " +
                         "VALUES (?, 'AADHAAR', ?, 'PENDING') " +
                         "ON DUPLICATE KEY UPDATE document_path = VALUES(document_path), status = 'PENDING'";
            
            jdbcTemplate.update(sql, id, filePath);
            return ResponseEntity.ok("KYC uploaded and status set to PENDING");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Upload failed: " + e.getMessage());
        }
    }

    // --- 4. CHECK KYC STATUS ---
    @GetMapping("/profile/{id}/kyc-status")
    public Map<String, Object> getKycStatus(@PathVariable Long id) {
        String sql = "SELECT status FROM kyc_documents WHERE worker_id = ?";
        try {
            return jdbcTemplate.queryForMap(sql, id);
        } catch (Exception e) {
            // Return default if no document has been uploaded yet
            return Map.of("status", "NOT_SUBMITTED");
        }
    }
 // src/main/java/com/shramikconnect/modules/user/controller/WorkerController.java

    @GetMapping("/contracts")
    public ResponseEntity<?> getMyContracts(Authentication auth) {
        try {
            String email = auth.getName();
            String userIdSql = "SELECT user_id FROM users WHERE email = ?";
            Long id = jdbcTemplate.queryForObject(userIdSql, Long.class, email);

            // ✅ Fetches specific contracts where Shubham is the assigned worker
            String sql = "SELECT * FROM contracts WHERE worker_user_id = ?";
            List<Map<String, Object>> contracts = jdbcTemplate.queryForList(sql, id);
            return ResponseEntity.ok(contracts);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/wallet")
    public ResponseEntity<?> getWalletStats(Authentication auth) {
        try {
            String email = auth.getName();
            String userIdSql = "SELECT user_id FROM users WHERE email = ?";
            Long id = jdbcTemplate.queryForObject(userIdSql, Long.class, email);

            // ✅ Sums the amount for your active/signed contracts
            String escrowSql = "SELECT SUM(agreed_amount) FROM contracts " +
                               "WHERE worker_user_id = ? AND status IN ('ACTIVE', 'SIGNED')";
            Double escrowBalance = jdbcTemplate.queryForObject(escrowSql, Double.class, id);

            // ✅ Sums the amount for your completed contracts
            String releasedSql = "SELECT SUM(agreed_amount) FROM contracts " +
                                 "WHERE worker_user_id = ? AND status = 'COMPLETED'";
            Double releasedBalance = jdbcTemplate.queryForObject(releasedSql, Double.class, id);

            // ✅ Fetches individual transactions for the table
            String listSql = "SELECT signed_at as date, contract_terms as description, agreed_amount as amount, status " +
                             "FROM contracts WHERE worker_user_id = ? ORDER BY signed_at DESC";
            List<Map<String, Object>> transactions = jdbcTemplate.queryForList(listSql, id);

            return ResponseEntity.ok(Map.of(
                "escrowBalance", escrowBalance != null ? escrowBalance : 0.0,
                "releasedBalance", releasedBalance != null ? releasedBalance : 0.0,
                "transactions", transactions
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching wallet data: " + e.getMessage());
        }
    }
 // src/main/java/com/shramikconnect/modules/user/controller/WorkerController.java

    @GetMapping("/dashboard-stats")
    public ResponseEntity<?> getDashboardStats(Authentication auth) {
        try {
            String email = auth.getName();
            String userIdSql = "SELECT user_id FROM users WHERE email = ?";
            Long id = jdbcTemplate.queryForObject(userIdSql, Long.class, email);

            // ✅ SQL to fetch Name, KYC status, and Application Count for User 7
            String sql = "SELECT u.full_name, " +
                         "(SELECT status FROM kyc_documents WHERE worker_id = ?) as kyc_status, " +
                         "(SELECT COUNT(*) FROM job_applications WHERE worker_user_id = ?) as app_count " +
                         "FROM users u WHERE u.user_id = ?";
            
            Map<String, Object> stats = jdbcTemplate.queryForMap(sql, id, id, id);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}