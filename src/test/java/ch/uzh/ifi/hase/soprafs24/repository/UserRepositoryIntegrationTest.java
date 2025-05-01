package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private UserRepository userRepository;

  @Test
  void findByName_success() {
    // given
    User user = new User();
    user.setName("Firstname Lastname");
    user.setEmail("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE);
    user.setToken("1");
    user.setPassword("password123");
    user.setCreationDate(LocalDateTime.now());
    user.setStudyLevel("Bachelor");
    user.setStudyGoals("Learning");

    entityManager.persist(user);
    entityManager.flush();

    // when
    User found = userRepository.findByName(user.getName());

    // then
    assertNotNull(found.getId());
    assertEquals(found.getName(), user.getName());
    assertEquals(found.getEmail(), user.getEmail());
    assertEquals(found.getToken(), user.getToken());
    assertEquals(found.getStatus(), user.getStatus());
  }
}