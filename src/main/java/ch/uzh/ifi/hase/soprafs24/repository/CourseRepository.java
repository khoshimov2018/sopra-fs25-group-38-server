package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.UserAvailability;
import ch.uzh.ifi.hase.soprafs24.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query(value = "SELECT u.user_id FROM user_courses u WHERE u.course_id IN :courseIds GROUP BY u.user_id HAVING COUNT(u.course_id) = :count", nativeQuery = true)
    List<Long> findUserIdsByAllCourseIds(List<Long> courseIds, Long count);

    @Query(value = "SELECT u.id FROM users u WHERE u.availability IN :availability GROUP BY u.id HAVING COUNT(u.id) = :count", nativeQuery = true)
    List<Long> findUserIdsEnrolledInAllAvailability(@Param("availability") List<String> availability, @Param("count") Long count);
    
    Optional<Course> findByCourseName(String courseName);
}
