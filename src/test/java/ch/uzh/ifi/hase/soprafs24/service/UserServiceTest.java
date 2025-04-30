package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock private UserRepository userRepository;
    @Mock private MatchRepository matchRepository;
    @Mock private CourseRepository courseRepository;
    @Mock private UserCourseRepository userCourseRepository;
    @Mock private MessageRepository messageRepository;
    @Mock private BlockRepository blockRepository;
    @Mock private ReportRepository reportRepository;
    @Mock private ChatChannelRepository chatChannelRepository;
    @Mock private StudyPlanRepository studyPlanRepository;
    @Mock private ProfileRepository profileRepository;

    private User testUser;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("testpassword");
        testUser.setStatus(UserStatus.ONLINE);
        testUser.setToken("testtoken");
        testUser.setCreationDate(LocalDateTime.now());
        testUser.setName("Test User");
        testUser.setStudyLevel("Bachelor");
        testUser.setStudyGoals("Learn Java");
    }

    @Test
    void getUserById_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        User found = userService.getUserById(1L);
        assertEquals("test@example.com", found.getEmail());
    }

    @Test
    void getUserById_notFound_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> userService.getUserById(1L));
    }

    @Test
    void loginUser_success() {
        // Prepare encoded password
        String rawPassword = "password123";
        String encodedPassword = new BCryptPasswordEncoder().encode(rawPassword);
        testUser.setPassword(encodedPassword);

        // Mock the repository
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(userRepository.save(any())).thenReturn(testUser); // needed because loginUser saves the updated token

        // Call the service
        User loggedIn = userService.loginUser("test@example.com", rawPassword);

        // Verify results
        assertNotNull(loggedIn);
        assertEquals(UserStatus.ONLINE, loggedIn.getStatus());
        assertNotNull(loggedIn.getToken());
        verify(userRepository, times(1)).save(any());
    }


    @Test
    void loginUser_invalidPassword_throws() {
        testUser.setPassword(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("wrongpw"));
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        assertThrows(ResponseStatusException.class, () -> userService.loginUser("test@example.com", "notcorrect"));
    }

    @Test
    void logoutUser_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        userService.logoutUser(1L);
        assertEquals(UserStatus.OFFLINE, testUser.getStatus());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void authenticateByToken_valid() {
        when(userRepository.existsByToken("validToken")).thenReturn(true);
        assertDoesNotThrow(() -> userService.authenticateByToken("Bearer validToken"));
    }

    @Test
    void authenticateByToken_invalid_throws() {
        when(userRepository.existsByToken("invalidToken")).thenReturn(false);
        assertThrows(ResponseStatusException.class, () -> userService.authenticateByToken("Bearer invalidToken"));
    }

    @Test
    void getUserByToken_returnsUser() {
        when(userRepository.findByToken("valid")).thenReturn(testUser);
        User result = userService.getUserByToken("Bearer valid");
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void getUserByToken_null_returnsNull() {
        User result = userService.getUserByToken(null);
        assertNull(result);
    }
}
