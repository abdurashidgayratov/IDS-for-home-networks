package uz.ids.homenetwork.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uz.ids.homenetwork.dto.AlertDTO;
import uz.ids.homenetwork.model.Alert;
import uz.ids.homenetwork.model.SuricataSession;
import uz.ids.homenetwork.repository.AlertRepository;
import uz.ids.homenetwork.repository.SuricataSessionRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogMonitorService {
    
    @Value("${suricata.log.path}")
    private String logPath;
    
    private final AlertRepository alertRepository;
    private final SuricataSessionRepository sessionRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final Gson gson = new Gson();
    private long lastPosition = 0;
    
    /**
     * Monitor Suricata logs every second
     */
    @Scheduled(fixedDelay = 1000)
    public void monitorLogs() {
        // Check if any session is running
        List<SuricataSession> runningSessions = sessionRepository
            .findByStatus(SuricataSession.SessionStatus.RUNNING);
        
        if (runningSessions.isEmpty()) {
            return;
        }
        
        try {
            Path path = Paths.get(logPath);
            
            if (!Files.exists(path)) {
                return;
            }
            
            // Read new lines from last position
            List<String> allLines = Files.readAllLines(path);
            
            if (allLines.size() > lastPosition) {
                List<String> newLines = allLines.subList((int) lastPosition, allLines.size());
                
                for (String line : newLines) {
                    processLogLine(line, runningSessions);
                }
                
                lastPosition = allLines.size();
            }
            
        } catch (IOException e) {
            log.error("Error reading Suricata log", e);
        }
    }
    
    /**
     * Process a single log line
     */
    private void processLogLine(String line, List<SuricataSession> sessions) {
        try {
            JsonObject json = gson.fromJson(line, JsonObject.class);
            
            // Check if it's an alert
            if (json.has("event_type") && "alert".equals(json.get("event_type").getAsString())) {
                
                JsonObject alertObj = json.getAsJsonObject("alert");
                
                // Parse timestamp
                String timestamp = json.get("timestamp").getAsString();
                LocalDateTime alertTime = LocalDateTime.parse(
                    timestamp.substring(0, 19),
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME
                );
                
                // Parse IPs and ports
                String srcIp = json.has("src_ip") ? json.get("src_ip").getAsString() : "unknown";
                String destIp = json.has("dest_ip") ? json.get("dest_ip").getAsString() : "unknown";
                Integer srcPort = json.has("src_port") ? json.get("src_port").getAsInt() : null;
                Integer destPort = json.has("dest_port") ? json.get("dest_port").getAsInt() : null;
                String protocol = json.has("proto") ? json.get("proto").getAsString() : "unknown";
                
                Long flowId = json.has("flow_id") ? json.get("flow_id").getAsLong() : null;
                
                // Create alert for each running session
                for (SuricataSession session : sessions) {
                    Alert alert = Alert.builder()
                        .timestamp(alertTime)
                        .signature(alertObj.get("signature").getAsString())
                        .severity(alertObj.get("severity").getAsInt())
                        .category(alertObj.get("category").getAsString())
                        .sourceIp(srcIp)
                        .sourcePort(srcPort)
                        .destinationIp(destIp)
                        .destinationPort(destPort)
                        .protocol(protocol)
                        .flowId(flowId)
                        .payload(line)
                        .user(session.getUser())
                        .build();
                    
                    alert = alertRepository.save(alert);
                    
                    // Update session stats
                    session.setTotalAlerts(session.getTotalAlerts() + 1);
                    sessionRepository.save(session);
                    
                    log.info("New alert saved: {} for user: {}", 
                        alert.getSignature(), 
                        session.getUser().getUsername());
                    
                    // Send real-time notification via WebSocket
                    sendAlertNotification(alert);
                }
            }
            
        } catch (Exception e) {
            log.error("Error processing log line: {}", line, e);
        }
    }
    
    /**
     * Send alert notification via WebSocket
     */
    private void sendAlertNotification(Alert alert) {
        try {
            AlertDTO alertDTO = AlertDTO.builder()
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
            
            // Send to user-specific topic
            messagingTemplate.convertAndSend(
                "/topic/alerts/" + alert.getUser().getId(), 
                alertDTO
            );
            
            log.debug("Alert notification sent via WebSocket for user: {}", 
                alert.getUser().getUsername());
                
        } catch (Exception e) {
            log.error("Error sending WebSocket notification", e);
        }
    }
    
    /**
     * Reset log position (for testing)
     */
    public void resetLogPosition() {
        lastPosition = 0;
        log.info("Log position reset");
    }
}
