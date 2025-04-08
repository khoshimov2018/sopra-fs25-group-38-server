package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.config.TestSecurityConfig;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserLoginDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Map;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserRestIntegrationTest
 * 
 * This is an integration test that verifies the end-to-end flow 
 * of the REST API for user stories, focusing on the protocol-level
 * behavior (HTTP methods, headers, status codes, etc.).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserRestIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }
    


/**
 * Integration Test 1: Complete User Registration Flow
 *
 * This test verifies that a new user can successfully register via the REST API:
 * 
 * Steps:
 * 1. Send a POST request to /users/register with valid registration data.
 * 2. Expect a 201 Created response with user information and an authentication token.
 * 3. Validate the response body contains all required fields (id, name, email, status, token).
 * 4. Confirm the user is persisted in the database.
 */

    @Test
    public void testUserRegistrationFlow() throws Exception {
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setName("Integration Tester");
        userPostDTO.setEmail("integration-test@gmail.com");
        userPostDTO.setPassword("password123");
        userPostDTO.setStudyLevel("Bachelor");
        userPostDTO.setStudyGoals(List.of("exam prep"));
    
        MvcResult result = mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())  // <-- fix here
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(userPostDTO.getName())))
                .andExpect(jsonPath("$.email", is(userPostDTO.getEmail())))
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();
    
        String responseContent = result.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(responseContent, Map.class);
        Integer userId = (Integer) responseMap.get("id");
        String token = (String) responseMap.get("token");
    
        assertNotNull(userId);
        assertNotNull(token);
        assertNotNull(userService.getUserById(userId.longValue()));
    }
    
    /**
         * Integration Test 2: User Registration with Duplicate Username
         *
         * Tests the REST protocol behavior when registering with a duplicate username:
         * 1. Register first user successfully
         * 2. Attempt to register second user with same email
         * 3. Verify 409 CONFLICT status code is returned with proper error message
         */
        @Test
        public void testDuplicateUserRegistration() throws Exception {
        UserPostDTO user = new UserPostDTO();
        user.setName("Test User");
        user.setEmail("duplicate@example.com");
        user.setPassword("Password123");
        user.setStudyLevel("Bachelor");
        user.setStudyGoals(List.of("exam prep"));

        // First registration should succeed
        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                .andExpect(status().isCreated());

        // Second registration with same email should fail
        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                .andExpect(status().isConflict());
        }

        /**
         * Integration Test 3: User Profile Retrieval
         *
         * This test verifies the full flow for retrieving a user's profile through the REST API.
         * It ensures that:
         * 1. A new user can successfully register via POST /users/register.
         * 2. The registration response contains a valid authorization token and user ID.
         * 3. The user's profile can be retrieved via GET /users/{userId} using the token.
         * 4. The response returns HTTP 200 OK with the correct user details (ID, name, email).
         */


        @Test
        public void testUserProfileRetrieval() throws Exception {
        // Register a user to get the token
        UserPostDTO registrationDTO = new UserPostDTO();
        registrationDTO.setName("Profile User");
        registrationDTO.setEmail("profile-test@example.com");
        registrationDTO.setPassword("password123");
        registrationDTO.setStudyLevel("Bachelor");
        registrationDTO.setStudyGoals(List.of("project work"));

        // Register the user
        MvcResult registrationResult = mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(registrationDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract token and user ID
        String responseContent = registrationResult.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(responseContent, Map.class);
        Integer userId = (Integer) responseMap.get("id");
        String token = registrationResult.getResponse().getHeader(HttpHeaders.AUTHORIZATION);

        // Retrieve the profile using the token
        mockMvc.perform(get("/users/" + userId)
                .header(HttpHeaders.AUTHORIZATION, token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userId)))
                .andExpect(jsonPath("$.name", is(registrationDTO.getName())))
                .andExpect(jsonPath("$.email", is(registrationDTO.getEmail())));
        }



    /**
     * Integration Test 5: Complete User Profile Update Flow
     * 
     * Tests the REST protocol behavior for updating a user profile:
     * 1. Register a user
     * 2. Extract token
     * 3. Update the user profile with PUT /users/{userId}
     * 4. Verify 204 NO_CONTENT status
     * 5. Verify the changes were made by retrieving the profile
     */
    /* @Test
    public void testUserProfileUpdate() throws Exception {
        // First register a user to get a token
        UserPostDTO registrationDTO = new UserPostDTO();
        registrationDTO.setName("Original Name");
        registrationDTO.setEmail("update-test");
        registrationDTO.setPassword("password123");
        
        // Register the user
        MvcResult registrationResult = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(registrationDTO)))
                .andExpect(status().isCreated())
                .andReturn();
                
        // Extract token and ID
        String responseContent = registrationResult.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(responseContent, Map.class);
        Integer userId = (Integer) responseMap.get("id");
        String token = registrationResult.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        
        // Create update DTO
        UserPostDTO updateDTO = new UserPostDTO();
        updateDTO.setName("Updated Name");
        updateDTO.setEmail("update-test"); // Username remains the same
        
        // Update the profile
        mockMvc.perform(put("/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(asJsonString(updateDTO)))
                .andExpect(status().isNoContent());
        
        // Verify that we can still access the profile after the update
        // Note: We're just checking we can retrieve the profile, not checking specific field values
        // This is because the actual update functionality might vary in implementation
        mockMvc.perform(get("/users/" + userId)
                .header(HttpHeaders.AUTHORIZATION, token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId)))
                .andExpect(jsonPath("$.username", is(updateDTO.getEmail())));
    }
 */
    /**
     * Integration Test 6: User Profile Update Without Authentication
     * 
     * Tests the REST protocol behavior when trying to update a profile without authentication:
     * 1. Register a user
     * 2. Try to update the profile without a token
     * 3. Verify 401 UNAUTHORIZED status
     */
   /*  @Test
    public void testProfileUpdateWithoutAuthentication() throws Exception {
        // First register a user
        UserPostDTO registrationDTO = new UserPostDTO();
        registrationDTO.setName("NoAuth User");
        registrationDTO.setEmail("no-auth-test");
        registrationDTO.setPassword("password123");
        
        // Register the user
        MvcResult registrationResult = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(registrationDTO)))
                .andExpect(status().isCreated())
                .andReturn();
                
        // Extract the user ID
        String responseContent = registrationResult.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(responseContent, Map.class);
        Integer userId = (Integer) responseMap.get("id");
        
        // Create update DTO
        UserPostDTO updateDTO = new UserPostDTO();
        updateDTO.setName("Updated Without Auth");
        
        // Try to update without token
        mockMvc.perform(put("/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateDTO)))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Helper Method to convert objects into a JSON string
     */
     private String asJsonString(final Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }  
} 