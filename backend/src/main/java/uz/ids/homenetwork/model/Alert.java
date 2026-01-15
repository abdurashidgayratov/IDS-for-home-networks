package uz.ids.homenetwork.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerts", indexes = {
    @Index(name = "idx_user_timestamp", columnList = "user_id,timestamp"),
    @Index(name = "idx_flow_id", columnList = "flow_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
    
    @Column(name = "alert_signature", length = 500)
    private String signature;
    
    @Column(name = "severity")
    private Integer severity;
    
    @Column(name = "category", length = 100)
    private String category;
    
    @Column(name = "source_ip", length = 45)
    private String sourceIp;
    
    @Column(name = "source_port")
    private Integer sourcePort;
    
    @Column(name = "destination_ip", length = 45)
    private String destinationIp;
    
    @Column(name = "destination_port")
    private Integer destinationPort;
    
    @Column(name = "protocol", length = 20)
    private String protocol;
    
    @Column(name = "flow_id", unique = true)
    private Long flowId;
    
    @Column(columnDefinition = "TEXT")
    private String payload;
    
    @Column(name = "is_read")
    private Boolean isRead;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @PrePersist
    protected void onCreate() {
        if (isRead == null) {
            isRead = false;
        }
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
