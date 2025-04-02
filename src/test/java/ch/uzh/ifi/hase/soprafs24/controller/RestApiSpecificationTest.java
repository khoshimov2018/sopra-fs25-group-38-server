package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Course;
import ch.uzh.ifi.hase.soprafs24.constant.UserAvailability;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CourseGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserLoginDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.service.CourseService;
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
import static org.hamcrest.Matchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.springframework.test.web.servlet.MvcResult;

/**
 * RestApiSpecificationTest
 * 
 * This class specifically tests that the REST API specification is
 * correctly implemented for all required endpoints.
 */
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) 

public class RestApiSpecificationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private CourseService courseService;

    /**
     * Test 9: GET /courses
     * Verifies that a user can successfully retrieve the list of enrolled courses (200 OK).
     */
    @Test
    public void getUserCourses_validData_returnsCourses() throws Exception {
        //given
        User user = new User();
        user.setId(1L); 
        Course course = new Course(user, "Test courseName"); 
      
        // Mock the CourseService behavior
        given(courseService.getAllCourses()).willReturn(List.of(course));

        // when/then
        MvcResult result = mockMvc.perform(get("/courses")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer valid-token")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))))
        .andExpect(jsonPath("$[0].courseName", is(course.getCourseName())))
        .andExpect(jsonPath("$[0].userId", is(course.getUserId().intValue())))
        .andReturn(); 

        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response Body: " + result.getResponse().getContentAsString());
    }
    /**
     * Test 10: GET /students?availability={filter} 
     * Verifies that a user can retrieve students with matching availability (200 OK).
     */
    @Test
    public void getStudents_validAvailability_returnsStudents() throws Exception {
        // given - user1 (match)
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@email.com");
        user1.setName("User One");
        user1.setPassword("password123");
        user1.setAvailability(UserAvailability.EVENING);

        // given - user2 (no match)
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@email.com");
        user2.setName("User Two");
        user2.setPassword("password123");
        user2.setAvailability(UserAvailability.MORNING);

        List<User> userList = List.of(user1, user2);
        List<Long> matchedIds = List.of(user1.getId());

        // Mock the userService, CourseService behavior
        given(userService.getUsers()).willReturn(userList);
        given(courseService.findUserIdsEnrolledInAllAvailability(List.of("EVENING"))).willReturn(matchedIds);

        // when/then
        MvcResult result = mockMvc.perform(get("/students")
        .param("availability", "EVENING")
        .header("Authorization", "Bearer valid-token")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].email", is(user1.getEmail())))
        .andExpect(jsonPath("$[0].id", is(user1.getId().intValue())))
        .andReturn();

    System.out.println("Status: " + result.getResponse().getStatus());
    System.out.println("Response Body: " + result.getResponse().getContentAsString());
    }

    // /**
    //  * Test 1: POST /users - User Creation with Required Headers
    //  * Verifies that the response has the correct Content-Type and Location headers
    //  */
    // @Test
    // public void createUser_checkResponseHeaders() throws Exception {
    //     // given
    //     User user = new User();
    //     user.setId(1L);
    //     user.setName("Test User");
    //     user.setUsername("testuser");
    //     user.setPassword("password");
    //     user.setToken("token123");
    //     user.setStatus(UserStatus.ONLINE);
    //     user.setCreationDate(LocalDateTime.now());

    //     UserPostDTO userPostDTO = new UserPostDTO();
    //     userPostDTO.setName("Test User");
    //     userPostDTO.setUsername("testuser");
    //     userPostDTO.setPassword("password");

    //     given(userService.createUser(Mockito.any())).willReturn(user);

    //     // when/then
    //     MockHttpServletRequestBuilder postRequest = post("/users")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(asJsonString(userPostDTO))
    //             .accept(MediaType.APPLICATION_JSON);

    //     // then
    //     mockMvc.perform(postRequest)
    //             .andExpect(status().isCreated())
    //             .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    //             .andExpect(header().exists(HttpHeaders.AUTHORIZATION))
    //             .andExpect(header().string(HttpHeaders.AUTHORIZATION, "Bearer " + user.getToken()))
    //             .andExpect(jsonPath("$.username", is(user.getUsername())));
    // }

    // /**
    //  * Test 2: POST /users - User Creation with Invalid Data
    //  * Verifies that sending invalid data (missing required fields) returns a 400 BAD_REQUEST
    //  */
    // @Test
    // public void createUser_invalidData_returnsBadRequest() throws Exception {
    //     // given
    //     UserPostDTO invalidDTO = new UserPostDTO();
    //     // Missing required username and password

    //     given(userService.createUser(Mockito.any())).willThrow(
    //             new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username and password are required"));

    //     // when/then
    //     MockHttpServletRequestBuilder postRequest = post("/users")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(asJsonString(invalidDTO))
    //             .accept(MediaType.APPLICATION_JSON);

    //     // then
    //     mockMvc.perform(postRequest)
    //             .andExpect(status().isBadRequest());
    // }

    // /**
    //  * Test 3: GET /users/{userId} - Check that Accept header is respected
    //  * Verifies that the endpoint returns data in the format specified by the Accept header
    //  */
    // @Test
    // public void getUserById_withSpecificAcceptHeader_returnsCorrectContentType() throws Exception {
    //     // given
    //     User user = new User();
    //     user.setId(1L);
    //     user.setName("Test User");
    //     user.setUsername("testuser");
    //     user.setStatus(UserStatus.ONLINE);
    //     user.setToken("valid-token");
    //     user.setCreationDate(LocalDateTime.now());
        
    //     doNothing().when(userService).authenticateByToken(anyString());
    //     given(userService.getUserById(1L)).willReturn(user);

    //     // when/then
    //     MockHttpServletRequestBuilder getRequest = get("/users/1")
    //             .header("Authorization", "Bearer valid-token")
    //             .accept(MediaType.APPLICATION_JSON);

    //     // then
    //     mockMvc.perform(getRequest)
    //             .andExpect(status().isOk())
    //             .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    // }

    // /**
    //  * Test 4: GET /users/{userId} - Valid ID with incorrect path format
    //  * Verifies that using an incorrect format for ID in the path returns a 400 BAD_REQUEST
    //  */
    // @Test
    // public void getUserById_invalidIdFormat_returnsBadRequest() throws Exception {
    //     // when/then
    //     MockHttpServletRequestBuilder getRequest = get("/users/invalid-id")
    //             .header("Authorization", "Bearer valid-token")
    //             .accept(MediaType.APPLICATION_JSON);

    //     // then
    //     mockMvc.perform(getRequest)
    //             .andExpect(status().isBadRequest());
    // }

    // /**
    //  * Test 5: PUT /users/{userId} - Verify all updated fields
    //  * Tests that when updating a user, all provided fields are correctly updated
    //  */
    // @Test
    // public void updateUser_checkAllFieldsUpdated() throws Exception {
    //     // given
    //     Long userId = 1L;
        
    //     UserPostDTO updateDTO = new UserPostDTO();
    //     updateDTO.setName("Updated User");
    //     updateDTO.setUsername("updateduser");
        
    //     User updatedUser = new User();
    //     updatedUser.setId(userId);
    //     updatedUser.setName("Updated User");
    //     updatedUser.setUsername("updateduser");
    //     updatedUser.setStatus(UserStatus.ONLINE);
    //     updatedUser.setToken("valid-token");
        
    //     // Mock passing authentication
    //     doNothing().when(userService).authenticateByToken(anyString());
    //     doNothing().when(userService).checkAuthorizationById(anyString(), eq(userId));
        
    //     // Mock the update method and capture the argument
    //     doAnswer(invocation -> {
    //         User userArg = invocation.getArgument(1);
    //         // Verify that all fields from the DTO were set on the user entity
    //         assert userArg.getName().equals(updateDTO.getName());
    //         assert userArg.getUsername().equals(updateDTO.getUsername());
    //         return null;
    //     }).when(userService).updateUser(eq(userId), any(User.class));

    //     // when/then
    //     MockHttpServletRequestBuilder putRequest = put("/users/" + userId)
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(asJsonString(updateDTO))
    //             .header("Authorization", "Bearer valid-token");

    //     // then
    //     mockMvc.perform(putRequest)
    //             .andExpect(status().isNoContent());
        
    //     // Verify service method was called with correct parameters
    //     verify(userService).updateUser(eq(userId), any(User.class));
    // }

    // /**
    //  * Test 6: POST /login - Test login endpoint Content-Type negotiation
    //  * Ensures that the login endpoint correctly handles content type negotiation
    //  */
    // @Test
    // public void loginUser_contentTypeNegotiation() throws Exception {
    //     // given
    //     User user = new User();
    //     user.setId(1L);
    //     user.setName("Test User");
    //     user.setUsername("testuser");
    //     user.setPassword("password");
    //     user.setToken("login-token");
    //     user.setStatus(UserStatus.ONLINE);
    //     user.setCreationDate(LocalDateTime.now());

    //     UserLoginDTO loginDTO = new UserLoginDTO();
    //     loginDTO.setUsername("testuser");
    //     loginDTO.setPassword("password");

    //     given(userService.loginUser(anyString(), anyString())).willReturn(user);

    //     // when/then
    //     MockHttpServletRequestBuilder postRequest = post("/login")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(asJsonString(loginDTO))
    //             .accept(MediaType.APPLICATION_JSON);

    //     // then
    //     mockMvc.perform(postRequest)
    //             .andExpect(status().isOk())
    //             .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    //             .andExpect(header().exists(HttpHeaders.AUTHORIZATION))
    //             .andExpect(header().string(HttpHeaders.AUTHORIZATION, "Bearer " + user.getToken()));
    // }

    // /**
    //  * Test 7: PUT /users - Test invalid userId format
    //  * Verifies that the endpoint returns 400 BAD_REQUEST when userId is invalid
    //  */
    // @Test
    // public void updateUser_invalidIdFormat_returnsBadRequest() throws Exception {
    //     // Invalid ID (not a number)
    //     String invalidUserId = "invalid-id";
        
    //     // Empty update body
    //     String emptyJson = "{}";

    //     // Send request with invalid ID
    //     MockHttpServletRequestBuilder putRequest = put("/users/" + invalidUserId)
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(emptyJson)
    //             .header("Authorization", "Bearer valid-token");

    //     // Should return 400 Bad Request
    //     mockMvc.perform(putRequest)
    //             .andExpect(status().isBadRequest());
    // }

    // /**
    //  * Test 8: GET /users - Test options request for CORS support
    //  * Verifies that the API supports CORS preflight requests
    //  */
    // @Test
    // public void checkCorsSupport() throws Exception {
    //     // when/then
    //     mockMvc.perform(options("/users")
    //             .header("Access-Control-Request-Method", "GET")
    //             .header("Origin", "http://localhost:3000"))
    //             .andExpect(status().isOk())
    //             .andExpect(header().exists("Access-Control-Allow-Origin"))
    //             .andExpect(header().exists("Access-Control-Allow-Methods"));
    // }

    /**
     * Helper Method to convert objects into a JSON string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    String.format("The request body could not be created.%s", e));
        }
    }
}