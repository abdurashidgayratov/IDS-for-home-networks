package uz.ids.homenetwork.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.ids.homenetwork.model.SuricataSession;
import uz.ids.homenetwork.model.User;
import uz.ids.homenetwork.service.AuthService;
import uz.ids.homenetwork.service.SuricataService;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/suricata")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class SuricataController {
    
    private final SuricataService suricataService;
    private final AuthService authService;
    
    @PostMapping("/start")
    public ResponseEntity<?> startMonitoring() {
        try {
            User user = authService.getCurrentUser();
            SuricataSession session = suricataService.startMonitoring(user);
            
            return ResponseEntity.ok(Map.of(
                "message", "Suricata monitoring started",
                "session", Map.of(
                    "id", session.getId(),
                    "status", session.getStatus(),
                    "startTime", session.getStartTime(),
                    "processId", session.getProcessId()
                )
            ));
        } catch (Exception e) {
            log.error("Failed to start Suricata", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/stop")
    public ResponseEntity<?> stopMonitoring() {
        try {
            User user = authService.getCurrentUser();
            suricataService.stopMonitoring(user);
            
            return ResponseEntity.ok(Map.of(
                "message", "Suricata monitoring stopped"
            ));
        } catch (Exception e) {
            log.error("Failed to stop Suricata", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/status")
    public ResponseEntity<?> getStatus() {
        try {
            User user = authService.getCurrentUser();
            boolean isRunning = suricataService.isRunning(user);
            SuricataSession stats = suricataService.getSessionStats(user);
            
            return ResponseEntity.ok(Map.of(
                "isRunning", isRunning,
                "currentSession", stats != null ? Map.of(
                    "id", stats.getId(),
                    "status", stats.getStatus(),
                    "startTime", stats.getStartTime(),
                    "totalAlerts", stats.getTotalAlerts(),
                    "totalPackets", stats.getTotalPackets()
                ) : null
            ));
        } catch (Exception e) {
            log.error("Failed to get Suricata status", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
