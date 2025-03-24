package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.constant.ProfileKnowledgeLevel;
import ch.uzh.ifi.hase.soprafs24.constant.UserAvailability;

import javax.persistence.*; // package provides annotations (such as @Entity, @Table, @Id, @Column, etc.) used to map this class to a database table.
import java.io.Serializable; // to allow the object to be converted into a byte stream (a requirement for many JPA providers).
import java.time.LocalDate; 
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
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private LocalDate creationDate;

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

  @Column(nullable = true)
  private String bio;


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

  // username
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  // password
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
      this.password = password;
  }

  // creationDate
  public LocalDate getCreationDate() {
      return creationDate;
  }

  public void setCreationDate(LocalDate creationDate) {
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

  // bio
  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }




  // A no-argument constructor is required by JPA for creating instances via reflection.
  public User() {
    this.creationDate = LocalDate.now();
  }
  // parameterized constructor
  public User(String username, String name, String password) {
    this.username = username;
    this.name = name;
    this.password = password;
    this.creationDate = LocalDate.now();
  }
}
