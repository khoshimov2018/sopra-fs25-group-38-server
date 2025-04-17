package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.config.TestSecurityConfig;
import ch.uzh.ifi.hase.soprafs24.constant.ProfileKnowledgeLevel;
import ch.uzh.ifi.hase.soprafs24.constant.UserAvailability;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Course;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.UserCourse;
import ch.uzh.ifi.hase.soprafs24.repository.CourseRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CourseSelectionDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserLoginDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
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
import ch.uzh.ifi.hase.soprafs24.rest.dto.CourseSelectionDTO;


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
    
    @Autowired
    private CourseRepository courseRepository;

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
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
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
        /* @Test
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
        } */

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
        // First, ensure a course with ID 1 exists
        // The test is failing because the course doesn't exist in the test database
        Course course = new Course(1L, "AI");
        courseRepository.save(course);
        
        UserPostDTO registrationDTO = new UserPostDTO();
        registrationDTO.setName("Profile User");
        registrationDTO.setEmail("profile-test@example.com");
        registrationDTO.setPassword("password123");
        registrationDTO.setStudyLevel("Bachelor");
        registrationDTO.setStudyGoals(List.of("project work"));
        registrationDTO.setAvailability(UserAvailability.EVENING);

        // Include one course with knowledge level
        CourseSelectionDTO courseSelection = new CourseSelectionDTO();
        courseSelection.setCourseId(1L); // assumes course with ID 1 exists
        courseSelection.setKnowledgeLevel(ProfileKnowledgeLevel.BEGINNER); 
        registrationDTO.setCourseSelections(List.of(courseSelection));

        // Register the user
        MvcResult registrationResult = mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(registrationDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = registrationResult.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(responseContent, Map.class);
        Integer userId = (Integer) responseMap.get("id");
        
        // Extract token from either the Authorization header or the response body
        String token = registrationResult.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        if (token == null) {
            // If not in header, try to get it from the response body
            token = (String) responseMap.get("token");
            if (token != null && !token.startsWith("Bearer ")) {
                token = "Bearer " + token;
            }
        }

        // Retrieve the profile using the token
        mockMvc.perform(get("/users/" + userId)
                .header(HttpHeaders.AUTHORIZATION, token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userId)))
                .andExpect(jsonPath("$.name", is(registrationDTO.getName())))
                .andExpect(jsonPath("$.email", is(registrationDTO.getEmail())))
                .andExpect(jsonPath("$.studyLevel", is(registrationDTO.getStudyLevel())))
                .andExpect(jsonPath("$.availability", is("EVENING")))
                .andExpect(jsonPath("$.studyGoals[0]", is("project work")))
                .andExpect(jsonPath("$.userCourses[0].courseId", is(1)))
                .andExpect(jsonPath("$.userCourses[0].knowledgeLevel", is("BEGINNER")));
        }


/*         @Test
        public void testUserProfileUpdate_success() throws Exception {
        // 1. Register user
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setName("Original Name");
        userPostDTO.setEmail("update-test@example.com");
        userPostDTO.setPassword("originalPassword");
        userPostDTO.setStudyLevel("Bachelor");
        userPostDTO.setStudyGoals(List.of("prep"));
        userPostDTO.setAvailability(UserAvailability.EVENING);

        CourseSelectionDTO courseSelection = new CourseSelectionDTO();
        courseSelection.setCourseId(1L); // assumes course ID 1 exists
        courseSelection.setKnowledgeLevel(ProfileKnowledgeLevel.BEGINNER);
        userPostDTO.setCourseSelections(List.of(courseSelection));

        MvcResult registrationResult = mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = registrationResult.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(responseContent, Map.class);
        Integer userId = (Integer) responseMap.get("id");
        
        // Extract token from either the Authorization header or the response body
        String token = registrationResult.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        if (token == null) {
            // If not in header, try to get it from the response body
            token = (String) responseMap.get("token");
            if (token != null && !token.startsWith("Bearer ")) {
                token = "Bearer " + token;
            }
        }

        // 2. Prepare update DTO
        UserPutDTO updateDTO = new UserPutDTO();
        updateDTO.setName("Updated Name");
        updateDTO.setBio("Updated bio");
        updateDTO.setStudyLevel("Master");
        updateDTO.setStudyGoals(List.of("career"));
        updateDTO.setAvailability(UserAvailability.MORNING);

        CourseSelectionDTO updatedCourse = new CourseSelectionDTO();
        updatedCourse.setCourseId(1L);
        updatedCourse.setKnowledgeLevel(ProfileKnowledgeLevel.ADVANCED);
        updateDTO.setCourseSelections(List.of(updatedCourse));

        // 3. Perform PUT /users/{id}
        mockMvc.perform(put("/users/" + userId)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateDTO)))
                .andExpect(status().isNoContent());

        // 4. Fetch updated user and verify fields
        mockMvc.perform(get("/users/" + userId)
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$.bio", is("Updated bio")))
                .andExpect(jsonPath("$.studyLevel", is("Master")))
                .andExpect(jsonPath("$.studyGoals[0]", is("career")))
                .andExpect(jsonPath("$.availability", is("MORNING")))
                .andExpect(jsonPath("$.userCourses[0].courseId", is(1)))
                .andExpect(jsonPath("$.userCourses[0].knowledgeLevel", is("ADVANCED")));
        } */

        

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