package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByUserIdOrderByCreationDateDesc(Long userId);
    
    List<Notification> findByUserIdAndReadFalseOrderByCreationDateDesc(Long userId);
    
    void deleteByUserId(Long userId);
}