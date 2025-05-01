package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.StudyPlan;
import ch.uzh.ifi.hase.soprafs24.entity.User;

import ch.uzh.ifi.hase.soprafs24.repository.StudyPlanRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyPlanService {

    private final StudyPlanRepository studyPlanRepository;
    private final UserRepository userRepository;

    @Autowired
    public StudyPlanService(StudyPlanRepository studyPlanRepository, UserRepository userRepository) {
        this.studyPlanRepository = studyPlanRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get all study plans for a given user ID
     */
    public List<StudyPlan> getPlansByUserId(Long userId) {
        return studyPlanRepository.findByUserId(userId);
    }

    /**
     * Add a new study plan for a given user and content
     */
    public void addStudyPlanForUser(Long userId, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        StudyPlan plan = new StudyPlan(user, content);
        studyPlanRepository.save(plan);
    }
}