package ch.uzh.ifi.hase.soprafs24.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import ch.uzh.ifi.hase.soprafs24.entity.User;


@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
  User findByName(String name);

  User findByEmail(String email);

  boolean existsByEmail(String email);
  
  User findByToken(String token);
  
  boolean existsByToken(String token);

  @EntityGraph(attributePaths = {"userCourses", "userCourses.course"})
  Optional<User> findWithCoursesById(Long id);

  @Query("SELECT u FROM User u WHERE u.id NOT IN :excludedIds AND u.id <> :userId")
  List<User> findDiscoverableUsers(@Param("userId") Long userId, @Param("excludedIds") List<Long> excludedIds);

}

