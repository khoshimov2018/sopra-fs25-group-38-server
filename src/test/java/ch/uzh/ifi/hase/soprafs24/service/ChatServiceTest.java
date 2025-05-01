package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
// utils
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

// junit & Mockito
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
        ChatChannelPostDTO postDTO = new ChatChannelPostDTO();
        postDTO.setChannelName("Study Group");
        postDTO.setChannelType("group");
        postDTO.setChannelProfileImage("group.png");
        postDTO.setParticipantIds(List.of(1L, 2L));

        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        when(chatChannelRepository.save(any(ChatChannel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ChatChannel result = chatService.createChatChannel(postDTO);

        assertNotNull(result);
        assertEquals("Study Group", result.getName());
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

        // Add a participant to the channel
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

        Message result = chatService.sendMessage(channelId, postDTO);

        assertNotNull(result);
        assertEquals(200L, result.getId());
        assertEquals("Hello!", result.getContent());
        assertEquals(user, result.getSender());
    }

    @Test
    void sendMessage_senderNotParticipant_throwsException() {
        Long channelId = 100L;
        MessagePostDTO postDTO = new MessagePostDTO();
        postDTO.setSenderId(1L);
        postDTO.setContext("Hello!");

        ChatChannel channel = new ChatChannel("Study Group", "group", "group.png");
        channel.setId(channelId);

        when(chatChannelRepository.findById(channelId)).thenReturn(Optional.of(channel));
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(ResponseStatusException.class, () -> {
            chatService.sendMessage(channelId, postDTO);
        });
    }

    @Test
    void getChatHistory_validChannel_returnsMessages() {
        Long channelId = 100L;
        ChatChannel channel = new ChatChannel("Study Group", "group", "group.png");
        channel.setId(channelId);
        when(chatChannelRepository.findById(channelId)).thenReturn(Optional.of(channel));

        Message message = new Message(channel, new User(), "Hello!");
        message.setId(200L);
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("id").ascending());
        Page<Message> messagePage = new PageImpl<>(List.of(message), pageable, 1);
        when(messageRepository.findByChannelId(channelId, pageable)).thenReturn(messagePage);

        List<Message> history = chatService.getChatHistory(channelId);

        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals(200L, history.get(0).getId());
    }

    
    
    // UserTypingStatus test suite
    @Test
    void updateTypingStatus_validInput_success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setStatus(UserStatus.ONLINE);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userTypingStatusRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userTypingStatusRepository.save(any(UserTypingStatus.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserTypingStatusPushDTO pushDTO = new UserTypingStatusPushDTO(userId, true);

        UserTypingStatusGetDTO result = chatService.updateTypingStatus(pushDTO);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertTrue(result.isTyping());
        assertEquals(UserStatus.ONLINE, result.getUserStatus());
    }



    @Test
    void getTypingStatus_validInput_success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setStatus(UserStatus.ONLINE);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        
        UserTypingStatus typingStatus = new UserTypingStatus(user, true);
        when(userTypingStatusRepository.findByUserId(userId)).thenReturn(Optional.of(typingStatus));

        UserTypingStatusGetDTO result = chatService.getTypingStatus(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertTrue(result.isTyping());
        assertEquals(UserStatus.ONLINE, result.getUserStatus());
    }
    

    @Test
    void updateTypingStatus_userNotFound_throwsException() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        UserTypingStatusPushDTO pushDTO = new UserTypingStatusPushDTO(userId, true);
        
        assertThrows(ResponseStatusException.class, () -> {
            chatService.updateTypingStatus(pushDTO);
        });
    }

    @Test
    void deleteIndividualChannelBetweenUsers_channelExists_deletesChannel() {
        Long blockerId = 1L;
        Long blockedUserId = 2L;
        
        ChatChannel individualChannel = new ChatChannel();
        individualChannel.setId(100L);
        individualChannel.setType("individual");
        
        User blocker = new User();
        blocker.setId(blockerId);
        User blocked = new User();
        blocked.setId(blockedUserId);
        
        ChatParticipant participantBlocker = new ChatParticipant(blocker, "admin");
        ChatParticipant participantBlocked = new ChatParticipant(blocked, "member");
        
        List<ChatParticipant> participants = List.of(participantBlocker, participantBlocked);
        individualChannel.setParticipants(participants);
        
        when(chatChannelRepository.findByParticipantsUserId(blockerId))
                .thenReturn(List.of(individualChannel));
        
        chatService.deleteIndividualChannelBetweenUsers(blockerId, blockedUserId);
        
        verify(chatChannelRepository, times(1)).delete(individualChannel);
        verify(chatChannelRepository, times(1)).flush();
    }

    @Test
    void deleteIndividualChannelBetweenUsers_noChannelExists_noDeletion() {

        Long blockerId = 1L;
        Long blockedUserId = 2L;

        when(chatChannelRepository.findByParticipantsUserId(blockerId))
                .thenReturn(List.of());

        chatService.deleteIndividualChannelBetweenUsers(blockerId, blockedUserId);
        
        verify(chatChannelRepository, never()).delete(any(ChatChannel.class));
        verify(chatChannelRepository, times(1)).flush();
    }

    @Test
    void deleteIndividualChannelBetweenUsers_channelExistsButNoMatch_noDeletion() {
        Long blockerId = 1L;
        Long blockedUserId = 2L;
        
        ChatChannel groupChannel = new ChatChannel();
        groupChannel.setId(101L);
        groupChannel.setType("group");
        
        User blocker = new User();
        blocker.setId(blockerId);
        
        ChatParticipant participant = new ChatParticipant(blocker, "admin");
        groupChannel.setParticipants(List.of(participant));
        
        when(chatChannelRepository.findByParticipantsUserId(blockerId))
                .thenReturn(List.of(groupChannel));
        
        chatService.deleteIndividualChannelBetweenUsers(blockerId, blockedUserId);
        
        verify(chatChannelRepository, never()).delete(any(ChatChannel.class));
        verify(chatChannelRepository, times(1)).flush();
    }
    
    // negative tests for creating chat channel method

    @Test
    void createChatChannel_participantNull_throwsBadRequest() {
        ChatChannelPostDTO dto = new ChatChannelPostDTO();
        dto.setChannelType("group");
        dto.setParticipantIds(null);                       

        ResponseStatusException ex =
            assertThrows(ResponseStatusException.class,
                        () -> chatService.createChatChannel(dto));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

   
    @Test
    void createChatChannel_participantEmpty_throwsBadRequest() {
        ChatChannelPostDTO dto = new ChatChannelPostDTO();
        dto.setChannelType("group");
        dto.setParticipantIds(List.of());                  

        ResponseStatusException ex =
            assertThrows(ResponseStatusException.class,
                        () -> chatService.createChatChannel(dto));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void createChatChannel_individualWrongSize_throwsBadRequest() {
        // prepare DTO with three ids instead of two
        ChatChannelPostDTO dto = new ChatChannelPostDTO();
        dto.setChannelType("individual");
        dto.setParticipantIds(List.of(1L, 2L, 3L));

        // stub all three users so size-check is reached
        when(userRepository.findById(anyLong()))
            .thenAnswer(inv ->
                Optional.of(new User())   // return dummy user for 1,2,3
            );

        ResponseStatusException ex =
            assertThrows(ResponseStatusException.class,
                        () -> chatService.createChatChannel(dto));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void createChatChannel_userMissing_throwsNotFound() {
        ChatChannelPostDTO dto = new ChatChannelPostDTO();
        dto.setChannelType("group");
        dto.setParticipantIds(List.of(1L, 2L));

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());  // <- missing

        ResponseStatusException ex =
            assertThrows(ResponseStatusException.class,
                        () -> chatService.createChatChannel(dto));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

}



