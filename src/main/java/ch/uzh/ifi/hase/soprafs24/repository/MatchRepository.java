package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;




@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    @Query("SELECT m FROM Match m WHERE (m.userId1 = :userId OR m.userId2 = :userId) AND m.status = 'ACCEPTED'")
    List<Match> findAcceptedMatchesByUserId(@Param("userId") Long userId);
    
    @Query("SELECT m FROM Match m WHERE (m.userId1 = :userId AND m.userId2 = :targetUserId) " +
    "OR (m.userId1 = :targetUserId AND m.userId2 = :userId)")
    Optional<Match> findMatchByUsers(@Param("userId") Long userId, @Param("targetUserId") Long targetUserId);
}

