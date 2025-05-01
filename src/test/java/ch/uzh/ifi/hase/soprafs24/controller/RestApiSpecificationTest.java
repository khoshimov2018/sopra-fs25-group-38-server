package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.ProfileKnowledgeLevel;
import ch.uzh.ifi.hase.soprafs24.constant.UserAvailability;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Course;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.CourseRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CourseSelectionDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserLoginDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.service.CourseService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class RestApiSpecificationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private CourseService courseService;

    @MockBean
    private CourseRepository courseRepository;

    @Test
    public void createUser_checkResponseHeaders() throws Exception {
        // mock return user
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("testuser@email.com");
        user.setPassword("password");
        user.setToken("token123");
        user.setStatus(UserStatus.ONLINE);
        user.setCreationDate(LocalDateTime.now());

        // input DTO
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setName("Test User");
        userPostDTO.setEmail("testuser@email.com");
        userPostDTO.setPassword("password");
        userPostDTO.setCourseSelections(Collections.emptyList()); // IMPORTANT

        // make sure the mock actually triggers
        when(userService.createUser(any(User.class), anyList())).thenReturn(user);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userPostDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Authorization", "Bearer token123"))
                .andExpect(jsonPath("$.email", is("testuser@email.com")))
                .andExpect(jsonPath("$.name", is("Test User")));
    }

    @Test
    public void createUser_invalidData_returnsBadRequest() throws Exception {
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setName("Invalid User");
        userPostDTO.setEmail(""); // Invalid
        userPostDTO.setPassword(""); // Invalid

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userPostDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getUserById_withSpecificAcceptHeader() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("testuser@email.com");
        user.setStatus(UserStatus.ONLINE);
        user.setToken("token123");

        given(userService.getUserById(1L)).willReturn(user);

        mockMvc.perform(get("/users/1")
                        .header("Authorization", "token123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("testuser@email.com"));
    }

        @Test
    public void loginUser_successful_returnsTokenAndUser() throws Exception {
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setEmail("user@example.com");
        loginDTO.setPassword("password");

        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setToken("token123");

        given(userService.loginUser("user@example.com", "password")).willReturn(user);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "Bearer token123"))
                .andExpect(jsonPath("$.email", is("user@example.com")));
    }

    @Test
    public void logoutUser_validToken_returnsNoContent() throws Exception {
        mockMvc.perform(post("/users/logout")
                        .header("Authorization", "Bearer token123"))
                .andExpect(status().isNoContent());
    }

    /* @Test
    public void updateUser_validData_returnsNoContent() throws Exception {
        UserPutDTO putDTO = new UserPutDTO();
        putDTO.setName("Updated Name");

        User existingUser = new User();
        existingUser.setId(1L);

        given(userService.getUserById(1L)).willReturn(existingUser);

        mockMvc.perform(put("/users/1")
                        .header("Authorization", "Bearer token123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(putDTO)))
                .andExpect(status().isNoContent());
    } */

    @Test
    public void getCurrentUser_validToken_returnsUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setToken("token123");

        given(userService.getUserByToken("token123")).willReturn(user);

        mockMvc.perform(get("/users/me")
                        .header("Authorization", "Bearer token123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("user@example.com")));
    }

    @Test
    public void getCurrentUser_invalidToken_returnsUnauthorized() throws Exception {
        given(userService.getUserByToken("invalidtoken")).willReturn(null);

        mockMvc.perform(get("/users/me")
                        .header("Authorization", "Bearer invalidtoken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteMyAccount_validToken_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/users/me")
                        .header("Authorization", "Bearer token123"))
                .andExpect(status().isNoContent());
    }



  
    @Test
    public void updateUser_validData_updatesSuccessfully() throws Exception {
        Long userId = 1L;
    
        // Existing user mock
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setToken("validToken");
        existingUser.setUserCourses(new ArrayList<>()); // Important for update logic
    
        // Mock course entity
        Course mockCourse = mock(Course.class);
        when(mockCourse.getId()).thenReturn(10L);
        when(mockCourse.getCourseName()).thenReturn("AI");
    
        when(courseRepository.findById(10L)).thenReturn(Optional.of(mockCourse));
        when(userService.getUserById(userId)).thenReturn(existingUser);
    
        doNothing().when(userService).authenticateByToken("validToken");
        doNothing().when(userService).checkAuthorizationById("validToken", userId);
    
        when(userService.updateUser(eq(userId), any(User.class))).thenReturn(existingUser);
    
        // Build DTO for update
        CourseSelectionDTO courseSelection = new CourseSelectionDTO();
        courseSelection.setCourseId(10L);
        courseSelection.setKnowledgeLevel(ProfileKnowledgeLevel.INTERMEDIATE);
    
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setName("Updated Name");
        userPutDTO.setBio("Updated Bio");
        userPutDTO.setProfilePicture("img.png");
        userPutDTO.setAvailability(UserAvailability.EVENING);
        userPutDTO.setStudyLevel("BSc");
        userPutDTO.setStudyGoals(Arrays.asList("Thesis"));
        userPutDTO.setCourseSelections(Arrays.asList(courseSelection)); 
    
        mockMvc.perform(put("/users/1")
                .header("Authorization", "validToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO)))
                .andExpect(status().isNoContent());
    }
    
    @Test
    public void updateUser_validData_updatesSuccessfully2() throws Exception {
        Long userId = 1L;
        String token = "validToken";
    
        // Mock courses
        Course course1 = mock(Course.class);
        Course course2 = mock(Course.class);
        when(course1.getId()).thenReturn(100L);
        when(course1.getCourseName()).thenReturn("AI");
        when(course2.getId()).thenReturn(200L);
        when(course2.getCourseName()).thenReturn("Data Science");
    
        when(courseRepository.findById(100L)).thenReturn(Optional.of(course1));
        when(courseRepository.findById(200L)).thenReturn(Optional.of(course2));
    
        // Mock existing user from DB
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setToken(token);
        existingUser.setUserCourses(new ArrayList<>());
    
        when(userService.getUserById(userId)).thenReturn(existingUser);
    
        doNothing().when(userService).authenticateByToken(token);
        doNothing().when(userService).checkAuthorizationById(token, userId);
        when(userService.updateUser(eq(userId), any(User.class))).thenReturn(existingUser);
    
        // Prepare input DTO
        CourseSelectionDTO courseSel1 = new CourseSelectionDTO();
        courseSel1.setCourseId(100L);
        courseSel1.setKnowledgeLevel(ProfileKnowledgeLevel.INTERMEDIATE);
    
        CourseSelectionDTO courseSel2 = new CourseSelectionDTO();
        courseSel2.setCourseId(200L);
        courseSel2.setKnowledgeLevel(ProfileKnowledgeLevel.INTERMEDIATE);
    
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setName("Updated Name");
        userPutDTO.setBio("Updated Bio");
        userPutDTO.setProfilePicture("updated-pic.png");
        userPutDTO.setAvailability(UserAvailability.EVENING);
        userPutDTO.setStudyLevel("MSc");
        userPutDTO.setStudyGoals(Arrays.asList("Thesis", "Internship"));
        userPutDTO.setCourseSelections(Arrays.asList(courseSel1, courseSel2)); 
    
        mockMvc.perform(put("/users/{userId}", userId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO)))
            .andExpect(status().isNoContent());
    
        verify(userService).authenticateByToken(token);
        verify(userService).checkAuthorizationById(token, userId);
        verify(userService).updateUser(eq(userId), any(User.class));
    }
    


    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e));
        }
    }

    
}
