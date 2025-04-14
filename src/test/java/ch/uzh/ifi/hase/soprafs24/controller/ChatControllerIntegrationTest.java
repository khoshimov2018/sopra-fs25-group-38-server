package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ChatChannelRepository;
import ch.uzh.ifi.hase.soprafs24.repository.MessageRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatChannelPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.MessagePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserTypingStatusPushDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters (and CSRF) for testing
@Transactional
public class ChatControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatChannelRepository chatChannelRepository;

    @Autowired
    private MessageRepository messageRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    // Test users used in our integration tests.
    private User userAlice;
    private User userBob;
    private User userCarol;

    @BeforeEach
    public void setup() {
        // Clear repositories to ensure test isolation.
        messageRepository.deleteAll();
        chatChannelRepository.deleteAll();
        userRepository.deleteAll();

        // Create test users with all required fields.
        userAlice = new User();
        userAlice.setName("Alice");
        userAlice.setEmail("alice@example.com");
        userAlice.setPassword("password");
        userAlice.setToken("tokenAlice");
        userAlice.setStatus(UserStatus.ONLINE);
        userAlice.setStudyLevel("BEGINNER");
        userAlice = userRepository.save(userAlice);

        userBob = new User();
        userBob.setName("Bob");
        userBob.setEmail("bob@example.com");
        userBob.setPassword("password");
        userBob.setToken("tokenBob");
        userBob.setStatus(UserStatus.ONLINE);
        userBob.setStudyLevel("BEGINNER");
        userBob = userRepository.save(userBob);

        userCarol = new User();
        userCarol.setName("Carol");
        userCarol.setEmail("carol@example.com");
        userCarol.setPassword("password");
        userCarol.setToken("tokenCarol");
        userCarol.setStatus(UserStatus.ONLINE);
        userCarol.setStudyLevel("BEGINNER");
        userCarol = userRepository.save(userCarol);
    }

    // Test the channel creation endpoint.
    @Test
    public void testCreateChatChannel_group() throws Exception {
        ChatChannelPostDTO postDTO = new ChatChannelPostDTO();
        postDTO.setChannelName("Study Group");
        postDTO.setChannelType("group");
        postDTO.setChannelProfileImage("group.png");
        postDTO.setParticipantIds(Arrays.asList(userAlice.getId(), userBob.getId(), userCarol.getId()));

        String postContent = objectMapper.writeValueAsString(postDTO);

        mockMvc.perform(post("/chat/channels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postContent))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.channelId").exists())
                .andExpect(jsonPath("$.channelName", is("Study Group")))
                .andExpect(jsonPath("$.channelType", is("group")))
                .andExpect(jsonPath("$.channelProfileImage", is("group.png")))
                .andExpect(jsonPath("$.participants", hasSize(3)));
    }

    // Test retrieving channels for a specific user.
    @Test
    public void testGetChannelsForUser() throws Exception {
        // First, create a channel.
        ChatChannelPostDTO postDTO = new ChatChannelPostDTO();
        postDTO.setChannelName("Group Chat");
        postDTO.setChannelType("group");
        postDTO.setChannelProfileImage("chat.png");
        postDTO.setParticipantIds(Arrays.asList(userAlice.getId(), userBob.getId()));

        String postContent = objectMapper.writeValueAsString(postDTO);

        mockMvc.perform(post("/chat/channels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postContent))
                .andExpect(status().isCreated());

        // Retrieve channels for userAlice.
        mockMvc.perform(get("/chat/channels/user/" + userAlice.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].channelId").exists());
    }

    // Test sending a message and retrieving chat history.
    @Test
    public void testSendMessage_andGetChatHistory() throws Exception {
        // Create a chat channel that includes userAlice and userBob.
        ChatChannelPostDTO postDTO = new ChatChannelPostDTO();
        postDTO.setChannelName("Discussion");
        postDTO.setChannelType("group");
        postDTO.setChannelProfileImage("discussion.png");
        postDTO.setParticipantIds(Arrays.asList(userAlice.getId(), userBob.getId()));

        String postContent = objectMapper.writeValueAsString(postDTO);

        String createChannelResponse = mockMvc.perform(post("/chat/channels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postContent))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract the channelId from the response.
        Long channelId = objectMapper.readTree(createChannelResponse).get("channelId").asLong();

        // Build the message to send from userAlice.
        MessagePostDTO messagePostDTO = new MessagePostDTO();
        messagePostDTO.setSenderId(userAlice.getId());
        messagePostDTO.setContext("Hello, everyone!");

        String messageContent = objectMapper.writeValueAsString(messagePostDTO);

        // Send the message.
        mockMvc.perform(post("/chat/" + channelId + "/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(messageContent))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.messageId").exists())
                .andExpect(jsonPath("$.senderId", is(userAlice.getId().intValue())))
                .andExpect(jsonPath("$.channelId", is(channelId.intValue())))
                .andExpect(jsonPath("$.context", is("Hello, everyone!")))
                // Instead of checking for existence (which fails if value is null), check that the value is null.
                .andExpect(jsonPath("$.senderProfileImage", nullValue()));

        // Verify that the chat history contains the sent message.
        mockMvc.perform(get("/chat/channels/" + channelId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].messageId").exists())
                .andExpect(jsonPath("$[0].context", is("Hello, everyone!")));
    }

    // UserTypingStatus test suite
    @Test
    public void testUpdateTypingStatus_endpoint() throws Exception {
        Long userId = userAlice.getId();
        // Prepare the push DTO.
        UserTypingStatusPushDTO pushDTO = new UserTypingStatusPushDTO(userId, true);
        String jsonBody = objectMapper.writeValueAsString(pushDTO);

        mockMvc.perform(put("/chat/typing")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(userId.intValue())))
                .andExpect(jsonPath("$.typing", is(true)))
                .andExpect(jsonPath("$.userStatus", is("ONLINE")));
    }
    
    @Test
    public void testGetTypingStatus_endpoint() throws Exception {
        Long userId = userAlice.getId();

        // First, update the typing status using the PUT endpoint.
        UserTypingStatusPushDTO pushDTO = new UserTypingStatusPushDTO(userId, false);
        String jsonBody = objectMapper.writeValueAsString(pushDTO);
        
        mockMvc.perform(put("/chat/typing")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk());

        // Then, retrieve the typing status using the GET endpoint.
        mockMvc.perform(get("/chat/typing/" + userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(userId.intValue())))
                .andExpect(jsonPath("$.typing", is(false)))
                .andExpect(jsonPath("$.userStatus", is("ONLINE")));
    }
}
