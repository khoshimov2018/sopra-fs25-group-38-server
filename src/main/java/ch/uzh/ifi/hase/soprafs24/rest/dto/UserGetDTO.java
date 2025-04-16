package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.ProfileKnowledgeLevel;
import ch.uzh.ifi.hase.soprafs24.constant.UserAvailability;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

import java.time.LocalDateTime;
import java.util.List;

public class UserGetDTO {

  private Long id;
  private String name;
  private String email;
  private UserStatus status;
  private String token;
  private LocalDateTime creationDate;

  private UserAvailability availability;
  private String studyLevel;
  private List<String> studyGoals;
  private String bio;
  private String profilePicture;
  private ProfileKnowledgeLevel knowledgeLevel;

  private List<UserCourseDTO> userCourses;

  // DTO for courses + level
  public static class UserCourseDTO {
    private Long courseId;
    private String courseName;
    private ProfileKnowledgeLevel knowledgeLevel;

    public Long getCourseId() {
      return courseId;
    }

    public void setCourseId(Long courseId) {
      this.courseId = courseId;
    }

    public String getCourseName() {
      return courseName;
    }

    public void setCourseName(String courseName) {
      this.courseName = courseName;
    }

    public ProfileKnowledgeLevel getKnowledgeLevel() {
      return knowledgeLevel;
    }

    public void setKnowledgeLevel(ProfileKnowledgeLevel knowledgeLevel) {
      this.knowledgeLevel = knowledgeLevel;
    }
  }

  // Getters & Setters

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public LocalDateTime getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(LocalDateTime creationDate) {
    this.creationDate = creationDate;
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

  public ProfileKnowledgeLevel getKnowledgeLevel() {
    return knowledgeLevel;
  }

  public void setKnowledgeLevel(ProfileKnowledgeLevel knowledgeLevel) {
    this.knowledgeLevel = knowledgeLevel;
  }

  public List<UserCourseDTO> getUserCourses() {
    return userCourses;
  }

  public void setUserCourses(List<UserCourseDTO> userCourses) {
    this.userCourses = userCourses;
  }
}
