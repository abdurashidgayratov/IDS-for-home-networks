package uz.ids.homenetwork.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.ids.homenetwork.dto.AlertDTO;
import uz.ids.homenetwork.model.User;
import uz.ids.homenetwork.service.AlertService;
import uz.ids.homenetwork.service.AuthService;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AlertController {
    
    private final AlertService alertService;
    private final AuthService authService;
    
    @GetMapping
    public ResponseEntity<?> getAllAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        try {
            User user = authService.getCurrentUser();
            
            if (page == 0 && size == 50) {
                // Return all alerts
                List<AlertDTO> alerts = alertService.getAllAlerts(user);
                return ResponseEntity.ok(alerts);
            } else {
                // Return paginated
                Page<AlertDTO> alerts = alertService.getAlertsPaginated(user, page, size);
                return ResponseEntity.ok(alerts);
            }
        } catch (Exception e) {
            log.error("Failed to get alerts", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/unread")
    public ResponseEntity<?> getUnreadAlerts() {
        try {
            User user = authService.getCurrentUser();
            List<AlertDTO> alerts = alertService.getUnreadAlerts(user);
            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            log.error("Failed to get unread alerts", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getAlert(@PathVariable Long id) {
        try {
            User user = authService.getCurrentUser();
            AlertDTO alert = alertService.getAlertById(id, user);
            return ResponseEntity.ok(alert);
        } catch (Exception e) {
            log.error("Failed to get alert", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        try {
            User user = authService.getCurrentUser();
            alertService.markAsRead(id, user);
            return ResponseEntity.ok(Map.of("message", "Alert marked as read"));
        } catch (Exception e) {
            log.error("Failed to mark alert as read", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead() {
        try {
            User user = authService.getCurrentUser();
            alertService.markAllAsRead(user);
            return ResponseEntity.ok(Map.of("message", "All alerts marked as read"));
        } catch (Exception e) {
            log.error("Failed to mark all as read", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAlert(@PathVariable Long id) {
        try {
            User user = authService.getCurrentUser();
            alertService.deleteAlert(id, user);
            return ResponseEntity.ok(Map.of("message", "Alert deleted"));
        } catch (Exception e) {
            log.error("Failed to delete alert", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @PostMapping("/{id}/block-ip")
    public ResponseEntity<?> blockIp(@PathVariable Long id) {
        try {
            User user = authService.getCurrentUser();
            AlertDTO alert = alertService.getAlertById(id, user);
            String ip = alert.getSourceIp();

            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder pb;

            if (os.contains("mac")) {
                // macOS uchun
                pb = new ProcessBuilder(
                        "sudo", "pfctl", "-t", "blocked", "-T", "add", ip
                );
            } else {
                // Linux uchun
                pb = new ProcessBuilder(
                        "sudo", "iptables", "-A", "INPUT", "-s", ip, "-j", "DROP"
                );
            }

            pb.redirectErrorStream(true);
            Process process = pb.start();
            process.waitFor(10, TimeUnit.SECONDS);

            log.info("IP {} blocked by {}", ip, user.getUsername());

            return ResponseEntity.ok(Map.of(
                    "message", "IP blocked: " + ip,
                    "ip", ip,
                    "blocked", true
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
