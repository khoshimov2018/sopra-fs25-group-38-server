package ch.uzh.ifi.hase.soprafs24.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.uzh.ifi.hase.soprafs24.entity.chat.ChatChannel;

import java.util.List;

@Repository("chatChannelRepository")
public interface ChatChannelRepository extends JpaRepository<ChatChannel, Long> {
    // Find all channels that include a participant with the given user id.
    List<ChatChannel> findByParticipantsUserId(Long userId);
}