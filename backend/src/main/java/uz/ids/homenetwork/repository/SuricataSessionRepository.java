package uz.ids.homenetwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.ids.homenetwork.model.SuricataSession;
import uz.ids.homenetwork.model.User;
import java.util.List;
import java.util.Optional;

@Repository
public interface SuricataSessionRepository extends JpaRepository<SuricataSession, Long> {
    
    List<SuricataSession> findByUserOrderByStartTimeDesc(User user);
    
    Optional<SuricataSession> findByUserAndStatus(
        User user, SuricataSession.SessionStatus status);
    
    List<SuricataSession> findByStatus(SuricataSession.SessionStatus status);
    
    Long countByUser(User user);
    
    Optional<SuricataSession> findTopByUserOrderByStartTimeDesc(User user);
}
