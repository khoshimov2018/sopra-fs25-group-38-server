package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {

    // Find all blocks made by a specific user
    List<Block> findByBlockerId(Long blockerId);

    // Check if a user has already blocked another user
    boolean existsByBlockerIdAndBlockedUserId(Long blockerId, Long blockedUserId);

    // Check if two users are blocked in either direction
    boolean existsByBlockerIdAndBlockedUserIdOrBlockedUserIdAndBlockerId(Long userId1, Long userId2, Long userId3, Long userId4);

    // Optionally: find all blocks against a user
    List<Block> findByBlockedUserId(Long blockedUserId);

    // Delete all records involving the given user(both of blockId, blockedUserId)
    void deleteAllByBlockerIdOrBlockedUserId(Long blockerId, Long blockedUserId);


}
