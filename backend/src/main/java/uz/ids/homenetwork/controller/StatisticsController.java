package uz.ids.homenetwork.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.ids.homenetwork.dto.StatisticsDTO;
import uz.ids.homenetwork.model.User;
import uz.ids.homenetwork.service.AlertService;
import uz.ids.homenetwork.service.AuthService;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class StatisticsController {
    
    private final AlertService alertService;
    private final AuthService authService;
    
    @GetMapping
    public ResponseEntity<?> getStatistics() {
        try {
            User user = authService.getCurrentUser();
            StatisticsDTO stats = alertService.getStatistics(user);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Failed to get statistics", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
