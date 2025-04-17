package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.constant.ProfileKnowledgeLevel;
import ch.uzh.ifi.hase.soprafs24.constant.UserAvailability;

import javax.persistence.*; // package provides annotations (such as @Entity, @Table, @Id, @Column, etc.) used to map this class to a database table.
import java.io.Serializable; // to allow the object to be converted into a byte stream (a requirement for many JPA providers).
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.time.LocalDateTime;
import java.util.List;
import ch.uzh.ifi.hase.soprafs24.entity.Course;
/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */

// @Entity: Indicates that this class is a JPA entity. It will be mapped to a database table.
// Table(name = "USER"): Specifies the table name in the database.
// implements Serializable: Ensures that instances of User can be serialized, which is often required for entities in Java.
@Entity
@Table(name = "USER")
public class User implements Serializable {

  // This is a unique identifier for the Serializable class, ensuring that a loaded class corresponds exactly to a serialized object.
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private LocalDateTime creationDate;

  @Column(nullable = false, unique = true)
  private String token;

  @Column(nullable = true, unique = true)
  private String chatToken;

  @Column(nullable = false)
  private UserStatus status;

  @Column(nullable = true)
  private UserAvailability availability;

  @Column(nullable = true)
  private ProfileKnowledgeLevel knowledgeLevel;

  @Column(nullable = true)
  private String studyGoals;

  @Column(nullable = false)
  private String studyLevel;

  @Lob
  @Column(nullable = true, columnDefinition = "CLOB") 
  private String profilePicture;

  @Lob
  @Column(nullable = true)
  private String bio;
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private List<UserCourse> userCourses = new ArrayList<>();  


  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(name = "user_match_ids", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "match_id")
  private Set<Long> matchIds = new HashSet<>();

  

  /*
    Getter and Setter Methods
  */

  // id
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  // name
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  // email
  public String getEmail() {
    return email;
  }
  
  public void setEmail(String email) {
    this.email = email;
  }

  // password
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
      this.password = password;
  }

  // creationDate
  public LocalDateTime getCreationDate() {
      return creationDate;
  }

  public void setCreationDate(LocalDateTime creationDate) {
      this.creationDate = creationDate;
  }

  // token
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  // chat token
  public String getChatToken() {
    return chatToken;
  }

  public void setChatToken(String token) {
    this.chatToken = chatToken;
  }

  // status
  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  // availability
  public UserAvailability getAvailability() {
    return availability;
  }

  public void setAvailability(UserAvailability availability) {
    this.availability = availability;
  }

  // knowledge level
  public ProfileKnowledgeLevel getKnowledgeLevel() {
    return knowledgeLevel;
  }

  public void setKnowledgeLevel(ProfileKnowledgeLevel knowledgeLevel) {
    this.knowledgeLevel = knowledgeLevel;
  }

  // study goals
  public String getStudyGoals() {
    return studyGoals;
  }

  public void setStudyGoals(String studyGoals) {
    this.studyGoals = studyGoals;
  }

  // study level
  public String getStudyLevel() {
    return studyLevel;
  }

  public void setStudyLevel(String studyLevel) {
    this.studyLevel = studyLevel;
  }

  // profile picture
  public String getProfilePicture() {
    return profilePicture;
  }

  public void setProfilePicture(String profilePicture) {
    this.profilePicture = profilePicture;
  }

  // bio
  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public Set<Long> getMatchIds() {
    return matchIds;
  }

  public void setMatchIds(Set<Long> matchIds) {
    this.matchIds = matchIds;
  }

  public List<UserCourse> getUserCourses() {
    return userCourses;
  }


  // A no-argument constructor is required by JPA for creating instances via reflection.
  public User() {
    this.creationDate = LocalDateTime.now();
  }
  // parameterized constructor
  public User(String email, String name, String password) {
    this.email = email;
    this.name = name;
    this.password = password;
    this.creationDate = LocalDateTime.now();
  }

  public void setUserCourses(List<UserCourse> newCourses) {
    this.userCourses.clear();
    if (newCourses != null) {
        for (UserCourse uc : newCourses) {
            uc.setUser(this); // link each course
            this.userCourses.add(uc);
        }
    }
}


}
