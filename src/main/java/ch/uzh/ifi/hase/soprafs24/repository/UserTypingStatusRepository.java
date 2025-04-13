package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.UserTypingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserTypingStatusRepository extends JpaRepository<UserTypingStatus, Long> {
    Optional<UserTypingStatus> findByUserId(Long userId);
}
