package uz.ids.homenetwork.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertDTO {
    private Long id;
    private LocalDateTime timestamp;
    private String signature;
    private Integer severity;
    private String category;
    private String sourceIp;
    private Integer sourcePort;
    private String destinationIp;
    private Integer destinationPort;
    private String protocol;
    private Boolean isRead;
}
