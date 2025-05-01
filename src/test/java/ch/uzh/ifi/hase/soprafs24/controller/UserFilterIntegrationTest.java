package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.ProfileKnowledgeLevel;
import ch.uzh.ifi.hase.soprafs24.constant.UserAvailability;
import ch.uzh.ifi.hase.soprafs24.entity.Course;
import ch.uzh.ifi.hase.soprafs24.repository.CourseRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CourseSelectionDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Integration Test: User and Course Filtering Logic
 * 
 * This class verifies that filtering users by course and availability works
 * correctly, and that all courses can be retrieved from the system.
 * 
 * Flow:
 * 1. Setup creates two courses: Java and Python
 * 2. Two users are registered using POST /users/register with different availability and courses
 * 3. Several GET requests are performed to test:
 *    - Retrieval of all courses
 *    - Filtering users by courseId
 *    - Filtering users by availability
 * 
 *  Notes:
 * - Courses are dynamically created and their IDs extracted from the DB
 * - Users are registered with different combinations of course and availability
 * - All responses are validated using jsonPath assertions to confirm expected filtering
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Skip auth for testing
class UserFilterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Course savedCourse1;
    private Course savedCourse2;

    private String bobEmail;
    private String claudeEmail;

    @BeforeEach
    void setup() throws Exception {
        userRepository.deleteAll();
        courseRepository.deleteAll();

        savedCourse1 = getOrCreateCourse("Java");
        savedCourse2 = getOrCreateCourse("Python");

        bobEmail = registerUserAndGetEmail("Bob", UserAvailability.MORNING, List.of(savedCourse1.getId()));
        claudeEmail = registerUserAndGetEmail("Claude", UserAvailability.EVENING, List.of(savedCourse2.getId()));
    }

    private Course getOrCreateCourse(String courseName) {
        return courseRepository.findByCourseName(courseName)
            .orElseGet(() -> {
                Long manualId = switch (courseName) {
                    case "Java" -> 1L;
                    case "Python" -> 2L;
                    default -> throw new IllegalArgumentException("Unknown course: " + courseName);
                };
                Course newCourse = new Course(manualId, courseName);
                return courseRepository.save(newCourse);
            });
    }

    /**
     * S6. Test that all courses are successfully retrieved.
     */
    @Test
    void getCourses_returnsAllCourses() throws Exception {
        mockMvc.perform(get("/courses")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].courseName", is("Java")))
            .andExpect(jsonPath("$[1].courseName", is("Python")));
    }

    /**
     * S9. Test that student is successfully filtered by courseId.
     */
    @Test
    void getStudents_filteredByCourseId_returnsBob() throws Exception {
        mockMvc.perform(get("/students")
                .param("courseIds", savedCourse1.getId().toString())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].email", is(bobEmail)));
    }

    /**
     * S8. Test that student is successfully filtered by availability.
     */
    @Test
    void getStudents_filteredByAvailability_returnsClaude() throws Exception {
        mockMvc.perform(get("/students")
                .param("availability", "EVENING")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].email", is(claudeEmail)));
    }   

    private String registerUserAndGetEmail(String name, UserAvailability availability, List<Long> courseIds) throws Exception {
        String randomEmail = generateRandomEmail(name);
    
        // Convert courseIds to CourseSelectionDTOs
        List<CourseSelectionDTO> courseSelections = courseIds.stream().map((Long id) -> {
            CourseSelectionDTO selection = new CourseSelectionDTO();
            selection.setCourseId(id);
            selection.setKnowledgeLevel(ProfileKnowledgeLevel.BEGINNER); 
            return selection;
        }).toList();
    
        UserPostDTO dto = new UserPostDTO();
        dto.setName(name);
        dto.setEmail(randomEmail);
        dto.setPassword("password123");
        dto.setStudyLevel("Bachelor");
        dto.setStudyGoals(List.of("get better"));
        dto.setAvailability(availability);
        dto.setCourseSelections(courseSelections); 
    
        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated());
    
        return randomEmail;
    }    

    private String generateRandomEmail(String name) {
        return name.toLowerCase() + "+" + UUID.randomUUID().toString().substring(0, 6) + "@example.com";
    }
}
