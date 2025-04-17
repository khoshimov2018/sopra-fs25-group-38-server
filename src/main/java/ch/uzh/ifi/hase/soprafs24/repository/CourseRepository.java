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
    @Query(value = """
        SELECT "USER_ID"
        FROM "USER_COURSE"
        WHERE "COURSE_ID" IN (:courseIds)
    """, nativeQuery = true)
    List<Long> findUserIdsByAnyCourseIds(@Param("courseIds") List<Long> courseIds);
    
    @Query(value = """
        SELECT "USER_ID"
        FROM "USER_COURSE"
        WHERE "COURSE_ID" IN (:courseIds)
        GROUP BY "USER_ID"
        HAVING COUNT(DISTINCT "COURSE_ID") = :count
    """, nativeQuery = true)
    List<Long> findUserIdsByAllCourseIds(@Param("courseIds") List<Long> courseIds, @Param("count") Long count);

    // Using JPQL instead of native query to ensure proper binding of enums in IN clauses
    @Query("SELECT u.id FROM User u WHERE u.availability IN :availability GROUP BY u.id HAVING COUNT(u.id) = :count")
    List<Long> findUserIdsEnrolledInAllAvailability(@Param("availability") List<UserAvailability> availability,
                                                @Param("count") Long count);

    Optional<Course> findByCourseName(String courseName);
}
