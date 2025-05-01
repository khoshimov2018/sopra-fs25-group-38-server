package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CourseSelectionDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void createUser_validInput_userCreatedSuccessfully() {
        User user = new User();
        user.setEmail("testuser@example.com");
        user.setPassword("securePass123");
        user.setName("Test User");
        user.setStudyLevel("Bachelor");
        user.setStudyGoals("AI and ML");

        User createdUser = userService.createUser(user, List.of());

        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getToken()).isNotNull();
        assertThat(createdUser.getStatus()).isEqualTo(UserStatus.ONLINE);
    }

    @Test
    void createUser_duplicateEmail_throwsException() {
        // Create initial user
        User user1 = new User();
        user1.setEmail("duplicate@example.com");
        user1.setPassword("securePass123");
        user1.setName("Original User");
        user1.setStudyLevel("Bachelor");
        user1.setStudyGoals("Goals A");
        userService.createUser(user1, List.of());

        // Attempt to register a second user with the same email
        User user2 = new User();
        user2.setEmail("duplicate@example.com");
        user2.setPassword("securePass123");
        user2.setName("Other User");
        user2.setStudyLevel("Master");
        user2.setStudyGoals("Goals B");

        // Create a local variable with the correct type
        List<CourseSelectionDTO> emptyCourseList = Collections.emptyList();
        assertThatThrownBy(() -> userService.createUser(user2, emptyCourseList))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Email already exists");
    }

    @Test
    void loginUser_validCredentials_successfulLogin() {
        User user = new User();
        user.setEmail("login@example.com");
        user.setPassword("securePass123");
        user.setName("Login User");
        user.setStudyLevel("PhD");
        user.setStudyGoals("Deep Learning");
        userService.createUser(user, List.of());

        User loggedIn = userService.loginUser("login@example.com", "securePass123");

        assertThat(loggedIn).isNotNull();
        assertThat(loggedIn.getStatus()).isEqualTo(UserStatus.ONLINE);
    }

    @Test
    void loginUser_invalidPassword_throwsException() {
        User user = new User();
        user.setEmail("wrongpass@example.com");
        user.setPassword("correctPass123");
        user.setName("Wrong Pass");
        user.setStudyLevel("MSc");
        user.setStudyGoals("Bioinformatics");
        userService.createUser(user, List.of());

        assertThatThrownBy(() -> userService.loginUser("wrongpass@example.com", "incorrectPassword"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    void getUserById_validId_returnsUser() {
        User user = new User();
        user.setEmail("fetch@example.com");
        user.setPassword("fetchPass123");
        user.setName("Fetch Me");
        user.setStudyLevel("Bachelor");
        user.setStudyGoals("Genetics");
        User created = userService.createUser(user, List.of());

        User fetched = userService.getUserById(created.getId());

        assertThat(fetched.getEmail()).isEqualTo("fetch@example.com");
        assertThat(fetched.getName()).isEqualTo("Fetch Me");
    }
}
