package uz.ids.homenetwork.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.ids.homenetwork.model.Alert;
import uz.ids.homenetwork.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    
    Page<Alert> findByUserOrderByTimestampDesc(User user, Pageable pageable);
    
    List<Alert> findByUserOrderByTimestampDesc(User user);
    
    List<Alert> findByUserAndIsReadOrderByTimestampDesc(User user, Boolean isRead);
    
    List<Alert> findByUserAndTimestampBetweenOrderByTimestampDesc(
        User user, LocalDateTime start, LocalDateTime end);
    
    Long countByUserAndIsRead(User user, Boolean isRead);
    
    Long countByUser(User user);
    
    @Query("SELECT COUNT(a) FROM Alert a WHERE a.user = :user AND a.timestamp >= :since")
    Long countRecentAlerts(@Param("user") User user, @Param("since") LocalDateTime since);
    
    @Query("SELECT a.severity, COUNT(a) FROM Alert a WHERE a.user = :user GROUP BY a.severity")
    List<Object[]> countBySeverity(@Param("user") User user);
    
    @Query("SELECT a.category, COUNT(a) FROM Alert a WHERE a.user = :user GROUP BY a.category ORDER BY COUNT(a) DESC")
    List<Object[]> countByCategory(@Param("user") User user);
    
    @Query("SELECT a.sourceIp, COUNT(a) FROM Alert a WHERE a.user = :user GROUP BY a.sourceIp ORDER BY COUNT(a) DESC")
    List<Object[]> countBySourceIp(@Param("user") User user);
    
    @Query("SELECT DATE(a.timestamp), COUNT(a) FROM Alert a WHERE a.user = :user AND a.timestamp >= :since GROUP BY DATE(a.timestamp) ORDER BY DATE(a.timestamp)")
    List<Object[]> countByDateSince(@Param("user") User user, @Param("since") LocalDateTime since);
}
