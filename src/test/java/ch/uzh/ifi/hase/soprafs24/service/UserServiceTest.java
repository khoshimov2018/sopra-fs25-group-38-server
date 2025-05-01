package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CourseSelectionDTO;

import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  private User testUser;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testUser = new User();
    testUser.setId(1L);
    testUser.setName("testName");
    testUser.setEmail("testUsername");
    testUser.setPassword("testPassword");
    testUser.setStudyLevel("Bachelor");
    testUser.setStudyGoals("Learning");

    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
  }

  @Test
  public void createUser_validInputs_success() {
    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    User createdUser = userService.createUser(testUser, Collections.emptyList());

    // then
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getName(), createdUser.getName());
    assertEquals(testUser.getEmail(), createdUser.getEmail());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.ONLINE, createdUser.getStatus());
  }

  @Test
  public void createUser_duplicateUsername_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser, Collections.emptyList());

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.existsByEmail(Mockito.any())).thenReturn(true);

    // Create a new user with the same username but different name
    User secondUser = new User();
    secondUser.setId(2L);
    secondUser.setName("Another Name");
    secondUser.setEmail("testUsername"); // Same username as testUser
    secondUser.setPassword("anotherPassword");
    secondUser.setStudyLevel("Master");
    secondUser.setStudyGoals("Data Science");

    // then -> attempt to create second user with same username -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(secondUser, Collections.emptyList()));
  }

  @Test
  public void createUser_duplicateInputs_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser, Collections.emptyList());

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByName(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.existsByEmail(Mockito.any())).thenReturn(true);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser, Collections.emptyList()));
  }

}
