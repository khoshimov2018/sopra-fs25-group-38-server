package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  private User testUser;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);

    testUser = new User();
    testUser.setId(1L);
    testUser.setName("testName");
    testUser.setEmail("testUsername");
    testUser.setPassword("testPassword");
    testUser.setStudyLevel("Bachelor");
    testUser.setStudyGoals("Learning");

    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
  }

  @Test
  void createUser_validInputs_success() {
    User createdUser = userService.createUser(testUser, Collections.emptyList());

    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getName(), createdUser.getName());
    assertEquals(testUser.getEmail(), createdUser.getEmail());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.ONLINE, createdUser.getStatus());
  }

  @Test
  void createUser_duplicateUsername_throwsException() {
    userService.createUser(testUser, Collections.emptyList());

    Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.existsByEmail(Mockito.any())).thenReturn(true);

    User secondUser = new User();
    secondUser.setId(2L);
    secondUser.setName("Another Name");
    secondUser.setEmail("testUsername"); // duplicate email
    secondUser.setPassword("anotherPassword");
    secondUser.setStudyLevel("Master");
    secondUser.setStudyGoals("Data Science");

    Executable executable = () -> userService.createUser(secondUser, Collections.emptyList());
    assertThrows(ResponseStatusException.class, executable);
  }

  @Test
  void createUser_duplicateInputs_throwsException() {
    userService.createUser(testUser, Collections.emptyList());

    Mockito.when(userRepository.findByName(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.existsByEmail(Mockito.any())).thenReturn(true);

    Executable executable = () -> userService.createUser(testUser, Collections.emptyList());
    assertThrows(ResponseStatusException.class, executable);
  }
}

