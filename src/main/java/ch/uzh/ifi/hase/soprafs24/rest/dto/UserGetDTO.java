package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public class UserGetDTO {

  private Long id;
  private String name;
  private String email;
  private UserStatus status;
  private String token;
  private LocalDateTime creationDate;
  private List<String> availability;
 

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
  
  public List<String> getAvailability() {
    return availability;
  }

  public void setAvailability(List<String> availability) {
    this.availability = availability;
  }
}
