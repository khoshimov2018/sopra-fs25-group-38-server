package ch.uzh.ifi.hase.soprafs24.service;

// entity
import ch.uzh.ifi.hase.soprafs24.entity.ChatChannel;
import ch.uzh.ifi.hase.soprafs24.entity.ChatParticipant;
import ch.uzh.ifi.hase.soprafs24.entity.Message;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.UserTypingStatus;
// repository
import ch.uzh.ifi.hase.soprafs24.repository.ChatChannelRepository;
import ch.uzh.ifi.hase.soprafs24.repository.MessageRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserTypingStatusRepository;
// dto
import ch.uzh.ifi.hase.soprafs24.rest.dto.MessagePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatChannelPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserTypingStatusPushDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserTypingStatusGetDTO;
// springboot 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
// utility
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.Iterator;

@Service
public class ChatService {

    private final ChatChannelRepository chatChannelRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final UserTypingStatusRepository userTypingStatusRepository;

    @Autowired
    public ChatService(ChatChannelRepository chatChannelRepository,
                       MessageRepository messageRepository,
                       UserRepository userRepository,
                       UserTypingStatusRepository userTypingStatusRepository) {
        this.chatChannelRepository = chatChannelRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.userTypingStatusRepository = userTypingStatusRepository;
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
        // 1) Look for an existing individual channel containing exactly those two users:
        List<ChatChannel> user1Channels =
            chatChannelRepository.findByParticipantsUserId(user1.getId());
        
        for (ChatChannel existing : user1Channels) {
            if ("individual".equalsIgnoreCase(existing.getType())) {
                // collect the two participant IDs into a Set
                Set<Long> ids = existing.getParticipants().stream()
                    .map(cp -> cp.getUser().getId())
                    .collect(Collectors.toSet());
                
                // if it’s exactly {user1, user2}, return it immediately
                if (ids.size() == 2 &&
                    ids.contains(user1.getId()) &&
                    ids.contains(user2.getId())) {
                    return existing;
                }
            }
        }

        // 2) Otherwise, build & save a new channel as before
        ChatChannel channel = new ChatChannel();
        channel.setType("individual");
        channel.setName(user1.getName() + "&" + user2.getName());
        channel.setChannelProfileImage(null);
        channel.setCreatedAt(LocalDateTime.now());
        channel.setUpdatedAt(LocalDateTime.now());

        channel.addParticipant(new ChatParticipant(user1, "member"));
        channel.addParticipant(new ChatParticipant(user2, "member"));

        ChatChannel saved = chatChannelRepository.save(channel);
        chatChannelRepository.flush();
        return saved;
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

    /**
     * Updates the typing indicator based on the push DTO and returns the updated state including user status.
     */
    @Transactional
    public UserTypingStatusGetDTO updateTypingStatus(UserTypingStatusPushDTO pushDTO) {
        // Ensure the user exists.
        User user = userRepository.findById(pushDTO.getUserId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + pushDTO.getUserId() + " not found"));

        // Retrieve or create the typing status record.
        UserTypingStatus typingStatus = userTypingStatusRepository.findByUserId(pushDTO.getUserId())
            .orElse(new UserTypingStatus(user, pushDTO.isTyping()));
        // Update typing flag.
        typingStatus.setTyping(pushDTO.isTyping());
        userTypingStatusRepository.save(typingStatus);
        userTypingStatusRepository.flush();

        // Return the DTO containing userId, current typing indicator, and the user's online/offline status.
        return new UserTypingStatusGetDTO(user.getId(), typingStatus.isTyping(), user.getStatus());
    }

    /**
     * Retrieves the current typing indicator and persistent user status.
     */
    public UserTypingStatusGetDTO getTypingStatus(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "User with id " + userId + " not found"));
    
        boolean typing = userTypingStatusRepository.findByUserId(userId)
                           .map(UserTypingStatus::isTyping)
                           .orElse(false);        // default
    
        return new UserTypingStatusGetDTO(user.getId(), typing, user.getStatus());
    }


    /**
     * Delete the individual channel when one user block another if the channel exists
     */
    public void deleteIndividualChannelBetweenUsers(Long blockerId, Long blockedUserId) {
        // Retrieve all channels for the blocker.
        List<ChatChannel> channels = chatChannelRepository.findByParticipantsUserId(blockerId);
        
        // Loop through channels to find an individual channel including the blocked user.
        for (ChatChannel channel : channels) {
            if ("individual".equalsIgnoreCase(channel.getType())) {
                boolean containsBlockedUser = channel.getParticipants().stream()
                    .anyMatch(participant -> participant.getUser().getId().equals(blockedUserId));
                
                if (containsBlockedUser) {
                    // Delete the channel if found.
                    chatChannelRepository.delete(channel);
                }
            }
        }
        chatChannelRepository.flush();
    }

    /*
     * response to the PUT /chat/channels/{channelId} request - update the chatchannel that's already been created
     */
    public ChatChannel updateChatChannel(Long channelId, ChatChannelPostDTO dto) {
        // 1) load & verify
        ChatChannel channel = chatChannelRepository.findById(channelId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "ChatChannel with id " + channelId + " not found"));

        // only allow updating of group channels
        if ("individual".equalsIgnoreCase(channel.getType())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Cannot update an individual channel via this endpoint");
        }

        // 2) update name & image
        if (dto.getChannelName() != null) {
            channel.setName(dto.getChannelName());
        }
        channel.setChannelProfileImage(dto.getChannelProfileImage());
        channel.setUpdatedAt(LocalDateTime.now());

        // 3) reconcile participants
        List<Long> newIds = dto.getParticipantIds();
        if (newIds == null || newIds.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "At least one participant is required");
        }
        Set<Long> newIdSet = new HashSet<>(newIds);

        // a) remove participants no longer in the new list
        Iterator<ChatParticipant> it = channel.getParticipants().iterator();
        while (it.hasNext()) {
            ChatParticipant cp = it.next();
            if (!newIdSet.contains(cp.getUser().getId())) {
                it.remove();
                cp.setChannel(null);  // orphanRemoval will delete
            }
        }

        // b) add any new participants
        Set<Long> existingIds = channel.getParticipants().stream()
            .map(p -> p.getUser().getId())
            .collect(Collectors.toSet());

        for (Long uid : newIdSet) {
            if (!existingIds.contains(uid)) {
                User user = userRepository.findById(uid)
                    .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User with id " + uid + " not found"));
                ChatParticipant p = new ChatParticipant(user, "member");
                channel.addParticipant(p);
            }
        }

        // 4) save & return
        ChatChannel saved = chatChannelRepository.save(channel);
        chatChannelRepository.flush();
        return saved;
    }
    

}
