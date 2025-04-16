package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.ProfileKnowledgeLevel;
import ch.uzh.ifi.hase.soprafs24.constant.UserAvailability;

import java.util.List;

public class UserPutDTO {

    private String name;
    private String email;
    private String bio;
    private String studyLevel;
    private List<String> studyGoals;
    private UserAvailability availability;
    private List<CourseSelectionDTO> courseSelections;

    // Getters & Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
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

    public UserAvailability getAvailability() {
        return availability;
    }

    public void setAvailability(UserAvailability availability) {
        this.availability = availability;
    }

    public List<CourseSelectionDTO> getCourseSelections() {
        return courseSelections;
    }

    public void setCourseSelections(List<CourseSelectionDTO> courseSelections) {
        this.courseSelections = courseSelections;
    }

    private String profilePicture;

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

}
