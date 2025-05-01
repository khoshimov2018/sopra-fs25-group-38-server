package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.StudyPlan;
import ch.uzh.ifi.hase.soprafs24.entity.User;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class StudyPlanTest {
    @Test
    void testSettersAndGetters() {
        StudyPlan plan = new StudyPlan();

        // test data
        Long userId = 99L;
        String content = "Study Spring Boot";

        // setter
        plan.setUserId(userId);
        plan.setPlanContent(content);
        plan.setGeneratedDate(); 

        // getter 
        assertEquals(userId, plan.getUserId());
        assertEquals(content, plan.getPlanContent());
        assertEquals(LocalDate.now(), plan.generatedDate());
    }

    @Test
    void testConstructor_setsFieldsCorrectly() {
        User mockUser = new User();
        mockUser.setId(42L);

        StudyPlan plan = new StudyPlan(mockUser, "Read Chapter 3");

        assertEquals(42L, plan.getUserId());
        assertEquals("Read Chapter 3", plan.getPlanContent());
        assertEquals(LocalDate.now(), plan.generatedDate());
    }
}
