package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.StudyPlan;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.StudyPlanRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.service.StudyPlanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StudyPlanIntegrationTest {

    @Autowired
    private StudyPlanService studyPlanService;

    @Autowired
    private StudyPlanRepository studyPlanRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setup() {
        studyPlanRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword("Test1234!");
        testUser.setStatus(UserStatus.ONLINE);
        testUser.setName("Test User");
        testUser.setStudyGoals("Get good");
        testUser.setStudyLevel("Bachelor");
        testUser = userRepository.saveAndFlush(testUser);
    }

    @Test
    void addAndRetrieveStudyPlan_worksCorrectly() {
        // when
        studyPlanService.addStudyPlanForUser(testUser.getId(), "Finish Lecture 4");

        // then
        List<StudyPlan> plans = studyPlanService.getPlansByUserId(testUser.getId());
        assertEquals(1, plans.size());

        StudyPlan plan = plans.get(0);
        assertEquals(testUser.getId(), plan.getUserId());
        assertEquals("Finish Lecture 4", plan.getPlanContent());
        assertNotNull(plan.generatedDate());
    }
}
