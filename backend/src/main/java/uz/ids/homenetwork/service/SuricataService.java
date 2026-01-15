package uz.ids.homenetwork.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uz.ids.homenetwork.model.SuricataSession;
import uz.ids.homenetwork.model.User;
import uz.ids.homenetwork.repository.SuricataSessionRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuricataService {
    
    @Value("${suricata.executable.path}")
    private String suricataPath;
    
    @Value("${suricata.config.path}")
    private String configPath;
    
    @Value("${suricata.interface}")
    private String networkInterface;
    
    private final SuricataSessionRepository sessionRepository;
    private final ConcurrentHashMap<Long, Process> activeProcesses = new ConcurrentHashMap<>();
    
    /**
     * Start Suricata monitoring for a user
     */
    public SuricataSession startMonitoring(User user) {
        log.info("Starting Suricata for user: {}", user.getUsername());
        
        // Check if already running
        Optional<SuricataSession> existingSession = sessionRepository
            .findByUserAndStatus(user, SuricataSession.SessionStatus.RUNNING);
        
        if (existingSession.isPresent()) {
            log.warn("Suricata already running for user: {}", user.getUsername());
            return existingSession.get();
        }
        
        try {
            // Build command
            ProcessBuilder processBuilder = new ProcessBuilder(
                "sudo",
                suricataPath,
                "-c", configPath,
                "-i", networkInterface,
                "--set", "outputs.1.eve-log.enabled=yes"
            );
            
            processBuilder.redirectErrorStream(true);
            
            // Start process
            Process process = processBuilder.start();
            log.info("Suricata process started with PID: {}", process.pid());
            
            // Create session
            SuricataSession session = SuricataSession.builder()
                .user(user)
                .processId(process.pid())
                .interfaceName(networkInterface)
                .status(SuricataSession.SessionStatus.RUNNING)
                .build();
            
            session = sessionRepository.save(session);
            
            // Store process
            activeProcesses.put(user.getId(), process);
            
            // Start thread to read output
            startOutputReader(process, user.getUsername());
            
            return session;
            
        } catch (IOException e) {
            log.error("Failed to start Suricata", e);
            
            SuricataSession errorSession = SuricataSession.builder()
                .user(user)
                .status(SuricataSession.SessionStatus.ERROR)
                .build();
            
            return sessionRepository.save(errorSession);
        }
    }
    
    /**
     * Stop Suricata monitoring
     */
    public void stopMonitoring(User user) {
        log.info("Stopping Suricata for user: {}", user.getUsername());
        
        Optional<SuricataSession> session = sessionRepository
            .findByUserAndStatus(user, SuricataSession.SessionStatus.RUNNING);
        
        if (session.isEmpty()) {
            log.warn("No running session found for user: {}", user.getUsername());
            return;
        }
        
        try {
            Process process = activeProcesses.get(user.getId());
            
            if (process != null && process.isAlive()) {
                // Stop process gracefully
                process.destroy();
                
                // Wait for termination
                boolean terminated = process.waitFor(10, java.util.concurrent.TimeUnit.SECONDS);
                
                if (!terminated) {
                    log.warn("Suricata did not terminate, forcing...");
                    process.destroyForcibly();
                }
                
                log.info("Suricata process stopped");
                activeProcesses.remove(user.getId());
            }
            
            // Update session
            SuricataSession currentSession = session.get();
            currentSession.setEndTime(LocalDateTime.now());
            currentSession.setStatus(SuricataSession.SessionStatus.STOPPED);
            sessionRepository.save(currentSession);
            
        } catch (InterruptedException e) {
            log.error("Error stopping Suricata", e);
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Check if Suricata is running for user
     */
    public boolean isRunning(User user) {
        Optional<SuricataSession> session = sessionRepository
            .findByUserAndStatus(user, SuricataSession.SessionStatus.RUNNING);
        
        if (session.isEmpty()) {
            return false;
        }
        
        Process process = activeProcesses.get(user.getId());
        return process != null && process.isAlive();
    }
    
    /**
     * Get current session status
     */
    public Optional<SuricataSession> getCurrentSession(User user) {
        return sessionRepository.findByUserAndStatus(
            user, SuricataSession.SessionStatus.RUNNING);
    }
    
    /**
     * Get session statistics
     */
    public SuricataSession getSessionStats(User user) {
        Optional<SuricataSession> session = sessionRepository
            .findTopByUserOrderByStartTimeDesc(user);
        return session.orElse(null);
    }
    
    /**
     * Read Suricata output in background thread
     */
    private void startOutputReader(Process process, String username) {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                
                String line;
                while ((line = reader.readLine()) != null) {
                    log.debug("[Suricata-{}] {}", username, line);
                }
            } catch (IOException e) {
                log.error("Error reading Suricata output for user {}", username, e);
            }
        }, "Suricata-Output-" + username).start();
    }
}
