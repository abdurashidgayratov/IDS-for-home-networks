package uz.ids.homenetwork.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ids.homenetwork.dto.AlertDTO;
import uz.ids.homenetwork.dto.StatisticsDTO;
import uz.ids.homenetwork.model.Alert;
import uz.ids.homenetwork.model.User;
import uz.ids.homenetwork.repository.AlertRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {
    
    private final AlertRepository alertRepository;
    
    /**
     * Get all alerts for user
     */
    public List<AlertDTO> getAllAlerts(User user) {
        List<Alert> alerts = alertRepository.findByUserOrderByTimestampDesc(user);
        return alerts.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get paginated alerts
     */
    public Page<AlertDTO> getAlertsPaginated(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Alert> alerts = alertRepository.findByUserOrderByTimestampDesc(user, pageable);
        return alerts.map(this::convertToDTO);
    }
    
    /**
     * Get unread alerts
     */
    public List<AlertDTO> getUnreadAlerts(User user) {
        List<Alert> alerts = alertRepository.findByUserAndIsReadOrderByTimestampDesc(user, false);
        return alerts.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get alert by ID
     */
    public AlertDTO getAlertById(Long id, User user) {
        Alert alert = alertRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Alert not found"));
        
        // Check if alert belongs to user
        if (!alert.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        return convertToDTO(alert);
    }
    
    /**
     * Mark alert as read
     */
    @Transactional
    public void markAsRead(Long id, User user) {
        Alert alert = alertRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Alert not found"));
        
        if (!alert.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        alert.setIsRead(true);
        alertRepository.save(alert);
    }
    
    /**
     * Mark all alerts as read
     */
    @Transactional
    public void markAllAsRead(User user) {
        List<Alert> unreadAlerts = alertRepository.findByUserAndIsReadOrderByTimestampDesc(user, false);
        unreadAlerts.forEach(alert -> alert.setIsRead(true));
        alertRepository.saveAll(unreadAlerts);
    }
    
    /**
     * Get statistics
     */
    public StatisticsDTO getStatistics(User user) {
        Long totalAlerts = alertRepository.countByUser(user);
        Long unreadAlerts = alertRepository.countByUserAndIsRead(user, false);
        
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        Long todayAlerts = alertRepository.countRecentAlerts(user, todayStart);
        
        LocalDateTime weekStart = LocalDateTime.now().minusDays(7);
        Long thisWeekAlerts = alertRepository.countRecentAlerts(user, weekStart);
        
        // Alerts by severity
        Map<String, Long> bySeverity = new HashMap<>();
        List<Object[]> severityData = alertRepository.countBySeverity(user);
        for (Object[] row : severityData) {
            String severity = getSeverityName((Integer) row[0]);
            bySeverity.put(severity, (Long) row[1]);
        }
        
        // Alerts by category
        Map<String, Long> byCategory = new HashMap<>();
        List<Object[]> categoryData = alertRepository.countByCategory(user);
        for (Object[] row : categoryData) {
            byCategory.put((String) row[0], (Long) row[1]);
        }
        
        // Top source IPs
        List<StatisticsDTO.TopIpDTO> topIps = alertRepository.countBySourceIp(user).stream()
            .limit(10)
            .map(row -> StatisticsDTO.TopIpDTO.builder()
                .ip((String) row[0])
                .count((Long) row[1])
                .build())
            .collect(Collectors.toList());
        
        // Timeline (last 7 days)
        List<StatisticsDTO.TimelinePoint> timeline = alertRepository
            .countByDateSince(user, weekStart).stream()
            .map(row -> StatisticsDTO.TimelinePoint.builder()
                .date(row[0].toString())
                .count((Long) row[1])
                .build())
            .collect(Collectors.toList());
        
        return StatisticsDTO.builder()
            .totalAlerts(totalAlerts)
            .unreadAlerts(unreadAlerts)
            .todayAlerts(todayAlerts)
            .thisWeekAlerts(thisWeekAlerts)
            .alertsBySeverity(bySeverity)
            .alertsByCategory(byCategory)
            .topSourceIps(topIps)
            .timeline(timeline)
            .build();
    }
    
    /**
     * Delete alert
     */
    @Transactional
    public void deleteAlert(Long id, User user) {
        Alert alert = alertRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Alert not found"));
        
        if (!alert.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        alertRepository.delete(alert);
    }
    
    /**
     * Convert Alert entity to DTO
     */
    private AlertDTO convertToDTO(Alert alert) {
        return AlertDTO.builder()
            .id(alert.getId())
            .timestamp(alert.getTimestamp())
            .signature(alert.getSignature())
            .severity(alert.getSeverity())
            .category(alert.getCategory())
            .sourceIp(alert.getSourceIp())
            .sourcePort(alert.getSourcePort())
            .destinationIp(alert.getDestinationIp())
            .destinationPort(alert.getDestinationPort())
            .protocol(alert.getProtocol())
            .isRead(alert.getIsRead())
            .build();
    }
    
    /**
     * Get severity name from level
     */
    private String getSeverityName(Integer level) {
        return switch (level) {
            case 1 -> "Critical";
            case 2 -> "High";
            case 3 -> "Medium";
            default -> "Low";
        };
    }
}
