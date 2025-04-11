package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Course;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserLoginDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CourseGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.repository.CourseRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = {"http://localhost:3000", "https://sopra-fs25-group-38-client.vercel.app"})
@RestController
public class UserController {

  private final UserService userService;
  private final CourseRepository courseRepository;

  public UserController(UserService userService, CourseRepository courseRepository) {
    this.userService = userService;
    this.courseRepository = courseRepository;
  }

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  public List<UserGetDTO> getAllUsers() {
    List<User> users = userService.getUsers();
    List<UserGetDTO> userGetDTOs = new ArrayList<>();
    for (User user : users) {
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    return userGetDTOs;
  }

  @GetMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  public UserGetDTO getUserById(
      @PathVariable Long userId,
      @RequestHeader(value = "Authorization", required = false) String token) {
    userService.authenticateByToken(token);
    User user = userService.getUserById(userId);
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
  }

  @PostMapping("/login")
  public ResponseEntity<UserGetDTO> loginUser(@RequestBody UserLoginDTO userLoginDTO) {
    String email = userLoginDTO.getEmail();
    String password = userLoginDTO.getPassword();

    User loggedInUser = userService.loginUser(email, password);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + loggedInUser.getToken());

    UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(loggedInUser);

    return ResponseEntity.status(HttpStatus.OK).headers(headers).body(userGetDTO);
  }

  @PostMapping("/users/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void logoutUser(@RequestHeader(value = "Authorization", required = false) String token) {
    userService.logoutUserByToken(token);
  }

  @PutMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateUser(
      @PathVariable Long userId,
      @RequestBody UserPutDTO userUpdateDTO,
      @RequestHeader(value = "Authorization", required = false) String token) {

    userService.authenticateByToken(token);
    userService.checkAuthorizationById(token, userId);

    // Get the user from the DB
    User existingUser = userService.getUserById(userId);
   
    // Apply updates from DTO to entity
    DTOMapper.INSTANCE.updateUserFromDTO(userUpdateDTO, existingUser, courseRepository);
  
    // Save updated user
    userService.updateUser(userId, existingUser);
  }

  @GetMapping("users/{userId}/accepted-matches")
  public ResponseEntity<Set<Long>> getAcceptedMatches(@PathVariable Long userId) {
    Set<Long> partnerIds = userService.getAcceptedMatchPartnerIds(userId);
    return ResponseEntity.ok(partnerIds);
  }

  @PostMapping("/users/register")
  public ResponseEntity<UserGetDTO> registerUser(@RequestBody UserPostDTO userPostDTO) {
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
    User createdUser = userService.createUser(userInput, userPostDTO.getCourseSelections());

    if (userPostDTO.getCourseSelections() != null && !userPostDTO.getCourseSelections().isEmpty()) {
      userService.assignCoursesWithKnowledgeLevels(createdUser, userPostDTO.getCourseSelections());
    }

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + createdUser.getToken());

    UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);

    return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(userGetDTO);
  }
  
  /**
   * Endpoint that uses the Authorization token to identify and return the current user
   * This is a common pattern in RESTful APIs for retrieving the authenticated user
   */
  @GetMapping("/users/me")
  public ResponseEntity<UserGetDTO> getCurrentUser(
      @RequestHeader(value = "Authorization", required = true) String token) {
    try {
      // Verify the token format
      if (token == null || token.isBlank()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
      }
      
      // Extract token without Bearer prefix if present
      String authToken = token;
      if (token.startsWith("Bearer ")) {
        authToken = token.substring(7);
      }
      
      // Get the user associated with this token
      User currentUser = userService.getUserByToken(authToken);
      
      if (currentUser == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
      }
      
      UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(currentUser);
      return ResponseEntity.ok(userGetDTO);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
  }

  /**
 * DELETE /users/me
 * Deletes the currently authenticated user's account and all associated data.
 *
 * Note: This does NOT delete all users. For admin-level deletion of other users, 
 * a separate endpoint with proper authorization should be implemented.
 *
 * @param token Bearer token from the Authorization header
 * @return 204 No Content on successful deletion 
*/@DeleteMapping("/users/me")
  public ResponseEntity<Void> deleteMyAccount(@RequestHeader("Authorization") String token) {
      userService.deleteUserByToken(token);
      return ResponseEntity.noContent().build();
  }
}