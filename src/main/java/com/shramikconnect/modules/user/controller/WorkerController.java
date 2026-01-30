package com.shramikconnect.modules.user.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
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

import com.shramikconnect.common.enums.UserStatus;
import com.shramikconnect.modules.user.service.FileStorageService;

@RestController
@RequestMapping("/api/worker")
@CrossOrigin(origins = "http://localhost:5173")
public class WorkerController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FileStorageService fileStorageService;
@GetMapping("/profile") 
    public ResponseEntity<?> getWorkerProfile(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            String userIdSql = "SELECT user_id FROM users WHERE email = ?";
            Long id = jdbcTemplate.queryForObject(userIdSql, Long.class, userEmail);

       
            String sql = "SELECT u.full_name, u.phone, u.email, w.location, w.skill_set, w.district, w.experience_years " +
                         "FROM users u LEFT JOIN workers w ON u.user_id = w.user_id WHERE u.user_id = ?";
            
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Worker profile not found.");
        }
    }

@PutMapping("/profile/update") 
public ResponseEntity<String> updateProfile(Authentication auth, @RequestBody Map<String, Object> data) {
    try {
        String email = auth.getName();
        // 1. Get the user_id for Shubham Shinde
        Long userId = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE email = ?", Long.class, email);

        // 2. ✅ UPSERT LOGIC: Handles missing worker records automatically
        String sql = "INSERT INTO workers (user_id, skill_set, experience_years, location, district, category) " +
                     "VALUES (?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "skill_set = VALUES(skill_set), " +
                     "experience_years = VALUES(experience_years), " +
                     "location = VALUES(location), " +
                     "district = VALUES(district), " +
                     "category = VALUES(category)";
        
        jdbcTemplate.update(sql, 
            userId,
            data.get("skill_set"), 
            data.get("experience_years"), 
            data.get("location"),
            data.get("district"), 
            data.get("category"));

        return ResponseEntity.ok("Profile synchronized successfully");
    } catch (Exception e) {
        return ResponseEntity.status(500).body("Backend Error: " + e.getMessage());
    }
}
 
    @PostMapping("/upload-kyc")
    public ResponseEntity<String> uploadKyc(Authentication authentication, @RequestParam("file") MultipartFile file) {
        try {
            String userEmail = authentication.getName();
            String userIdSql = "SELECT user_id FROM users WHERE email = ?";
            Long id = jdbcTemplate.queryForObject(userIdSql, Long.class, userEmail);

            String filePath = fileStorageService.saveFile(file, id);
            
            
            String sql = "INSERT INTO kyc_documents (worker_id, document_type, document_path, status) " +
                         "VALUES (?, 'AADHAAR', ?, 'PENDING') " +
                         "ON DUPLICATE KEY UPDATE document_path = VALUES(document_path), status = 'PENDING'";
            
            jdbcTemplate.update(sql, id, filePath);
            return ResponseEntity.ok("KYC uploaded and status set to PENDING");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Upload failed: " + e.getMessage());
        }
    }

   
    @GetMapping("/profile/{id}/kyc-status")
    public Map<String, Object> getKycStatus(@PathVariable Long id) {
        String sql = "SELECT status FROM kyc_documents WHERE worker_id = ?";
        try {
            return jdbcTemplate.queryForMap(sql, id);
        } catch (Exception e) {
           
            return Map.of("status", "NOT_SUBMITTED");
        }
    }
 

    @GetMapping("/contracts")
    public ResponseEntity<?> getMyContracts(Authentication auth) {
        try {
            String email = auth.getName();
            String userIdSql = "SELECT user_id FROM users WHERE email = ?";
            Long id = jdbcTemplate.queryForObject(userIdSql, Long.class, email);

            
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

           
            String escrowSql = "SELECT SUM(agreed_amount) FROM contracts " +
                               "WHERE worker_user_id = ? AND status IN ('ACTIVE', 'SIGNED')";
            Double escrowBalance = jdbcTemplate.queryForObject(escrowSql, Double.class, id);

  
            String releasedSql = "SELECT SUM(agreed_amount) FROM contracts " +
                                 "WHERE worker_user_id = ? AND status = 'COMPLETED'";
            Double releasedBalance = jdbcTemplate.queryForObject(releasedSql, Double.class, id);

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




 
    @GetMapping("/dashboard-stats")
    public ResponseEntity<?> getDashboardStats(Authentication auth) {
        try {
            String email = auth.getName();
            Long id = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE email = ?", Long.class, email);
            
         
            String baseSql = "SELECT u.full_name, " +
                             "(SELECT COALESCE(SUM(agreed_amount), 0) FROM contracts WHERE worker_user_id = ? AND status IN ('ACTIVE', 'SIGNED')) as escrow_balance " +
                             "FROM users u WHERE u.user_id = ?";
            
         
            String appStatsSql = "SELECT " +
                                 "COUNT(CASE WHEN status = 'APPLIED' THEN 1 END) as applied_count, " +
                                 "COUNT(CASE WHEN status = 'SHORTLISTED' THEN 1 END) as shortlisted_count " +
                                 "FROM job_applications WHERE applicant_user_id = ?";
            
            Map<String, Object> stats = jdbcTemplate.queryForMap(baseSql, id, id);
            Map<String, Object> appStats = jdbcTemplate.queryForMap(appStatsSql, id);
            
            stats.putAll(appStats);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
    @GetMapping("/active-jobs")
    public ResponseEntity<?> getActiveJobs(Authentication auth) {
        try {
            String email = auth.getName();
            Long userId = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE email = ?", Long.class, email);

           
            String sql = "SELECT c.contract_id, j.title, j.location, c.agreed_amount, c.status, c.start_date, c.end_date " +
                         "FROM contracts c " +
                         "JOIN jobs j ON c.job_job_id = j.job_id " +
                         "WHERE c.worker_user_id = ? AND c.status IN ('ACTIVE', 'SIGNED')";

            List<Map<String, Object>> activeJobs = jdbcTemplate.queryForList(sql, userId);
            return ResponseEntity.ok(activeJobs);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching active jobs: " + e.getMessage());
        }
    }


 

    @GetMapping("/history")
    public ResponseEntity<?> getContractHistory(Authentication auth) {
        try {
            String email = auth.getName();
            Long userId = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE email = ?", Long.class, email);

          
            String sql = "SELECT c.contract_id, j.title, j.location, c.agreed_amount, c.status, c.signed_at " +
                         "FROM contracts c " +
                         "JOIN jobs j ON c.job_job_id = j.job_id " +
                         "WHERE c.worker_user_id = ? " +
                         "ORDER BY c.signed_at DESC";

            List<Map<String, Object>> history = jdbcTemplate.queryForList(sql, userId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching history: " + e.getMessage());
        }
    }

    @PutMapping("/profile/deactivate")
    public ResponseEntity<String> deactivateProfile(Authentication auth) {
        try {
            String email = auth.getName();
            String sql = "UPDATE users SET status = ? WHERE email = ?";
            jdbcTemplate.update(sql, UserStatus.INACTIVE.name(), email);
            return ResponseEntity.ok("Account deactivated successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Deactivation failed: " + e.getMessage());
        }
    }
    @GetMapping("/my-district-jobs")
    public ResponseEntity<?> getJobsInMyDistrict(Authentication auth) {
        try {
            String email = auth.getName();
            String district = jdbcTemplate.queryForObject(
                "SELECT w.district FROM workers w JOIN users u ON w.user_id = u.user_id WHERE u.email = ?", 
                String.class, email);

            String sql = "SELECT j.*, u.full_name as client_name FROM jobs j " +
                         "JOIN users u ON j.posted_by_user_id = u.user_id " +
                         "WHERE j.district = ? AND j.status = 'OPEN' ORDER BY j.created_at DESC";

            return ResponseEntity.ok(jdbcTemplate.queryForList(sql, district));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching local jobs: " + e.getMessage());
        }
    }
}