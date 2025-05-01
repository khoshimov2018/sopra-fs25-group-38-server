package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
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
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatChannelPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.MessagePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserTypingStatusGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserTypingStatusPushDTO;

// Spring & Junit annotations
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.web.server.ResponseStatusException;
// utils
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
// junit & Mockito
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChatServiceTest {

    @Mock
    private ChatChannelRepository chatChannelRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserTypingStatusRepository userTypingStatusRepository;

    @InjectMocks
    private ChatService chatService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createChatChannel_validInput_success() {
        // Given
        ChatChannelPostDTO postDTO = new ChatChannelPostDTO();
        postDTO.setChannelName("Study Group");
        postDTO.setChannelType("group");
        postDTO.setChannelProfileImage("group.png");
        postDTO.setParticipantIds(List.of(1L, 2L));

        // Prepare mock users
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        // Instead of returning a new instance, return the argument (which includes participants)
        when(chatChannelRepository.save(any(ChatChannel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ChatChannel result = chatService.createChatChannel(postDTO);

        // Then
        assertNotNull(result);
        // You might check the ID if it's set later; otherwise, focus on the content
        assertEquals("Study Group", result.getName());
        // Check that two participants were added: first as admin and second as member.
        List<ChatParticipant> participants = result.getParticipants();
        assertEquals(2, participants.size());
        assertEquals("admin", participants.get(0).getRole());
        assertEquals("member", participants.get(1).getRole());
    }


    @Test
    void sendMessage_validInput_success() {
        // Given
        Long channelId = 100L;
        MessagePostDTO postDTO = new MessagePostDTO();
        postDTO.setSenderId(1L);
        postDTO.setContext("Hello!");

        ChatChannel channel = new ChatChannel("Study Group", "group", "group.png");
        channel.setId(channelId);

        // Add a participant to the channel (so sender is valid)
        User user = new User();
        user.setId(1L);
        ChatParticipant participant = new ChatParticipant(user, "admin");
        channel.addParticipant(participant);

        when(chatChannelRepository.findById(channelId)).thenReturn(Optional.of(channel));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Message savedMessage = new Message(channel, user, "Hello!");
        savedMessage.setId(200L);
        savedMessage.setTimestamp(LocalDateTime.now());
        when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);

        // When
        Message result = chatService.sendMessage(channelId, postDTO);

        // Then
        assertNotNull(result);
        assertEquals(200L, result.getId());
        assertEquals("Hello!", result.getContent());
        assertEquals(user, result.getSender());
    }

    @Test
    void sendMessage_senderNotParticipant_throwsException() {
        // Given
        Long channelId = 100L;
        MessagePostDTO postDTO = new MessagePostDTO();
        postDTO.setSenderId(1L);
        postDTO.setContext("Hello!");

        ChatChannel channel = new ChatChannel("Study Group", "group", "group.png");
        channel.setId(channelId);
        // Note: no participants added.

        when(chatChannelRepository.findById(channelId)).thenReturn(Optional.of(channel));
        // Even if user exists, he is not in channel.
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When & Then
        assertThrows(ResponseStatusException.class, () -> {
            chatService.sendMessage(channelId, postDTO);
        });
    }

    @Test
    void getChatHistory_validChannel_returnsMessages() {
        // Given
        Long channelId = 100L;
        ChatChannel channel = new ChatChannel("Study Group", "group", "group.png");
        channel.setId(channelId);
        when(chatChannelRepository.findById(channelId)).thenReturn(Optional.of(channel));

        Message message = new Message(channel, new User(), "Hello!");
        message.setId(200L);
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("id").ascending());
        Page<Message> messagePage = new PageImpl<>(List.of(message), pageable, 1);
        when(messageRepository.findByChannelId(channelId, pageable)).thenReturn(messagePage);

        // When
        List<Message> history = chatService.getChatHistory(channelId);

        // Then
        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals(200L, history.get(0).getId());
    }

    
    
    // UserTypingStatus test suite
    @Test
    void updateTypingStatus_validInput_success() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setStatus(UserStatus.ONLINE);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        // Simulate no existing typing record so that a new record is created.
        when(userTypingStatusRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userTypingStatusRepository.save(any(UserTypingStatus.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserTypingStatusPushDTO pushDTO = new UserTypingStatusPushDTO(userId, true);

        // When
        UserTypingStatusGetDTO result = chatService.updateTypingStatus(pushDTO);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertTrue(result.isTyping());
        assertEquals(UserStatus.ONLINE, result.getUserStatus());
    }



    @Test
    void getTypingStatus_validInput_success() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setStatus(UserStatus.ONLINE);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        
        UserTypingStatus typingStatus = new UserTypingStatus(user, true);
        when(userTypingStatusRepository.findByUserId(userId)).thenReturn(Optional.of(typingStatus));

        // When
        UserTypingStatusGetDTO result = chatService.getTypingStatus(userId);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertTrue(result.isTyping());
        assertEquals(UserStatus.ONLINE, result.getUserStatus());
    }
    

    @Test
    void updateTypingStatus_userNotFound_throwsException() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        UserTypingStatusPushDTO pushDTO = new UserTypingStatusPushDTO(userId, true);
        
        // Then
        assertThrows(ResponseStatusException.class, () -> {
            chatService.updateTypingStatus(pushDTO);
        });
    }

    @Test
    void deleteIndividualChannelBetweenUsers_channelExists_deletesChannel() {
        // Given
        Long blockerId = 1L;
        Long blockedUserId = 2L;
        
        // Create a fake individual channel that includes both the blocker and the blocked user.
        ChatChannel individualChannel = new ChatChannel();
        individualChannel.setId(100L);
        individualChannel.setType("individual");
        
        User blocker = new User();
        blocker.setId(blockerId);
        User blocked = new User();
        blocked.setId(blockedUserId);
        
        ChatParticipant participantBlocker = new ChatParticipant(blocker, "admin");
        ChatParticipant participantBlocked = new ChatParticipant(blocked, "member");
        
        // Create and set the participants list.
        List<ChatParticipant> participants = List.of(participantBlocker, participantBlocked);
        individualChannel.setParticipants(participants);
        
        // Simulate repository returning this channel for the blocker.
        when(chatChannelRepository.findByParticipantsUserId(blockerId))
                .thenReturn(List.of(individualChannel));
        
        // When: Invoke the method that deletes any individual channel between the two users.
        chatService.deleteIndividualChannelBetweenUsers(blockerId, blockedUserId);
        
        // Then: Verify that the delete method was called for the individual channel and that flush was performed.
        verify(chatChannelRepository, times(1)).delete(individualChannel);
        verify(chatChannelRepository, times(1)).flush();
    }

    @Test
    void deleteIndividualChannelBetweenUsers_noChannelExists_noDeletion() {
        // Given
        Long blockerId = 1L;
        Long blockedUserId = 2L;
        
        // Simulate repository returning an empty list for the blocker.
        when(chatChannelRepository.findByParticipantsUserId(blockerId))
                .thenReturn(List.of());
        
        // When: Invoke the method; no channel exists.
        chatService.deleteIndividualChannelBetweenUsers(blockerId, blockedUserId);
        
        // Then: Verify that delete is never called but flush() is still invoked.
        verify(chatChannelRepository, never()).delete(any(ChatChannel.class));
        verify(chatChannelRepository, times(1)).flush();
    }

    @Test
    void deleteIndividualChannelBetweenUsers_channelExistsButNoMatch_noDeletion() {
        // Given
        Long blockerId = 1L;
        Long blockedUserId = 2L;
        
        // Create a group channel that includes the blocker but does not contain the blocked user.
        ChatChannel groupChannel = new ChatChannel();
        groupChannel.setId(101L);
        groupChannel.setType("group");
        
        User blocker = new User();
        blocker.setId(blockerId);
        
        ChatParticipant participant = new ChatParticipant(blocker, "admin");
        groupChannel.setParticipants(List.of(participant));
        
        // Simulate repository returning the group channel.
        when(chatChannelRepository.findByParticipantsUserId(blockerId))
                .thenReturn(List.of(groupChannel));
        
        // When: Invoke the method; the channel is not individual or does not include blocked user.
        chatService.deleteIndividualChannelBetweenUsers(blockerId, blockedUserId);
        
        // Then: Verify that delete was not called on any channel.
        verify(chatChannelRepository, never()).delete(any(ChatChannel.class));
        verify(chatChannelRepository, times(1)).flush();
    }
}



