package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.ChatChannel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatChannelRepository extends JpaRepository<ChatChannel, Long> {
}