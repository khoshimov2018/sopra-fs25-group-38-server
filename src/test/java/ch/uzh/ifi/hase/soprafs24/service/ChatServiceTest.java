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
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatChannelPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.MessagePostDTO;
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

public class ChatServiceTest {

    @Mock
    private ChatChannelRepository chatChannelRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChatService chatService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createChatChannel_validInput_success() {
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
    public void sendMessage_validInput_success() {
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
    public void sendMessage_senderNotParticipant_throwsException() {
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
    public void getChatHistory_validChannel_returnsMessages() {
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
}



