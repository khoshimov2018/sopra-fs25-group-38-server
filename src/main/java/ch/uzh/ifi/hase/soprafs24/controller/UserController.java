package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserLoginDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

  private final UserService userService;

  UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsers() {
    // fetch all users in the internal representation
    List<User> users = userService.getUsers();
    List<UserGetDTO> userGetDTOs = new ArrayList<>();

    // convert each user to the API representation
    for (User user : users) {
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    return userGetDTOs;
  }

  @PostMapping("/users")
  @ResponseBody
  public ResponseEntity<UserGetDTO> createUser(@RequestBody UserPostDTO userPostDTO) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // create user
    User createdUser = userService.createUser(userInput);
    
    // Create HTTP headers and add token for automatic login
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + createdUser.getToken());
    
    // Convert internal representation of user back to API
    UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
    
    // Return response with headers and created status
    return ResponseEntity.status(HttpStatus.CREATED)
                        .headers(headers)
                        .body(userGetDTO);
  }
  
  @GetMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getUserById(@PathVariable Long userId, @RequestHeader(value = "Authorization", required = false) String token) {
    // Authenticate the request
    userService.authenticateByToken(token);
    
    // Fetch user by ID
    User user = userService.getUserById(userId);
    
    // Convert user to API representation
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
  }
  
  @PostMapping("/login")
  @ResponseBody
  public ResponseEntity<UserGetDTO> loginUser(@RequestBody UserLoginDTO userLoginDTO) {
    // Extract credentials from request
    String email = userLoginDTO.getEmail();
    String password = userLoginDTO.getPassword();
    
    // Perform login
    User loggedInUser = userService.loginUser(email, password);
    
    // Create HTTP headers and add token
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + loggedInUser.getToken());
    
    // Convert user to API representation
    UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(loggedInUser);
    
    // Return response with headers and OK status
    return ResponseEntity.status(HttpStatus.OK)
                         .headers(headers)
                         .body(userGetDTO);
  }
  
  @PostMapping("/users/{userId}/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void logoutUser(@PathVariable Long userId) {
    userService.logoutUser(userId);
  }
  
  /**
   * PUT /users/{userId} : Update an existing user
   * 
   * @param userId the ID of the user to update
   * @param userUpdateDTO the user data to update
   * @param token the authentication token from the request header
   * @return No content on success, 404 if user not found
   */
  @PutMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateUser(
      @PathVariable Long userId,
      @RequestBody UserPostDTO userUpdateDTO,
      @RequestHeader(value = "Authorization", required = false) String token) {
    
    // Authenticate the request
    userService.authenticateByToken(token);
    
    // Check if the user is authorized to update this profile (must be their own)
    userService.checkAuthorizationById(token, userId);
    
    // Convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userUpdateDTO);
    userInput.setId(userId);
    
    // Update the user
    userService.updateUser(userId, userInput);
  }
  @PostMapping("/users/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void logoutUser(@RequestHeader(value = "Authorization", required = false) String token) {
    userService.logoutUserByToken(token);
  }

}
