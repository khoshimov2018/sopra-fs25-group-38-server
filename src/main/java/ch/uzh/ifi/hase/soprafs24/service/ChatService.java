package ch.uzh.ifi.hase.soprafs24.service;

// entity
import ch.uzh.ifi.hase.soprafs24.entity.ChatChannel;
import ch.uzh.ifi.hase.soprafs24.entity.ChatParticipant;
import ch.uzh.ifi.hase.soprafs24.entity.Message;
import ch.uzh.ifi.hase.soprafs24.entity.User;
// repository
import ch.uzh.ifi.hase.soprafs24.repository.ChatChannelRepository;
import ch.uzh.ifi.hase.soprafs24.repository.MessageRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
// dto
import ch.uzh.ifi.hase.soprafs24.rest.dto.MessagePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatChannelPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
// springboot 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
// utility
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    private final ChatChannelRepository chatChannelRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Autowired
    public ChatService(ChatChannelRepository chatChannelRepository,
                       MessageRepository messageRepository,
                       UserRepository userRepository) {
        this.chatChannelRepository = chatChannelRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public ChatChannel createChatChannel(ChatChannelPostDTO chatChannelPostDTO) {
        // Convert the POST DTO to a ChatChannel entity.
        ChatChannel chatChannel = DTOMapper.INSTANCE.convertChatChannelPostDTOtoEntity(chatChannelPostDTO);

        // Add participants from the provided list.
        List<Long> participantIds = chatChannelPostDTO.getParticipantIds();
        if (participantIds == null || participantIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one participant is required");
        }
        // Loop over participant IDs.
        for (int i = 0; i < participantIds.size(); i++) {
            Long participantId = participantIds.get(i);
            User user = userRepository.findById(participantId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + participantId + " not found"));
            // First participant is designated as admin; others as member.
            String role = (i == 0) ? "admin" : "member";
            ChatParticipant participant = new ChatParticipant(user, role);
            chatChannel.addParticipant(participant);
        }

        // For individual chat, set the channel name as "username1&username2"
        if (chatChannel.getType().equalsIgnoreCase("individual")) {
            if (participantIds.size() != 2) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Individual channels must have exactly two participants");
            }
            // Retrieve both users to obtain their names.
            User user1 = userRepository.findById(participantIds.get(0))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + participantIds.get(0) + " not found"));
            User user2 = userRepository.findById(participantIds.get(1))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + participantIds.get(1) + " not found"));
            // Set the channel name in the format "username1&username2"
            chatChannel.setName(user1.getName() + "&" + user2.getName());
        }

        chatChannel = chatChannelRepository.save(chatChannel);
        chatChannelRepository.flush();
        return chatChannel;
    }

    public ChatChannel createIndividualChatChannelAfterMatch(User user1, User user2) {
        ChatChannel channel = new ChatChannel();
        channel.setType("individual");
        // Set the channel name in the format "username1&username2"
        channel.setName(user1.getName() + "&" + user2.getName());
        channel.setChannelProfileImage(null);
        channel.setCreatedAt(LocalDateTime.now());
        channel.setUpdatedAt(LocalDateTime.now());
    
        // Create ChatParticipant for each user.
        ChatParticipant participant1 = new ChatParticipant(user1, "member");
        ChatParticipant participant2 = new ChatParticipant(user2, "member");
    
        // Add participants to the channel using the helper method.
        channel.addParticipant(participant1);
        channel.addParticipant(participant2);
    
        // Save the channel (cascading saves the ChatParticipants).
        ChatChannel savedChannel = chatChannelRepository.save(channel);
        chatChannelRepository.flush();
        return savedChannel;
    }
    

    // Get all channels for a given user (for sidebar display)
    public List<ChatChannel> getChannelsForUser(Long userId) {
        return chatChannelRepository.findByParticipantsUserId(userId);
    }

    // Send a message in a channel
    public Message sendMessage(Long channelId, MessagePostDTO messagePostDTO) {
        ChatChannel chatChannel = chatChannelRepository.findById(channelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ChatChannel with id " + channelId + " not found"));
        User sender = userRepository.findById(messagePostDTO.getSenderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + messagePostDTO.getSenderId() + " not found"));

        // Verify that the sender is a participant.
        boolean isParticipant = chatChannel.getParticipants().stream()
                .anyMatch(participant -> participant.getUser().getId().equals(sender.getId()));
        if (!isParticipant) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not a participant of this chat channel");
        }
        Message message = DTOMapper.INSTANCE.convertMessagePostDTOtoEntity(messagePostDTO);
        message.setChannel(chatChannel);
        message.setSender(sender);
        message.setTimestamp(LocalDateTime.now());
        message = messageRepository.save(message);
        messageRepository.flush();

        return message;
    }

    // Get chat history of a channel given the channel Id
    public List<Message> getChatHistory(Long channelId) {
        ChatChannel chatChannel = chatChannelRepository.findById(channelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ChatChannel with id " + channelId + " not found"));
        // For simplicity, we fetch all messages in the channel sorted by id.
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("id").ascending());
        return messageRepository.findByChannelId(chatChannel.getId(), pageable).getContent();
    }

}
