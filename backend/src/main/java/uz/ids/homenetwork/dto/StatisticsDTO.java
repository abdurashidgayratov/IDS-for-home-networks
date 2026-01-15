package uz.ids.homenetwork.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsDTO {
    private Long totalAlerts;
    private Long unreadAlerts;
    private Long todayAlerts;
    private Long thisWeekAlerts;
    private Map<String, Long> alertsBySeverity;
    private Map<String, Long> alertsByCategory;
    private List<TopIpDTO> topSourceIps;
    private List<TimelinePoint> timeline;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopIpDTO {
        private String ip;
        private Long count;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TimelinePoint {
        private String date;
        private Long count;
    }
}
