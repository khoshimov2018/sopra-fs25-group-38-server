/* package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Course;
import ch.uzh.ifi.hase.soprafs24.entity.Match;
import ch.uzh.ifi.hase.soprafs24.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.HashSet;
import java.util.Set;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    // find userId who take selected courseId.
    public List<Long> findUserIdsEnrolledInAllCourses(List<Long> courseIds) {
        return courseRepository.findUserIdsByAllCourseIds(courseIds, (long) courseIds.size());
    }

    // find userId who have same availability.
    public List<Long> findUserIdsEnrolledInAllAvailability(List<String> availability) {
        return courseRepository.findUserIdsEnrolledInAllAvailability(availability, (long) availability.size());
    }    

    // show all courses.
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
}
 */