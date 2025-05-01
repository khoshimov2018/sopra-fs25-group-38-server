package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.config.TestSecurityConfig;
import ch.uzh.ifi.hase.soprafs24.constant.ProfileKnowledgeLevel;
import ch.uzh.ifi.hase.soprafs24.constant.UserAvailability;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Course;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.CourseRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CourseSelectionDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserRegistrationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CourseRepository courseRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        
        if (courseRepository.findById(1L).isEmpty()) {
            courseRepository.save(new Course(1L, "Software Engineering"));
        }
        if (courseRepository.findById(2L).isEmpty()) {
            courseRepository.save(new Course(2L, "Artificial Intelligence"));
        }
    }

    @Test
    void testFrontendBackendUserRegistrationIntegration() throws Exception {
        UserPostDTO registrationData = new UserPostDTO();
        registrationData.setName("Test User");
        registrationData.setEmail("test.user@example.com");
        registrationData.setPassword("password123");
        registrationData.setStudyLevel("Bachelor");
        registrationData.setStudyGoals(Arrays.asList("Pass exams", "Deep understanding"));
        registrationData.setAvailability(UserAvailability.EVENING);
        registrationData.setBio("I am a test user");
        
        List<CourseSelectionDTO> courseSelections = new ArrayList<>();
        
        CourseSelectionDTO course1 = new CourseSelectionDTO();
        course1.setCourseId(1L);
        course1.setKnowledgeLevel(ProfileKnowledgeLevel.BEGINNER);
        
        CourseSelectionDTO course2 = new CourseSelectionDTO();
        course2.setCourseId(2L);
        course2.setKnowledgeLevel(ProfileKnowledgeLevel.INTERMEDIATE);
        
        courseSelections.add(course1);
        courseSelections.add(course2);
        registrationData.setCourseSelections(courseSelections);
        
        MvcResult result = mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(registrationData)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(registrationData.getName())))
                .andExpect(jsonPath("$.email", is(registrationData.getEmail())))
                .andExpect(jsonPath("$.status", is("ONLINE")))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();
                
        String responseContent = result.getResponse().getContentAsString();
        JsonNode responseJson = objectMapper.readTree(responseContent);
        Long userId = responseJson.get("id").asLong();
        String token = responseJson.get("token").asText();
        
        String authHeader = result.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Remove "Bearer " prefix
        }
        
        User savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new AssertionError("User not saved in database"));
        
        assertEquals(registrationData.getName(), savedUser.getName());
        assertEquals(registrationData.getEmail(), savedUser.getEmail());
        assertEquals(UserStatus.ONLINE, savedUser.getStatus());
        assertEquals(registrationData.getStudyLevel(), savedUser.getStudyLevel());
        assertEquals(String.join(",", registrationData.getStudyGoals()), savedUser.getStudyGoals());
        assertEquals(registrationData.getAvailability(), savedUser.getAvailability());
        assertEquals(registrationData.getBio(), savedUser.getBio());
        
        assertNotEquals(registrationData.getPassword(), savedUser.getPassword());
        
        assertNotNull(savedUser.getToken());
        assertEquals(token, savedUser.getToken());
        
        MvcResult profileResult = mockMvc.perform(get("/users/" + userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
                
        JsonNode profileJson = objectMapper.readTree(profileResult.getResponse().getContentAsString());
        JsonNode userCourses = profileJson.get("userCourses");
        
        assertEquals(2, userCourses.size());
        
        boolean foundCourse1 = false;
        boolean foundCourse2 = false;
        
        for (JsonNode courseNode : userCourses) {
            long courseId = courseNode.get("courseId").asLong();
            String knowledgeLevel = courseNode.get("knowledgeLevel").asText();
            
            if (courseId == 1) {
                foundCourse1 = true;
                assertEquals("BEGINNER", knowledgeLevel);
            } else if (courseId == 2) {
                foundCourse2 = true;
                assertEquals("INTERMEDIATE", knowledgeLevel);
            }
        }
        
        assertTrue(foundCourse1, "Course 1 not found in user courses");
        assertTrue(foundCourse2, "Course 2 not found in user courses");
        
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test.user@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId.intValue())))
                .andExpect(jsonPath("$.name", is(registrationData.getName())));
    }

    @Test
    void testRegistrationValidation_emailAlreadyExists() throws Exception {
        UserPostDTO firstUser = new UserPostDTO();
        firstUser.setName("First User");
        firstUser.setEmail("duplicate@example.com");
        firstUser.setPassword("password123");
        firstUser.setStudyLevel("Bachelor");
        firstUser.setStudyGoals(Arrays.asList("Pass exams"));
        
        CourseSelectionDTO course = new CourseSelectionDTO();
        course.setCourseId(1L);
        course.setKnowledgeLevel(ProfileKnowledgeLevel.BEGINNER);
        firstUser.setCourseSelections(Arrays.asList(course));
        
        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(firstUser)))
                .andExpect(status().isCreated());
                
        UserPostDTO secondUser = new UserPostDTO();
        secondUser.setName("Second User");
        secondUser.setEmail("duplicate@example.com"); // Same email
        secondUser.setPassword("differentPassword");
        secondUser.setStudyLevel("Master");
        secondUser.setStudyGoals(Arrays.asList("Deep understanding"));
        secondUser.setCourseSelections(Arrays.asList(course));
        
        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(secondUser)))
                .andExpect(status().isConflict());
                
        assertEquals(1, userRepository.findAll().stream()
                .filter(u -> u.getEmail().equals("duplicate@example.com"))
                .count());
    }

    @Test
    void testRegistrationValidation_invalidInput() throws Exception {
        UserPostDTO invalidUser = new UserPostDTO();
        invalidUser.setName("Invalid User");
        invalidUser.setEmail("invalid@example.com");
        
        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(invalidUser)))
                .andExpect(status().isBadRequest());
                
        UserPostDTO shortPasswordUser = new UserPostDTO();
        shortPasswordUser.setName("Short Password User");
        shortPasswordUser.setEmail("short@example.com");
        shortPasswordUser.setPassword("short"); // Less than 8 characters
        shortPasswordUser.setStudyLevel("Bachelor");
        shortPasswordUser.setStudyGoals(Arrays.asList("Pass exams"));
        
        CourseSelectionDTO course = new CourseSelectionDTO();
        course.setCourseId(1L);
        course.setKnowledgeLevel(ProfileKnowledgeLevel.BEGINNER);
        shortPasswordUser.setCourseSelections(Arrays.asList(course));
        
        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(shortPasswordUser)))
                .andExpect(status().isBadRequest());
                
        assertFalse(userRepository.existsByEmail("invalid@example.com"));
        assertFalse(userRepository.existsByEmail("short@example.com"));
    }


    private String asJsonString(final Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }
}