package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.ProfileKnowledgeLevel;
import ch.uzh.ifi.hase.soprafs24.entity.Course;
import ch.uzh.ifi.hase.soprafs24.entity.UserCourse;
import ch.uzh.ifi.hase.soprafs24.entity.User;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CourseTest {

    /**
     * Test: Entity - Course Constructors and Getters
     * Purpose: Verifies that the constructors and getters of the Course entity work as expected.
     * Scenario: Test the constructors of the Course entity and check if the courseName and id are correctly initialized.
     */
    @Test
    void testConstructorsAndGetters() {
        Course c1 = new Course("AI");
        assertEquals("AI", c1.getCourseName());

        Course c2 = new Course(10L, "ML");
        assertEquals(10L, c2.getId());
        assertEquals("ML", c2.getCourseName());
    }

    /**
     * Test: UserCourse Constructor
     * Purpose: Verifies that the UserCourse constructor correctly initializes the association between User, Course, and KnowledgeLevel.
     * Scenario: A new UserCourse is created with a user, course, and profile knowledge level. The constructor should correctly set these values.
     */
    @Test
    void testUserCourseConstructor() {
        User user = new User();
        Course course = new Course("ML");
        ProfileKnowledgeLevel level = ProfileKnowledgeLevel.BEGINNER;

        UserCourse uc = new UserCourse(user, course, level);

        assertEquals(user, uc.getUser());
        assertEquals(course, uc.getCourse());
        assertEquals(level, uc.getKnowledgeLevel());
    }
  
    /**
     * Test: Setters and List Initialization
     * Purpose: Verifies that the setters and the userCourses list are initialized correctly.
     * Scenario: A new Course entity is created, the name is set using the setter, and it is checked if the list of userCourses is initialized.
     */
    @Test
    void testSettersAndList() {
        Course course = new Course();
        course.setCourseName("SE");
        assertEquals("SE", course.getCourseName());

        assertNotNull(course.getUserCourses());
        assertTrue(course.getUserCourses().isEmpty());
    }
}
