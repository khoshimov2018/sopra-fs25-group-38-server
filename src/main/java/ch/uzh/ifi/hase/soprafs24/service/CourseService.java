package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserAvailability;
import ch.uzh.ifi.hase.soprafs24.entity.Course;
import ch.uzh.ifi.hase.soprafs24.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    // find userId who have at least one same course
    public List<Long> findUserIdsByAnyCourseIds(List<Long> courseIds) {
        return courseRepository.findUserIdsByAnyCourseIds(courseIds);
    }
    
    // uses JPQL, allowing direct enum binding, not native query, required converting enums to Strings
    public List<Long> findUserIdsEnrolledInAllAvailability(List<UserAvailability> availability) {
        return courseRepository.findUserIdsEnrolledInAllAvailability(availability, (long) availability.size());
    }
    
    // show all courses.
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
}