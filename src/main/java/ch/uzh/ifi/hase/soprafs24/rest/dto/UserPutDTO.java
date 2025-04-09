package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.ProfileKnowledgeLevel;
import ch.uzh.ifi.hase.soprafs24.constant.UserAvailability;

import java.util.List;

public class UserPutDTO {

    private String name;
    private String bio;
    private String profilePicture;
    private UserAvailability availability;
    private String studyLevel;
    private List<String> studyGoals;

    // Improved: Renamed + type-safe
    private List<CourseSelectionDTO> courses;

    public static class CourseSelectionDTO {
        private Long courseId;
        private ProfileKnowledgeLevel knowledgeLevel;

        public Long getCourseId() {
            return courseId;
        }

        public void setCourseId(Long courseId) {
            this.courseId = courseId;
        }

        public ProfileKnowledgeLevel getKnowledgeLevel() {
            return knowledgeLevel;
        }

        public void setKnowledgeLevel(ProfileKnowledgeLevel knowledgeLevel) {
            this.knowledgeLevel = knowledgeLevel;
        }
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public UserAvailability getAvailability() {
        return availability;
    }

    public void setAvailability(UserAvailability availability) {
        this.availability = availability;
    }

    public String getStudyLevel() {
        return studyLevel;
    }

    public void setStudyLevel(String studyLevel) {
        this.studyLevel = studyLevel;
    }

    public List<String> getStudyGoals() {
        return studyGoals;
    }

    public void setStudyGoals(List<String> studyGoals) {
        this.studyGoals = studyGoals;
    }

    public List<CourseSelectionDTO> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseSelectionDTO> courses) {
        this.courses = courses;
    }
}
