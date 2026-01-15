package uz.ids.homenetwork.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "suricata_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuricataSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private SessionStatus status;
    
    @Column(name = "total_packets")
    private Long totalPackets;
    
    @Column(name = "total_alerts")
    private Long totalAlerts;
    
    @Column(name = "process_id")
    private Long processId;
    
    @Column(name = "interface_name", length = 50)
    private String interfaceName;
    
    public enum SessionStatus {
        RUNNING,
        STOPPED,
        ERROR
    }
    
    @PrePersist
    protected void onCreate() {
        startTime = LocalDateTime.now();
        status = SessionStatus.RUNNING;
        totalPackets = 0L;
        totalAlerts = 0L;
    }
}
