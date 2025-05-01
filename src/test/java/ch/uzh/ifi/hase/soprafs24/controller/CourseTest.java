package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Course;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CourseTest {

    @Test
    void testConstructorsAndGetters() {
        Course c1 = new Course("AI");
        assertEquals("AI", c1.getCourseName());

        Course c2 = new Course(10L, "ML");
        assertEquals(10L, c2.getId());
        assertEquals("ML", c2.getCourseName());
    }

    @Test
    void testSettersAndList() {
        Course course = new Course();
        course.setCourseName("SE");
        assertEquals("SE", course.getCourseName());

        assertNotNull(course.getUserCourses());
        assertTrue(course.getUserCourses().isEmpty());
    }
}
