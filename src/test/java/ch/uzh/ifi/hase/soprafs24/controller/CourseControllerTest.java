package ch.uzh.ifi.hase.soprafs24.controller;
import ch.uzh.ifi.hase.soprafs24.entity.Course;
import ch.uzh.ifi.hase.soprafs24.repository.CourseRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;


/**
 * Test: GET /courses
 * Purpose: Verifies that the course list is successfully retrieved (200 OK).
 * Scenario: Two courses exist in DB; should return both.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @BeforeEach
    void setup() {
        courseRepository.deleteAll();
    }

    @Test
    public void getAllCourses_returns200OkAndCourses() throws Exception {
        // given
        courseRepository.save(new Course("Java"));
        courseRepository.save(new Course("Python"));

        // when/then
        mockMvc.perform(get("/courses")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].courseName", is("Java")))
                .andExpect(jsonPath("$[1].courseName", is("Python")));
    }
}
