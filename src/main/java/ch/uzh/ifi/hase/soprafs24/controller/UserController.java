package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserLoginDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.repository.CourseRepository;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = {"http://localhost:3000", "https://sopra-fs25-group-38-client.vercel.app","https://sopra-fs25-group-38-client-notsofuns-projects.vercel.app"})
@RestController
public class UserController {

  private static final String BEARER_PREFIX = "Bearer ";

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
    headers.add("Authorization", BEARER_PREFIX + loggedInUser.getToken());

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
  public void updateUser(@PathVariable Long userId,
                         @RequestBody UserPutDTO dto,
                         @RequestHeader("Authorization") String token) {
      userService.authenticateByToken(token);
      userService.checkAuthorizationById(token, userId);
  
      User user = userService.getUserById(userId);
      DTOMapper.INSTANCE.updateUserFromDTO(dto, user, courseRepository);
      userService.updateUser(userId, user);
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

    if (createdUser == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user registration data.");
    }

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", BEARER_PREFIX + createdUser.getToken());
    headers.add("Location", "/users/" + createdUser.getId());

    UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);

    return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(userGetDTO);
  }

  @GetMapping("/users/me")
  public ResponseEntity<UserGetDTO> getCurrentUser(
      @RequestHeader(value = "Authorization", required = true) String token) {
    try {
      if (token == null || token.isBlank()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
      }

      String authToken = token;
      if (token.startsWith(BEARER_PREFIX)) {
        authToken = token.substring(BEARER_PREFIX.length());
      }

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

  @DeleteMapping("/users/me")
  public ResponseEntity<Void> deleteMyAccount(@RequestHeader("Authorization") String token) {
    userService.deleteUserByToken(token);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/delete/{userId}")
  public ResponseEntity<Void> deleteUserByAdmin(
    @PathVariable Long userId, @RequestHeader("Authorization") String token) {

    if (!userService.isAdmin(token)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    userService.deleteUserById(userId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/users/discoverable/{userId}")
  public List<UserGetDTO> getDiscoverableUsers(@PathVariable Long userId) {
    List<User> discoverable = userService.getDiscoverableUsers(userId);
    return discoverable.stream()
        .map(DTOMapper.INSTANCE::convertEntityToUserGetDTO)
        .toList();
  }
}
