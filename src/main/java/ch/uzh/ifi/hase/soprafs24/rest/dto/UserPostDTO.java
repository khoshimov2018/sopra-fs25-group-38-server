package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.ProfileKnowledgeLevel;
import ch.uzh.ifi.hase.soprafs24.constant.UserAvailability;
import java.util.List;


public class UserPostDTO {

  private String name;

  private String email;
  
  private String password;

  private List<Long> courseIds;

  private String studyLevel;        
  private List<String> studyGoals;       
  private String profilePicture;

    private String bio;
    private UserAvailability availability;
    private ProfileKnowledgeLevel knowledgeLevel;

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
  
  public String getPassword() {
    return password;
  }
  
  public void setPassword(String password) {
    this.password = password;
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

  public String getProfilePicture() {
    return profilePicture;
  }

  public void setProfilePicture(String profilePicture) {
    this.profilePicture = profilePicture;
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public UserAvailability getAvailability() {
    return availability;
  }

  public void setAvailability(UserAvailability availability) {
    this.availability = availability;
  }

  public ProfileKnowledgeLevel getKnowledgeLevel() {
    return knowledgeLevel;
  }

  public void setKnowledgeLevel(ProfileKnowledgeLevel knowledgeLevel) {
    this.knowledgeLevel = knowledgeLevel;
  }

  public List<Long> getCourseIds() {
    return courseIds;
  }
  
  public void setCourseIds(List<Long> courseIds) {
    this.courseIds = courseIds;
  }
  
}
