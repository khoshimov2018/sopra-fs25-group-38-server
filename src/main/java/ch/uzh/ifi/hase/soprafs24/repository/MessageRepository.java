package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("messageRepository")
public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findByChannelId(Long channelId, Pageable pageable);
}
