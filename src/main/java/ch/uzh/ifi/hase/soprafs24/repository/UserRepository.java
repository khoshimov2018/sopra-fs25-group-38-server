package ch.uzh.ifi.hase.soprafs24.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.uzh.ifi.hase.soprafs24.entity.User;

import java.util.Optional;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
  User findByName(String name);

  User findByEmail(String email);

  boolean existsByEmail(String email);
  
  User findByToken(String token);
  
  boolean existsByToken(String token);

  @EntityGraph(attributePaths = {"userCourses", "userCourses.course"})
  Optional<User> findWithCoursesById(Long id);
}

