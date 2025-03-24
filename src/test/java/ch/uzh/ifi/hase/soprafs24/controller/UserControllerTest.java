package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserLoginDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  /**
   * S1 - GET /users - Test retrieving all users
   */
  @Test
  public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setName("Firstname Lastname");
    user.setUsername("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE);
    user.setPassword("password123");
    user.setCreationDate(LocalDateTime.now());

    List<User> allUsers = Collections.singletonList(user);

    // Mock the UserService behavior
    given(userService.getUsers()).willReturn(allUsers);

    // when/then
    MockHttpServletRequestBuilder getRequest = get("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest)
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id", is(user.getId().intValue())))
        .andExpect(jsonPath("$[0].name", is(user.getName())))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())))
        .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
  }

  /**
   * S1 - POST /users - Test creating a user successfully
   * Should return 201 CREATED status and the created user object
   */
  @Test
  public void createUser_validInput_userCreated() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setName("Test User");
    user.setUsername("testUsername");
    user.setPassword("password123");
    user.setToken("token123");
    user.setStatus(UserStatus.ONLINE);
    user.setCreationDate(LocalDateTime.now());

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setName("Test User");
    userPostDTO.setUsername("testUsername");
    userPostDTO.setPassword("password123");

    given(userService.createUser(Mockito.any())).willReturn(user);

    // when/then
    MockHttpServletRequestBuilder postRequest = post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO))
        .accept(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(header().exists(HttpHeaders.AUTHORIZATION))
        .andExpect(jsonPath("$.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.name", is(user.getName())))
        .andExpect(jsonPath("$.username", is(user.getUsername())))
        .andExpect(jsonPath("$.token", is(user.getToken())))
        .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }
  
  /**
   * S1 - POST /users - Test creating a user with a username that already exists
   * Should return 409 CONFLICT status
   */
  @Test
  public void createUser_duplicateUsername_throwsConflict() throws Exception {
    // given
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setName("Test User");
    userPostDTO.setUsername("testUsername");
    userPostDTO.setPassword("password123");

    given(userService.createUser(Mockito.any())).willThrow(
        new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists"));

    // when/then
    MockHttpServletRequestBuilder postRequest = post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO))
        .accept(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isConflict());
  }

  /**
   * S2 - GET /users/{userId} - Test retrieving a specific user by ID successfully
   * Should return 200 OK status and the user object
   */
  @Test
  public void givenUser_whenGetUserById_thenReturnUserJson() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setName("Test User");
    user.setUsername("testuser");
    user.setStatus(UserStatus.ONLINE);
    user.setToken("valid-token");
    user.setPassword("password123");
    user.setCreationDate(LocalDateTime.now());
    user.setBirthday(LocalDate.of(1990, 1, 1));
    
    // when the authenticateByToken is called, simply return (no error)
    doNothing().when(userService).authenticateByToken(anyString());
    
    // when getUserById is called with ID 1, return our test user
    given(userService.getUserById(1L)).willReturn(user);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users/1")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer valid-token");

    // then
    mockMvc.perform(getRequest)
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.name", is(user.getName())))
        .andExpect(jsonPath("$.username", is(user.getUsername())))
        .andExpect(jsonPath("$.status", is(user.getStatus().toString())))
        .andExpect(jsonPath("$.birthday").value(user.getBirthday().toString()));
  }
  
  /**
   * S2 - GET /users/{userId} - Test retrieving a non-existent user
   * Should return 404 NOT_FOUND status
   */
  @Test
  public void getUserById_userNotFound_throwsNotFound() throws Exception {
    // when getUserById is called with ID 99, throw not found exception
    given(userService.getUserById(99L)).willThrow(
        new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID 99 was not found"));
    
    // authenticateByToken should pass
    doNothing().when(userService).authenticateByToken(anyString());

    // when/then
    MockHttpServletRequestBuilder getRequest = get("/users/99")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer valid-token");

    // then
    mockMvc.perform(getRequest)
        .andExpect(status().isNotFound());
  }
  
  /**
   * S2 - GET /users/{userId} - Test retrieving a user without authentication
   * Should return 401 UNAUTHORIZED status
   */
  @Test
  public void getUserById_unauthorized_throwsUnauthorized() throws Exception {
    // authenticateByToken should throw unauthorized
    doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication token"))
        .when(userService).authenticateByToken(anyString());

    // when/then
    MockHttpServletRequestBuilder getRequest = get("/users/1")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer invalid-token");

    // then
    mockMvc.perform(getRequest)
        .andExpect(status().isUnauthorized());
  }

  /**
   * S3 - PUT /users/{userId} - Test updating a user successfully
   * Should return 204 NO_CONTENT status
   */
  @Test
  public void updateUser_validInput_userUpdated() throws Exception {
    // given
    Long userId = 1L;
    UserPostDTO userUpdateDTO = new UserPostDTO();
    userUpdateDTO.setName("Updated Name");
    userUpdateDTO.setUsername("updatedUsername");
    
    // Mock passing authentication
    doNothing().when(userService).authenticateByToken(anyString());
    doNothing().when(userService).checkAuthorizationById(anyString(), eq(userId));
    
    // Mock successful update
    User updatedUser = new User();
    updatedUser.setId(userId);
    updatedUser.setName("Updated Name");
    updatedUser.setUsername("updatedUsername");
    when(userService.updateUser(eq(userId), any(User.class))).thenReturn(updatedUser);

    // when/then
    MockHttpServletRequestBuilder putRequest = put("/users/" + userId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userUpdateDTO))
        .header("Authorization", "Bearer valid-token");

    // then
    mockMvc.perform(putRequest)
        .andExpect(status().isNoContent());
    
    // Verify service method was called with correct parameters
    verify(userService).updateUser(eq(userId), any(User.class));
  }
  
  /**
   * S3 - PUT /users/{userId} - Test updating a non-existent user
   * Should return 404 NOT_FOUND status
   */
  @Test
  public void updateUser_userNotFound_throwsNotFound() throws Exception {
    // given
    Long userId = 99L;
    UserPostDTO userUpdateDTO = new UserPostDTO();
    userUpdateDTO.setName("Updated Name");
    userUpdateDTO.setUsername("updatedUsername");
    
    // Mock passing authentication
    doNothing().when(userService).authenticateByToken(anyString());
    doNothing().when(userService).checkAuthorizationById(anyString(), eq(userId));
    
    // Mock not found when updating
    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID 99 was not found"))
        .when(userService).updateUser(eq(userId), any(User.class));

    // when/then
    MockHttpServletRequestBuilder putRequest = put("/users/" + userId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userUpdateDTO))
        .header("Authorization", "Bearer valid-token");

    // then
    mockMvc.perform(putRequest)
        .andExpect(status().isNotFound());
  }
  
  /**
   * S3 - PUT /users/{userId} - Test updating a user without authentication
   * Should return 401 UNAUTHORIZED status
   */
  @Test
  public void updateUser_unauthorized_throwsUnauthorized() throws Exception {
    // given
    Long userId = 1L;
    UserPostDTO userUpdateDTO = new UserPostDTO();
    userUpdateDTO.setName("Updated Name");
    userUpdateDTO.setUsername("updatedUsername");
    
    // Mock failed authentication
    doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication token"))
        .when(userService).authenticateByToken(anyString());

    // when/then
    MockHttpServletRequestBuilder putRequest = put("/users/" + userId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userUpdateDTO))
        .header("Authorization", "Bearer invalid-token");

    // then
    mockMvc.perform(putRequest)
        .andExpect(status().isUnauthorized());
  }
  
  /**
   * S3 - PUT /users/{userId} - Test updating a user without authorization (not own profile)
   * Should return 403 FORBIDDEN status
   */
  @Test
  public void updateUser_notAuthorized_throwsForbidden() throws Exception {
    // given
    Long userId = 2L;
    UserPostDTO userUpdateDTO = new UserPostDTO();
    userUpdateDTO.setName("Updated Name");
    userUpdateDTO.setUsername("updatedUsername");
    
    // Mock passing authentication but failing authorization
    doNothing().when(userService).authenticateByToken(anyString());
    doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only edit your own profile"))
        .when(userService).checkAuthorizationById(anyString(), eq(userId));

    // when/then
    MockHttpServletRequestBuilder putRequest = put("/users/" + userId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userUpdateDTO))
        .header("Authorization", "Bearer valid-token");

    // then
    mockMvc.perform(putRequest)
        .andExpect(status().isForbidden());
  }
  
  /**
   * S1 - POST /login - Test logging in successfully
   * Should return 200 OK status and the user object with token
   */
  @Test
  public void loginUser_validInput_userLoggedIn() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setName("Test User");
    user.setUsername("testUsername");
    user.setPassword("password123");
    user.setToken("token123");
    user.setStatus(UserStatus.ONLINE);
    user.setCreationDate(LocalDateTime.now());

    UserLoginDTO userLoginDTO = new UserLoginDTO();
    userLoginDTO.setUsername("testUsername");
    userLoginDTO.setPassword("password123");

    given(userService.loginUser(Mockito.anyString(), Mockito.anyString())).willReturn(user);

    // when/then
    MockHttpServletRequestBuilder postRequest = post("/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userLoginDTO))
        .accept(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(header().exists(HttpHeaders.AUTHORIZATION))
        .andExpect(jsonPath("$.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.name", is(user.getName())))
        .andExpect(jsonPath("$.username", is(user.getUsername())))
        .andExpect(jsonPath("$.token", is(user.getToken())))
        .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }
  
  /**
   * S1 - POST /login - Test logging in with invalid credentials
   * Should return 401 UNAUTHORIZED status
   */
  @Test
  public void loginUser_invalidCredentials_throwsUnauthorized() throws Exception {
    // given
    UserLoginDTO userLoginDTO = new UserLoginDTO();
    userLoginDTO.setUsername("testUsername");
    userLoginDTO.setPassword("wrongPassword");

    given(userService.loginUser(Mockito.anyString(), Mockito.anyString())).willThrow(
        new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password"));

    // when/then
    MockHttpServletRequestBuilder postRequest = post("/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userLoginDTO))
        .accept(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isUnauthorized());
  }
  
  /**
   * S1 - POST /users/{userId}/logout - Test logging out successfully
   * Should return 204 NO_CONTENT status
   */
  @Test
  public void logoutUser_validInput_userLoggedOut() throws Exception {
    // given
    Long userId = 1L;
    
    // when/then
    MockHttpServletRequestBuilder postRequest = post("/users/" + userId + "/logout")
        .contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isNoContent());
    
    // verify that userService.logoutUser was called once with the correct ID
    verify(userService, times(1)).logoutUser(userId);
  }

  /**
   * Helper Method to convert objects into a JSON string
   * 
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}