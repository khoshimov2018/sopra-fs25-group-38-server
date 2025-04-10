package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.ProfileKnowledgeLevel;
import ch.uzh.ifi.hase.soprafs24.constant.UserAvailability;
import ch.uzh.ifi.hase.soprafs24.entity.Course;
import ch.uzh.ifi.hase.soprafs24.repository.CourseRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CourseSelectionDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class StudentFilterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Course javaCourse;
    private Course pythonCourse;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        userRepository.flush();
        courseRepository.deleteAll();
        courseRepository.flush();

        javaCourse = getOrCreateCourse("Java");
        pythonCourse = getOrCreateCourse("Python");

        System.out.println("[DEBUG] Java Course ID: " + javaCourse.getId());
        System.out.println("[DEBUG] Python Course ID: " + pythonCourse.getId());
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
     * Test: GET /students?availability=EVENING
     * Purpose: Verifies users can be filtered by availability only (200 OK).
     * Scenario: Bob has EVENING availability; only Bob should be returned.
     */
    @Test
    public void getStudents_byAvailability_eveningUserReturned() throws Exception {
        registerUser("Bob", "bob@example.com", UserAvailability.EVENING, List.of(select(javaCourse.getId())));
        registerUser("Alice", "alice@example.com", UserAvailability.MORNING, List.of(select(pythonCourse.getId())));

        mockMvc.perform(get("/students").param("availability", "EVENING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email", is("bob@example.com")));
    }

    /**
    * Test: GET /students?courseIds={javaCourseId}
    * Purpose: Verifies users can be filtered by course ID only (200 OK).
    * Scenario: Charlie is enrolled in Java; only Charlie should be returned.
    */
    @Test
    public void getStudents_byCourseId_javaStudentReturned() throws Exception {
        registerUser("Charlie", "charlie@example.com", UserAvailability.MORNING, List.of(select(javaCourse.getId())));
        registerUser("Diana", "diana@example.com", UserAvailability.EVENING, List.of(select(pythonCourse.getId())));

        mockMvc.perform(get("/students").param("courseIds", String.valueOf(javaCourse.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email", is("charlie@example.com")));
    }

    /**
    * Test: GET /students?courseIds={javaCourseId}&availability=EVENING
    * Purpose: Verifies users can be filtered by both course and availability (200 OK).
    * Scenario: Eve matches both Java course and EVENING availability; only Eve should be returned.
    */
    @Test
    public void getStudents_byCourseAndAvailability_combinedFilterWorks() throws Exception {
        MvcResult result = registerUser("Eve", "eve@example.com", UserAvailability.EVENING, List.of(select(javaCourse.getId())));
        registerUser("Frank", "frank@example.com", UserAvailability.MORNING, List.of(select(javaCourse.getId())));
        registerUser("Grace", "grace@example.com", UserAvailability.EVENING, List.of(select(pythonCourse.getId())));

        String response = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(response);
        Long extractedCourseId = root.get("userCourses").get(0).get("courseId").asLong();
        System.out.println("[DEBUG] Extracted Course ID for Eve: " + extractedCourseId);

        mockMvc.perform(get("/students")
                        .param("availability", UserAvailability.EVENING.name())
                        .param("courseIds", String.valueOf(javaCourse.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email", is("eve@example.com")))
                .andExpect(jsonPath("$[0].userCourses[0].courseId", is(javaCourse.getId().intValue())));
    }

    // --------------------------- 
    private MvcResult registerUser(String name, String email, UserAvailability availability,
                                   List<CourseSelectionDTO> selections) throws Exception {
        UserPostDTO dto = new UserPostDTO();
        dto.setName(name);
        dto.setEmail(email);
        dto.setPassword("password123");
        dto.setStudyLevel("Bachelor");
        dto.setStudyGoals(List.of("Study hard"));
        dto.setAvailability(availability);
        dto.setCourseSelections(selections);

        return mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();
    }

    private CourseSelectionDTO select(Long courseId) {
        CourseSelectionDTO dto = new CourseSelectionDTO();
        dto.setCourseId(courseId);
        dto.setKnowledgeLevel(ProfileKnowledgeLevel.BEGINNER); 
        return dto;
    }
}