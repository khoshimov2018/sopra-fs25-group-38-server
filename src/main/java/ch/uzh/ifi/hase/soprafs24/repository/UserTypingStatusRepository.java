package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.UserTypingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface UserTypingStatusRepository extends JpaRepository<UserTypingStatus, Long> {
    Optional<UserTypingStatus> findByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserTypingStatus uts WHERE uts.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

}
