package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User createTestUser(String name, String email, String token) {
      User user = new User();
      user.setName(name);
      user.setEmail(email);
      user.setPassword("password123");
      user.setStatus(UserStatus.ONLINE);
      user.setToken(token);
      user.setCreationDate(LocalDateTime.now());
  
      user.setStudyLevel("Bachelor");
      user.setStudyGoals("Finish my degree");
  
      return user;
  }
  

    @Test
    public void testFindByName_success() {
        User user = createTestUser("Alice", "alice@example.com", "token1");
        entityManager.persistAndFlush(user);

        User found = userRepository.findByName("Alice");

        assertNotNull(found);
        assertEquals("Alice", found.getName());
    }

    @Test
    public void testFindByEmail_success() {
        User user = createTestUser("Bob", "bob@example.com", "token2");
        entityManager.persistAndFlush(user);

        User found = userRepository.findByEmail("bob@example.com");

        assertNotNull(found);
        assertEquals("Bob", found.getName());
    }

    @Test
    public void testExistsByEmail_true() {
        User user = createTestUser("Charlie", "charlie@example.com", "token3");
        entityManager.persistAndFlush(user);

        assertTrue(userRepository.existsByEmail("charlie@example.com"));
    }

    @Test
    public void testFindByToken_success() {
        User user = createTestUser("Dana", "dana@example.com", "token4");
        entityManager.persistAndFlush(user);

        User found = userRepository.findByToken("token4");

        assertNotNull(found);
        assertEquals("Dana", found.getName());
    }

    @Test
    public void testExistsByToken_true() {
        User user = createTestUser("Eva", "eva@example.com", "token5");
        entityManager.persistAndFlush(user);

        assertTrue(userRepository.existsByToken("token5"));
    }

    @Test
    public void testFindDiscoverableUsers_excludesIds() {
        User user1 = createTestUser("A", "a@example.com", "tokenA");
        User user2 = createTestUser("B", "b@example.com", "tokenB");
        User user3 = createTestUser("C", "c@example.com", "tokenC");

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);
        entityManager.flush();

        List<Long> excludedIds = List.of(user2.getId(), user3.getId());
        List<User> discoverable = userRepository.findDiscoverableUsers(user1.getId(), excludedIds);

        assertEquals(0, discoverable.size()); // Only user1 remains, but should be excluded too
    }

    @Test
    public void testFindWithCoursesById_emptyCourses() {
        User user = createTestUser("Frank", "frank@example.com", "token6");
        entityManager.persistAndFlush(user);

        Optional<User> found = userRepository.findWithCoursesById(user.getId());

        assertTrue(found.isPresent());
        assertEquals("Frank", found.get().getName());
        assertNotNull(found.get().getUserCourses());
    }
}
