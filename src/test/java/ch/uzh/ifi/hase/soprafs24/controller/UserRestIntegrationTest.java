package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.config.TestSecurityConfig;
import ch.uzh.ifi.hase.soprafs24.constant.ProfileKnowledgeLevel;
import ch.uzh.ifi.hase.soprafs24.constant.UserAvailability;
import ch.uzh.ifi.hase.soprafs24.entity.Course;
import ch.uzh.ifi.hase.soprafs24.repository.CourseRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CourseSelectionDTO;
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

import java.util.Map;
import java.util.List;

import static org.hamcrest.Matchers.is;
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
class UserRestIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;
    
    @Autowired
    private CourseRepository courseRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
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
        void testUserRegistrationFlow() throws Exception {
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
 

    

        @Test
        void testUserProfileRetrieval() throws Exception {
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