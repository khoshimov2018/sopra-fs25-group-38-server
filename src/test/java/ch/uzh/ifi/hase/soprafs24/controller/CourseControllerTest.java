package ch.uzh.ifi.hase.soprafs24.controller;
import ch.uzh.ifi.hase.soprafs24.constant.ProfileKnowledgeLevel;
import ch.uzh.ifi.hase.soprafs24.entity.Course;
import ch.uzh.ifi.hase.soprafs24.entity.UserCourse;
import ch.uzh.ifi.hase.soprafs24.repository.CourseRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CoursePostDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.containsInAnyOrder;

import java.util.List;


/**
 * Test: GET /courses
 * Purpose: Verifies that the course list is successfully retrieved (200 OK).
 * Scenario: Two courses exist in DB; should return both.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @BeforeEach
    void setup() {
        courseRepository.deleteAll();
    }

    @Test
    void getAllCourses_returns200OkAndCourses() throws Exception {
        // given
        courseRepository.deleteAll(); // Ensure a clean state
        courseRepository.save(new Course("Java"));
        courseRepository.save(new Course("Python"));
    
        // when/then
        mockMvc.perform(get("/courses")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].courseName", containsInAnyOrder("Java", "Python")));
    }
    
    /**
     * Test: POST /courses
     * Purpose: Verifies that a new course can be added (201 Created).
     * Scenario: A course with name "Deep Learning" is sent and saved.
     */
    @Test
    void addNewCourse_returns200OK() throws Exception {
        String courseJson = "{ \"courseName\": \"Deep Learning\" }";

        mockMvc.perform(post("/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(courseJson))
                .andExpect(status().isCreated());
            }

    /**
     * Test: CoursePostDTO Setters and Getters
     * Purpose: Verifies that the getters and setters of the CoursePostDTO work as expected.
     * Scenario: A CoursePostDTO object is created, and its properties are validated using assertions.
     */
    @Test
    void testCoursePostDTOSettersAndGetters() {
        CoursePostDTO dto = new CoursePostDTO();
        List<Long> ids = List.of(1L, 2L);
        dto.setCourseIds(ids);
        dto.setCourseName("AI");

        assertEquals(ids, dto.getCourseIds());
        assertEquals("AI", dto.getCourseName());
    }         
}
