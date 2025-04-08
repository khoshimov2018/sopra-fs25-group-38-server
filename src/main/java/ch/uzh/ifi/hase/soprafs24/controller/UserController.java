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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class UserController {

  private final UserService userService;
  private final CourseRepository courseRepository;

  public UserController(UserService userService, CourseRepository courseRepository) {
    this.userService = userService;
    this.courseRepository = courseRepository;
  }

  @GetMapping("/courses")
  @ResponseStatus(HttpStatus.OK)
  public List<CourseGetDTO> getAllCourses() {
    List<Course> courses = courseRepository.findAll();
    List<CourseGetDTO> courseGetDTOs = new ArrayList<>();
    for (Course course : courses) {
      courseGetDTOs.add(DTOMapper.INSTANCE.convertEntityToCourseGetDTO(course));
    }
    return courseGetDTOs;
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
      @RequestBody UserPostDTO userUpdateDTO,
      @RequestHeader(value = "Authorization", required = false) String token) {

    userService.authenticateByToken(token);
    userService.checkAuthorizationById(token, userId);

    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userUpdateDTO);
    userInput.setId(userId);

    userService.updateUser(userId, userInput);
  }

  @GetMapping("users/{userId}/accepted-matches")
  public ResponseEntity<Set<Long>> getAcceptedMatches(@PathVariable Long userId) {
    Set<Long> partnerIds = userService.getAcceptedMatchPartnerIds(userId);
    return ResponseEntity.ok(partnerIds);
  }

  @PostMapping("/users/register")
  public ResponseEntity<UserGetDTO> registerUser(@RequestBody UserPostDTO userPostDTO) {
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
    User createdUser = userService.createUser(userInput);

    if (userPostDTO.getCourseSelections() != null && !userPostDTO.getCourseSelections().isEmpty()) {
      userService.assignCoursesWithKnowledgeLevels(createdUser, userPostDTO.getCourseSelections());
    }

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + createdUser.getToken());

    UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);

    return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(userGetDTO);
  }
}
