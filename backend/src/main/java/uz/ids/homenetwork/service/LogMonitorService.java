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
import java.io.RandomAccessFile;
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

    // Faylning oxirgi o'qilgan byte pozitsiyasi
    private long lastPosition = 0;

    /**
     * Monitor Suricata logs every 2 seconds
     * FIXED: Uses RandomAccessFile to read only NEW lines, not entire file
     */
    @Scheduled(fixedDelay = 2000)
    public void monitorLogs() {
        // Running session bormi tekshirish
        List<SuricataSession> runningSessions = sessionRepository
                .findByStatus(SuricataSession.SessionStatus.RUNNING);

        if (runningSessions.isEmpty()) {
            return;
        }

        Path path = Paths.get(logPath);
        if (!Files.exists(path)) {
            return;
        }

        // RandomAccessFile — faqat yangi qatorlarni o'qiydi, butun faylni emas!
        try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "r")) {
            long fileLength = raf.length();

            // Fayl tozalangan bo'lsa pozitsiyani reset qilish
            if (fileLength < lastPosition) {
                log.info("Log file was truncated, resetting position");
                lastPosition = 0;
            }

            // Yangi narsa yo'q bo'lsa chiqib ketish
            if (fileLength == lastPosition) {
                return;
            }

            // Oxirgi o'qilgan joydan boshlash
            raf.seek(lastPosition);

            String line;
            while ((line = raf.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    processLogLine(line, runningSessions);
                }
            }

            // Yangi pozitsiyani saqlash
            lastPosition = raf.getFilePointer();

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

            if (json == null || !json.has("event_type")) return;
            if (!"alert".equals(json.get("event_type").getAsString())) return;

            JsonObject alertObj = json.getAsJsonObject("alert");
            if (alertObj == null) return;

            // Timestamp parse
            String timestamp = json.has("timestamp") ? json.get("timestamp").getAsString() : null;
            LocalDateTime alertTime = LocalDateTime.now();
            if (timestamp != null && timestamp.length() >= 19) {
                try {
                    alertTime = LocalDateTime.parse(
                            timestamp.substring(0, 19),
                            DateTimeFormatter.ISO_LOCAL_DATE_TIME
                    );
                } catch (Exception ignored) {}
            }

            String srcIp    = json.has("src_ip")    ? json.get("src_ip").getAsString()    : "unknown";
            String destIp   = json.has("dest_ip")   ? json.get("dest_ip").getAsString()   : "unknown";
            Integer srcPort  = json.has("src_port")  ? json.get("src_port").getAsInt()     : null;
            Integer destPort = json.has("dest_port") ? json.get("dest_port").getAsInt()    : null;
            String protocol  = json.has("proto")     ? json.get("proto").getAsString()     : "unknown";
            Long flowId      = json.has("flow_id")   ? json.get("flow_id").getAsLong()     : null;

            for (SuricataSession session : sessions) {
                Alert alert = Alert.builder()
                        .timestamp(alertTime)
                        .signature(alertObj.has("signature") ? alertObj.get("signature").getAsString() : "Unknown")
                        .severity(alertObj.has("severity")   ? alertObj.get("severity").getAsInt()     : 4)
                        .category(alertObj.has("category")   ? alertObj.get("category").getAsString()  : "Unknown")
                        .sourceIp(srcIp)
                        .sourcePort(srcPort)
                        .destinationIp(destIp)
                        .destinationPort(destPort)
                        .protocol(protocol)
                        .flowId(flowId)
                        .payload(line.length() > 1000 ? line.substring(0, 1000) : line)
                        .user(session.getUser())
                        .build();

                alert = alertRepository.save(alert);

                session.setTotalAlerts(session.getTotalAlerts() + 1);
                sessionRepository.save(session);

                log.info("New alert: {} for user: {}", alert.getSignature(), session.getUser().getUsername());
                sendAlertNotification(alert);
            }

        } catch (Exception e) {
            log.debug("Skipping invalid log line: {}", e.getMessage());
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

            messagingTemplate.convertAndSend(
                    "/topic/alerts/" + alert.getUser().getId(),
                    alertDTO
            );
        } catch (Exception e) {
            log.error("Error sending WebSocket notification", e);
        }
    }

    /**
     * Reset log position
     */
    public void resetLogPosition() {
        lastPosition = 0;
        log.info("Log position reset");
    }
}
